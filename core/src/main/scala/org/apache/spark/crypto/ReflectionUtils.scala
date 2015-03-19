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
//import java.lang.reflect.Constructor
//import java.util.concurrent.ConcurrentHashMap
//import java.lang.Class
//need to be removed
/**
  *
 */
//object ReflectionUtils {
//  /**
//   * Cache of constructors for each class. Pins the classes so they
//   * can't be garbage collected until ReflectionUtils can be collected.
//   */
//   var CONSTRUCTOR_CACHE: Map[Class[CryptoCodec], Constructor[CryptoCodec]] = new
//      ConcurrentHashMap[Class[CryptoCodec], Constructor[CryptoCodec]]
//  /** Create an object for the given class and initialize it from conf
//    *
//    * @param theClass class of which an object is created
//    * @return a new object
//    */
//  def newInstance(theClass: Class[CryptoCodec]): CryptoCodec = {
//    // def newInstance(theClass: Class[T]): T = {
//    var result: CryptoCodec = null
//    try {
//var meth: Constructor[CryptoCodec] = CONSTRUCTOR_CACHE.get(theClass).asInstanceOf[Constructor[T]]
//      if (meth == null) {
//        meth = theClass.getDeclaredConstructor(new Array[Class[CryptoCodec]](0))
//        meth.setAccessible(true)
//        CONSTRUCTOR_CACHE.put(theClass, meth)
//      }
//      result = meth.newInstance
//    }
//    catch {
//      case e: Exception => {
//        throw new RuntimeException(e)
//      }
//    }
//    result
//  }
//}
