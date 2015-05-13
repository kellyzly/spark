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
package org.apache.spark.crypto.random

import java.util.Random
import com.google.common.base.Preconditions
import java.security.SecureRandom
import org.apache.spark.crypto.NativeCodeLoader
import org.apache.spark.Logging
import org.apache.spark.crypto.Test2

/**
 * OpenSSL secure random using JNI.
 * This implementation is thread-safe.
 * <p/>
 *
 * If using an Intel chipset with RDRAND, the high-performance hardware
 * random number generator will be used and it's much faster than
 * {@link java.security.SecureRandom}. If RDRAND is unavailable, default
 * OpenSSL secure random generator will be used. It's still faster
 * and can generate strong random bytes.
 * <p/>
 * @see https://wiki.openssl.org/index.php/Random_Numbers
 * @see http://en.wikipedia.org/wiki/RdRand
 */
object OpensslSecureRandom extends Logging {
  @native
  def initSR()

  @native
  def initSR1()

  var nativeEnabled: Boolean = {
    if (NativeCodeLoader.isNativeCodeLoaded() &&
      NativeCodeLoader.buildSupportsOpenssl()) {
      try {
       // initSR1
        Test1.initTest1()
        Test2.initTest2()
        nativeEnabled = true
        true
      }
      catch {
        case t: Throwable => {
          logInfo(s"Failed to load Openssl SecureRandom $t")
        }
          false
      }
    } else {
      false
    }
  }
}

class OpensslSecureRandom extends Random {

  /** If native SecureRandom unavailable, use java SecureRandom */
  private var fallback: SecureRandom = null

  if (!OpensslSecureRandom.nativeEnabled) {
    fallback = new SecureRandom
  }

  /**
   * Generates a user-specified number of random bytes.
   * It's thread-safe.
   *
   * @param bytes the array to be filled in with random bytes.
   */
  override def nextBytes(bytes: Array[Byte]) {
    if (!OpensslSecureRandom.nativeEnabled || !nextRandBytes(bytes)) {
      fallback.nextBytes(bytes)
    }
  }

  override def setSeed(seed: Long) {
  }

  /**
   * Generates an integer containing the user-specified number of
   * random bits (right justified, with leading zeros).
   *
   * @param numBits number of random bits to be generated, where
   *                0 <= <code>numBits</code> <= 32.
   *
   * @return int an <code>int</code> containing the user-specified number
   *         of random bits (right justified, with leading zeros).
   */
  protected final override def next(numBits: Int): Int = {
    Preconditions.checkArgument(numBits >= 0 && numBits <= 32)
    val numBytes: Int = (numBits + 7) / 8
    val b: Array[Byte] = new Array[Byte](numBytes)
    var next: Int = 0
    nextBytes(b)
    var i: Int = 0
    for (i <- 0 until numBytes) {
      next = (next << 8) + (b(i) & 0xFF)
    }
    next >>> (numBytes * 8 - numBits)
  }


  @native
  def nextRandBytes(bytes: Array[Byte]): Boolean
}

