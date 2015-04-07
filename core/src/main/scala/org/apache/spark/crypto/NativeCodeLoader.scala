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

import org.apache.spark.Logging

/**
 * A helper to load the native cryptostream code i.e. libcryptostream.so.
 * This handles the fallback to either the bundled libcryptostream-Linux-i386-32.so
 * or the default java implementations where appropriate.
 * need to be removed
 */
object NativeCodeLoader extends Logging {
  private var nativeCodeLoaded:Boolean = false
  // Try to load native spark library and set fallback flag appropriately
  logInfo("Trying to load the custom-built native-spark library...")
  try {
    System.loadLibrary("spark")
    logInfo("Loaded the native-spark library")
    nativeCodeLoaded = true
  }
  catch {
    case t: Throwable => {
      logInfo("Failed to load native-spark with error: " + t)
      logInfo(s"java.library.path=${System.getProperty("java.library.path")}")
    }
  }

  if (!nativeCodeLoaded) {
    logInfo("Unable to load native-spark library for your platform... " + "using builtin-java" +
      "classes where applicable")
  }


  /**
   * Returns true only if this build was compiled with support for openssl.
   */
  @native
  def buildSupportsOpenssl: Boolean

  //  @native
  //  def getLibraryName: String
}
