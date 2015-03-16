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

abstract class AesCtrCryptoCodec extends CryptoCodec {
  val CTR_OFFSET: Integer = 8
  val SUITE: CipherSuite = AES_CTR_NOPADDING
  val AES_BLOCK_SIZE: Integer = SUITE.algoBlockSize
  override def getCipherSuite(): CipherSuite = {
    SUITE
  }

  override def calculateIV(initIV: Array[Byte], counter: Long, IV: Array[Byte]) = {
    Preconditions.checkArgument(initIV.length == AES_BLOCK_SIZE)
    Preconditions.checkArgument(IV.length == AES_BLOCK_SIZE)

    System.arraycopy(initIV, 0, IV, 0, CTR_OFFSET)
    var l:Long = 0
    for (i <- 0 until 8) {
      l = ((l << 8) | (initIV(CTR_OFFSET + i) & 0xff))
    }
    l += counter
    IV(CTR_OFFSET + 0) = (l >>> 56).toByte
    IV(CTR_OFFSET + 1) = (l >>> 48).toByte
    IV(CTR_OFFSET + 2) = (l >>> 40).toByte
    IV(CTR_OFFSET + 3) = (l >>> 32).toByte
    IV(CTR_OFFSET + 4) = (l >>> 24).toByte
    IV(CTR_OFFSET + 5) = (l >>> 16).toByte
    IV(CTR_OFFSET + 6) = (l >>> 8).toByte
    IV(CTR_OFFSET + 7) = (l).toByte
  }
}

