/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.apache.spark.crypto

import java.nio.ByteBuffer
import java.lang.String
import java.security.NoSuchAlgorithmException
import java.util.StringTokenizer
import javax.crypto.NoSuchPaddingException
import com.google.common.base.Preconditions
import org.apache.hadoop.util.NativeCodeLoader
import org.apache.spark.Logging

/**
* OpenSSL cipher using JNI.
* Currently only AES-CTR is supported. It's flexible to add
* other crypto algorithms/modes.
*/
object OpensslCipher extends Logging {
  val ENCRYPT_MODE: Int = 1
  val DECRYPT_MODE: Int = 0
  var loadingFailureReason: String = null
  try {
    var loadingFailure: String = null
    if (!NativeCodeLoader.buildSupportsOpenssl) {
      logDebug("Build does not support openssl")
      loadingFailure = "build does not support openssl."
    }
    else {
      initIDs
    }
  }
  catch {
    case t: Throwable => {
      loadingFailureReason = t.getMessage
      logDebug("Failed to load OpenSSL Cipher.", t)
    }
  }

  def getLoadingFailureReason: String = {
    loadingFailureReason
  }

  def getInstance(transformation: String): OpensslCipher = {
    val transform: OpensslCipher.Transform = tokenizeTransformation(transformation)
    val algMode: Int = AlgMode.get(transform.alg, transform.mode)
    val padding: Int = Padding.get(transform.padding)
    val context: Long = initContext(algMode, padding)
    new OpensslCipher(context, algMode, padding)
  }

  def tokenizeTransformation(transformation: String): OpensslCipher.Transform = {
    if (transformation == null) {
      throw new NoSuchAlgorithmException("No transformation given.")
    }
    val parts: Array[String] = new Array[String](3)
    var count: Int = 0
    val parser: StringTokenizer = new StringTokenizer(transformation, "/")
    while (parser.hasMoreTokens && count < 3) {
      parts(count) = parser.nextToken.trim
      count = count + 1
    }
    if (count != 3 || parser.hasMoreTokens) {
      throw new NoSuchAlgorithmException("Invalid transformation format: " + transformation)
    }
    new OpensslCipher.Transform(parts(0), parts(1), parts(2))
  }

  @native
  def initIDs

  @native
  def initContext(alg: Int, padding: Int): Long

  @native
  def getLibraryName: String

  /** Currently only support AES/CTR/NoPadding. */
  object AlgMode extends Enumeration {
    val AES_CTR = Value("AES_CTR")

    def get(algorithm: String, mode: String): Int = {
      try {
        AlgMode.withName(algorithm + "_" + mode).id
      }
      catch {
        case e: Exception => {
          throw new NoSuchAlgorithmException("Doesn't support" + " algorithm: " + algorithm + " " +
            "and  mode: " + mode)
        }
      }
    }
  }

  object Padding extends Enumeration {
    val NoPadding = Value("NoPadding")

    def get(padding: String): Int = {
      try {
        Padding.withName(padding).id
      }
      catch {
        case e: Exception => {
          throw new NoSuchPaddingException("Doesn't support padding: " + padding)
        }
      }
    }

  }

  /** Nested class for algorithm, mode and padding. */
  class Transform(algVal: String, modeVal: String, paddingVal: String) {
    val alg: String = algVal
    val mode: String = modeVal
    val padding: String = paddingVal

    def tokenizeTransformation(transformation: String): OpensslCipher.Transform = {
      if (transformation == null) {
        throw new NoSuchAlgorithmException("No transformation given.")
      }
      val parts: Array[String] = new Array[String](3)
      var count: Int = 0
      val parser: StringTokenizer = new StringTokenizer(transformation, "/")
      while (parser.hasMoreTokens && count < 3) {
        parts(count) = parser.nextToken.trim
        count = count + 1
      }
      if (count != 3 || parser.hasMoreTokens) {
        throw new NoSuchAlgorithmException("Invalid transformation format: " + transformation)
      }
      new OpensslCipher.Transform(parts(0), parts(1), parts(2))
    }
  }

}

class OpensslCipher(contextVal: Long, algVal: Int, paddingVal: Int) {
  var context: Long = contextVal
  val alg: Int = algVal
  val padding: Int = paddingVal

  def init(mode: Int, key: Array[Byte], iv: Array[Byte]) {
    context = init(context, mode, alg, padding, key, iv)
  }

  def update(input: ByteBuffer, output: ByteBuffer): Int = {
    checkState
    Preconditions.checkArgument(input.isDirect && output.isDirect)
    val len: Int = update(context, input, input.position, input.remaining, output,
      output.position, output.remaining)
    input.position(input.limit)
    output.position(output.position + len)
    len
  }

  def doFinal(output: ByteBuffer): Int = {
    checkState
    Preconditions.checkArgument(output.isDirect)
    val len: Int = doFinal(context, output, output.position, output.remaining)
    output.position(output.position + len)
    len
  }

  /** Forcibly clean the context. */
  def clean {
    if (context != 0) {
      clean(context)
      context = 0
    }
  }

  /** Check whether context is initialized. */
  def checkState {
    Preconditions.checkState(context != 0)
  }

  protected override def finalize {
    clean
  }

  @native
  def init(context: Long, mode: Int, alg: Int, padding: Int, key: Array[Byte],
           iv: Array[Byte]): Long

  @native
  def update(context: Long, input: ByteBuffer, inputOffset: Int, inputLength: Int,
             output: ByteBuffer, outputOffset: Int, maxOutputLength: Int): Int

  @native
  def doFinal(context: Long, output: ByteBuffer, offset: Int, maxOutputLength: Int): Int

  @native
  def clean(context: Long)
}
