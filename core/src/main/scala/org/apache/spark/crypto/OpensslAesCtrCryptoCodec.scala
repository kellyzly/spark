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
import com.google.common.base.Preconditions
import java.io.{Closeable, IOException}
import java.lang.String
import java.util.Random
import java.security.SecureRandom
import org.apache.spark.{SparkConf, Logging}

/**
 * Implement the AES-CTR crypto codec using JNI into OpenSSL.
 */
class OpensslAesCtrCryptoCodec extends AesCtrCryptoCodec with Logging {

  var conf: SparkConf = null
  var random: Random = null


  val loadingFailureReason: String = OpensslCipher.getLoadingFailureReason
  if (loadingFailureReason != null) {
    throw new RuntimeException(loadingFailureReason)
  }

  def setConf(conf: SparkConf) {
//    this.conf = conf
//    val klass: Class[_ <: Random] = conf.getClass(HADOOP_SECURITY_SECURE_RANDOM_IMPL_KEY,
// classOf[OsSecureRandom], classOf[Random])
//    try {
//      random = ReflectionUtils.newInstance(klass, conf)
//    }
//    catch {
//      case e: Exception => {
//        LOG.info("Unable to use " + klass.getName + ".  Falling back to " + "Java SecureRandom
// .", e)
//        this.random = new SecureRandom
//      }
//    }
    this.random = new SecureRandom
  }



  protected override def finalize {
    try {
      val r: Closeable = this.random.asInstanceOf[Closeable]
      r.close
    }
    catch {
      case e: ClassCastException => {
      }
    }
    super.finalize
  }

  def getConf: SparkConf = {
    conf
  }

  def createEncryptor: Encryptor = {
    new OpensslAesCtrCipher(OpensslCipher.ENCRYPT_MODE)
  }

  def createDecryptor: Decryptor = {
    new OpensslAesCtrCipher(OpensslCipher.DECRYPT_MODE)
  }

  def generateSecureRandom(bytes: Array[Byte]) {
    random.nextBytes(bytes)
  }

  class OpensslAesCtrCipher(mode: Int) extends Encryptor with Decryptor {

    final val cipher: OpensslCipher = OpensslCipher.getInstance(SUITE.name)
    var contextReset: Boolean = false

    def init(key: Array[Byte], iv: Array[Byte]) {
      Preconditions.checkNotNull(key)
      Preconditions.checkNotNull(iv)
      contextReset = false
      cipher.init(mode, key, iv)
    }

    /**
     * AES-CTR will consume all of the input data. It requires enough space in
     * the destination buffer to encrypt entire input buffer.
     */
    def encrypt(inBuffer: ByteBuffer, outBuffer: ByteBuffer) {
      process(inBuffer, outBuffer)
    }

    /**
     * AES-CTR will consume all of the input data. It requires enough space in
     * the destination buffer to decrypt entire input buffer.
     */
    def decrypt(inBuffer: ByteBuffer, outBuffer: ByteBuffer) {
      process(inBuffer, outBuffer)
    }

    def process(inBuffer: ByteBuffer, outBuffer: ByteBuffer) {
      try {
        val inputSize: Int = inBuffer.remaining
        val n: Int = cipher.update(inBuffer, outBuffer)
        if (n < inputSize) {
          contextReset = true
          cipher.doFinal(outBuffer)
        }
      }
      catch {
        case e: Exception => {
          throw new IOException(e)
        }
      }
    }

    def isContextReset: Boolean = {
      contextReset
    }
  }

}
