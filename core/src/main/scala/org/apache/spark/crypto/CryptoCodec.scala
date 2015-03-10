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

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.util.ReflectionUtils


import java.lang.{String, ClassCastException, Class}

import org.apache.hadoop.fs.CommonConfigurationKeysPublic.HADOOP_SECURITY_CRYPTO_CIPHER_SUITE_DEFAULT
import org.apache.hadoop.fs.CommonConfigurationKeysPublic.HADOOP_SECURITY_CRYPTO_CIPHER_SUITE_KEY
import org.apache.hadoop.fs.CommonConfigurationKeysPublic.HADOOP_SECURITY_CRYPTO_CODEC_CLASSES_KEY_PREFIX
import org.apache.spark.Logging

abstract case class CryptoCodec() {
  def getCipherSuite(): CipherSuite

  def calculateIV(initIV: Array[Byte], counter: Long, IV: Array[Byte])

  def createEncryptor: Encryptor


  def createDecryptor: Decryptor

  def generateSecureRandom(bytes: Array[Byte])
}

object CryptoCodec extends Logging {
  def getInstance(conf: Configuration): CryptoCodec = {
    var name: String = conf.get(HADOOP_SECURITY_CRYPTO_CIPHER_SUITE_KEY,
      HADOOP_SECURITY_CRYPTO_CIPHER_SUITE_DEFAULT)
    getInstance(conf, CipherSuite.convert(name))
  }

  /**
   * Get crypto codec for specified algorithm/mode/padding.
   *
   * @param conf
   * the configuration
   * @param cipherSuite
   * algorithm/mode/padding
   * @return CryptoCodec the codec object. Null value will be returned if no
   *         crypto codec classes with cipher suite configured.
   */
  def getInstance(conf: Configuration, cipherSuite: CipherSuite): CryptoCodec = {
    val klasses: List[Class[_ <: CryptoCodec]] = getCodecClasses(conf, cipherSuite)
    if (klasses == null) {
      return null
    }
    var codec: CryptoCodec = null
    for (klass <- klasses) {
      try {
        val c: CryptoCodec = ReflectionUtils.newInstance(klass, conf)
        if (c.getCipherSuite.name == cipherSuite.name) {
          if (codec == null) {
            logDebug(s"Using crypto codec $klass.getName.")
            codec = c
          }
        }
        else {
          logDebug(s"Crypto codec $klass.getName doesn't meet the cipher suite $cipherSuite" +
            s".getName.")
        }
      }
      catch {
        case e: Exception => {
          logDebug(s"Crypto codec $klass.getName is not available.")
        }
      }
    }
    return codec
  }

  def getCodecClasses(conf: Configuration, cipherSuite: CipherSuite): List[Class[_ <:
    CryptoCodec]] = {
    val result = List()
    val configName: String = HADOOP_SECURITY_CRYPTO_CODEC_CLASSES_KEY_PREFIX + cipherSuite
      .getConfigSuffix
    val codecString: String = conf.get(configName)
    /**
     * <name>hadoop.security.crypto.codec.classes.aes.ctr.nopadding</name>
           <value>org.apache.hadoop.crypto.OpensslAesCtrCryptoCodec,
    org.apache.hadoop.crypto.JceAesCtrCryptoCodec</value>
           codecString:  org.apache.hadoop.crypto.OpensslAesCtrCryptoCodec,
    org.apache.hadoop.crypto.JceAesCtrCryptoCodec
     */
    if (codecString == null) {
      logDebug("No crypto codec classes with cipher suite configured.")
      return null
    }

    val codecArray: Array[String] = codecString.trim.split(",")
    for (c <- codecArray) {
      try {
        val cls: Class[_] = conf.getClassByName(c)
          cls.asSubclass(classOf[CryptoCodec])::result
      }
      catch {
        case e: ClassCastException => {
          logDebug(s"Class $c is not a CryptoCodec.")
        }
        case e: ClassNotFoundException => {
          logDebug(s"Crypto codec $c not found.")
        }
      }
    }
    result
  }
}
