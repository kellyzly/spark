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

import com.google.common.base.Preconditions
import org.apache.hadoop.fs.Seekable

import java.io.InputStream
import java.nio.ByteBuffer

import sun.nio.ch.DirectBuffer
import sun.misc.Cleaner


object CryptoStreamUtils {
  /** Forcibly free the direct buffer. */
  def freeDB(buffer: ByteBuffer) {
    if (buffer.isInstanceOf[DirectBuffer]) {
      val bufferCleaner: Cleaner = (buffer.asInstanceOf[DirectBuffer]).cleaner
      bufferCleaner.clean
    }
  }

  /** Read crypto buffer size */
  def getBufferSize(): Int= {
    CommonConfigurationKeys.SPARK_SECURITY_CRYPTO_BUFFER_SIZE_DEFAULT
  }

  /** Check and floor buffer size */
  def checkBufferSize(codec: CryptoCodec, bufferSize: Int): Int = {
    Preconditions.checkArgument(bufferSize >= MIN_BUFFER_SIZE)
     bufferSize - bufferSize % codec.getCipherSuite.algoBlockSize
  }

  /**
   * If input stream is {@link org.apache.hadoop.fs.Seekable}, return it's
   * current position, otherwise return 0
   */
  def getInputStreamOffset(in: InputStream): Long = {
    if (in.isInstanceOf[Seekable]) {
       (in.asInstanceOf[Seekable]).getPos
    }
     0
  }

  def checkCodec(codec: CryptoCodec): Unit = {
    if (codec.getCipherSuite != AES_CTR_NOPADDING) {
      throw new RuntimeException("AES/CTR/NoPadding is required")
    }
  }

//  def  getRandomDevPath():String= {
//     System.getProperty(
//      CRYPTOSTREAM_RANDOM_DEVICE_FILE_PATH_KEY,
//      CRYPTOSTREAM_RANDOM_DEVICE_FILE_PATH_DEFAULT)
//  }

  val MIN_BUFFER_SIZE: Int = 512
}
