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
//
import java.util.Random
import java.io.{IOException, FileInputStream, File, Closeable}
//import java.lang.{String, RuntimeException}
import org.apache.spark.{SparkConf, Logging}
import org.apache.spark.crypto.CommonConfigurationKeys.SPARK_SECURITY_SECURE_RANDOM_DEVICE_FILE_PATH_KEY
import org.apache.spark.crypto.CommonConfigurationKeys.SPARK_SECURITY_SECURE_RANDOM_DEVICE_FILE_PATH_DEFAULT
//
///**
// * A Random implementation that uses random bytes sourced from the
// * operating system.
// * need to be removed
//*/
//
//class OsSecureRandom(conf:SparkConf) extends Random with Closeable with Logging {
//  @transient
//  var stream: FileInputStream = null
//  val reservoir: Array[Byte] = new Array[Byte](RESERVOIR_LENGTH)
//  var pos: Int = reservoir.length
//  val RESERVOIR_LENGTH: Int = 8192
//  var randomDevPath: String = conf.get(
//    SPARK_SECURITY_SECURE_RANDOM_DEVICE_FILE_PATH_KEY,
//    SPARK_SECURITY_SECURE_RANDOM_DEVICE_FILE_PATH_DEFAULT)
//  val randomDevFile: File = new File(randomDevPath)
//  try {
//    close
//    this.stream = new FileInputStream(randomDevFile)
//  }
//  catch {
//    case e: IOException => {
//      throw new RuntimeException(e)
//    }
//  }
//  try {
//    fillReservoir(0)
//  }
//  catch {
//    case e: RuntimeException => {
//      close
//      throw e
//    }
//  }
//
//  def fillReservoir(min: Int) {
//    if (pos >= reservoir.length - min) {
//      try {
//        IOUtils.readFully(stream, reservoir, 0, reservoir.length)
//      }
//      catch {
//        case e: IOException => {
//          throw new RuntimeException("failed to fill reservoir", e)
//        }
//      }
//      pos = 0
//    }
//  }
//
//  override def nextBytes(bytes: Array[Byte]) {
//    var off: Int = 0
//    var n: Int = 0
//    while (off < bytes.length) {
//      fillReservoir(0)
//      n = Math.min(bytes.length - off, reservoir.length - pos)
//      System.arraycopy(reservoir, pos, bytes, off, n)
//      off += n
//      pos += n
//    }
//  }
//
//  protected override def next(nbits: Int): Int = {
//    fillReservoir(4)
//    var n:Int =0
//    var i:Int =0
//    for (i <- 0 until 4) {
//      n = ((n << 8) | (reservoir(pos) & 0xff))
//      pos = pos + 1
//    }
//    n & (0xffffffff >> (32 - nbits))
//  }
//
//  def close {
//    if (stream != null) {
//      IOUtils.cleanup(stream)
//      stream = null
//    }
//  }
//
//
//}


