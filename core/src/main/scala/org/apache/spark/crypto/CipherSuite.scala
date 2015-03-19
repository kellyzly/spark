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

/**
 * There are two CipherSuite: 1. UNKNOWN  2. AES_CTR_NOPADDING
 */
case class CipherSuite(name: String, algoBlockSize: Int) {
  var unknownValue: Int = 0

  def setUnknownValue(unknown: Int) {
    this.unknownValue = unknown
  }

  def getUnknownValue(): Int = unknownValue


  override  def toString(): String = {
    var builder:StringBuilder  = new StringBuilder("{")
    builder.append("name: " + name)
    builder.append(", algorithmBlockSize: " + algoBlockSize)
    if (unknownValue != null) {
      builder.append(", unknownValue: " + unknownValue)
    }
    builder.append("}")
    builder.toString()
  }

  /**
   * Returns suffix of cipher suite configuration.
   * @return String configuration suffix
   */
  def getConfigSuffix(): String = {
    val parts = name.split("/")
    var suffix:StringBuilder = new StringBuilder()
    parts.foreach(part =>
      suffix.append(".").append(part.toLowerCase())

    )
    suffix.toString()
  }
}


object UNKNOWN extends CipherSuite("Unknown", 0)

object AES_CTR_NOPADDING extends CipherSuite("AES/CTR/NoPadding", 16)

object CipherSuite {
  def convert(name: String): CipherSuite = {
    if (name.equals(AES_CTR_NOPADDING.name)) {
      AES_CTR_NOPADDING
    } else if (name.equals(UNKNOWN.name)) {
      UNKNOWN
    } else {
      throw new IllegalArgumentException("Invalid cipher suite name: " + name)
    }
  }
}
