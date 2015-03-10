package org.apache.spark.crypto
import java.io.BufferedInputStream
import java.io.DataInputStream
import java.io.IOException
import java.util.Arrays
import java.util.HashMap
import java.util.Map
import java.util.Random
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.io.{DataInputBuffer, DataOutputBuffer}
import org.apache.hadoop.util.NativeCodeLoader
import org.apache.hadoop.util.ReflectionUtils
import org.apache.spark.Logging
import org.scalatest.FunSuite

class CryptoCodecSuite extends FunSuite with Logging {
//   val key: Array[Byte] = Array(0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16)
//   val iv: Array[Byte] = Array(0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08)
//   val bufferSize: Int = 4096
//
//
//  test("TestJceAesCtrCryptoCodec") {
//    if (!"true".equalsIgnoreCase(System.getProperty("runningWithNative"))) {
//      LOG.warn("Skipping since test was not run with -Pnative flag")
//      assume(false)
//    }
//    if (!NativeCodeLoader.buildSupportsOpenssl) {
//      LOG.warn("Skipping test since openSSL library not loaded")
//      assume(false)
//    }
//    assert(OpensslCipher.getLoadingFailureReason==null)
//    cryptoCodecTest(conf, seed, 0, jceCodecClass, jceCodecClass)
//    cryptoCodecTest(conf, seed, count, jceCodecClass, jceCodecClass)
//    cryptoCodecTest(conf, seed, count, jceCodecClass, opensslCodecClass)
//  }
//
//  test("testOpensslAesCtrCryptoCodec") {
//    if (!"true".equalsIgnoreCase(System.getProperty("runningWithNative"))) {
//      LOG.warn("Skipping since test was not run with -Pnative flag")
//      assume(false)
//    }
//    if (!NativeCodeLoader.buildSupportsOpenssl) {
//      LOG.warn("Skipping test since openSSL library not loaded")
//      assume(false)
//    }
//    assert(OpensslCipher.getLoadingFailureReason == null )
//    cryptoCodecTest(conf, seed, 0, opensslCodecClass, opensslCodecClass)
//    cryptoCodecTest(conf, seed, count, opensslCodecClass, opensslCodecClass)
//    cryptoCodecTest(conf, seed, count, opensslCodecClass, jceCodecClass)
//  }
//
//   def cryptoCodecTest(conf: Configuration, seed: Int, count: Int, encCodecClass: String, decCodecClass: String) {
//    var encCodec: CryptoCodec = null
//    try {
//      encCodec = ReflectionUtils.newInstance(conf.getClassByName(encCodecClass), conf).asInstanceOf[CryptoCodec]
//    }
//    catch {
//      case cnfe: ClassNotFoundException => {
//        throw new IOException("Illegal crypto codec!")
//      }
//    }
//    logInfo("Created a Codec object of type: " + encCodecClass)
//    val data: DataOutputBuffer = new DataOutputBuffer
//    val generator: RandomDatum.Generator = new RandomDatum.Generator(seed)
//    {
//      var i: Int = 0
//      while (i < count) {
//        {
//          generator.next
//          val key: RandomDatum = generator.getKey
//          val value: RandomDatum = generator.getValue
//          key.write(data)
//          value.write(data)
//          i = i+1
//        }
//      }
//    }
//    logInfo("Generated " + count + " records")
//    val encryptedDataBuffer: DataOutputBuffer = new DataOutputBuffer
//    val out: CryptoOutputStream = new CryptoOutputStream(encryptedDataBuffer, encCodec, bufferSize, key, iv)
//    out.write(data.getData, 0, data.getLength)
//    out.flush
//    out.close
//    logInfo("Finished encrypting data")
//    var decCodec: CryptoCodec = null
//    try {
//      decCodec = ReflectionUtils.newInstance(conf.getClassByName(decCodecClass), conf).asInstanceOf[CryptoCodec]
//    }
//    catch {
//      case cnfe: ClassNotFoundException => {
//        throw new IOException("Illegal crypto codec!")
//      }
//    }
//    logInfo("Created a Codec object of type: " + decCodecClass)
//    val decryptedDataBuffer: DataInputBuffer = new DataInputBuffer
//    decryptedDataBuffer.reset(encryptedDataBuffer.getData, 0, encryptedDataBuffer.getLength)
//    var in: CryptoInputStream = new CryptoInputStream(decryptedDataBuffer, decCodec, bufferSize, key, iv)
//    val dataIn: DataInputStream = new DataInputStream(new BufferedInputStream(in))
//    val originalData: DataInputBuffer = new DataInputBuffer
//    originalData.reset(data.getData, 0, data.getLength)
//    var originalIn: DataInputStream = new DataInputStream(new BufferedInputStream(originalData))
//    {
//      var i: Int = 0
//      while (i < count) {
//        {
//          val k1: RandomDatum = new RandomDatum
//          val v1: RandomDatum = new RandomDatum
//          k1.readFields(originalIn)
//          v1.readFields(originalIn)
//          val k2: RandomDatum = new RandomDatum
//          val v2: RandomDatum = new RandomDatum
//          k2.readFields(dataIn)
//          v2.readFields(dataIn)
//          assert((k1 == k2) && (v1 == v2))
//          val m: Map[RandomDatum, String] = new HashMap[RandomDatum, String]
//          m.put(k1, k1.toString)
//          m.put(v1, v1.toString)
//          var result: String = m.get(k2)
//          assert(result.equals(k1.toString))
//          result = m.get(v2)
//          assert(result == v1.toString)
//          i=i+1
//        }
//      }
//    }
//    originalData.reset(data.getData, 0, data.getLength)
//    decryptedDataBuffer.reset(encryptedDataBuffer.getData, 0, encryptedDataBuffer.getLength)
//    in = new CryptoInputStream(decryptedDataBuffer, decCodec, bufferSize, key, iv)
//    originalIn = new DataInputStream(new BufferedInputStream(originalData))
//    var expected: Int = 0
//    do {
//      expected = originalIn.read
//      assert(expected == in.read)
//    } while (expected != -1)
//    originalData.reset(data.getData, 0, data.getLength)
//    decryptedDataBuffer.reset(encryptedDataBuffer.getData, 0, encryptedDataBuffer.getLength)
//    in = new CryptoInputStream(new TestCryptoStreams.FakeInputStream(decryptedDataBuffer), decCodec, bufferSize, key, iv)
//    val seekPos: Int = data.getLength / 3
//    in.seek(seekPos)
//    val originalInput: TestCryptoStreams.FakeInputStream = new TestCryptoStreams.FakeInputStream(originalData)
//    originalInput.seek(seekPos)
//    do {
//      expected = originalInput.read
//      assert( expected == in.read)
//    } while (expected != -1)
//    logInfo("SUCCESS! Completed checking " + count + " records")
//    testSecureRandom(encCodec)
//  }
//
//  /** Test secure random generator */
//   def testSecureRandom(codec: CryptoCodec) {
//    checkSecureRandom(codec, 16)
//    checkSecureRandom(codec, 32)
//    checkSecureRandom(codec, 128)
//  }
//
//   def checkSecureRandom(codec: CryptoCodec, len: Int) {
//    val rand: Array[Byte] = new Array[Byte](len)
//    val rand1: Array[Byte] = new Array[Byte](len)
//    codec.generateSecureRandom(rand)
//    codec.generateSecureRandom(rand1)
//    assert(rand.length == len)
//    assert(rand1.length == len)
//    assert(Arrays.equals(rand, rand1) == false)
//  }
//
//   var conf: Configuration = new Configuration
//   var count: Int = 10000
//   var seed: Int = new Random().nextInt
//   val jceCodecClass: String = "org.apache.hadoop.crypto.JceAesCtrCryptoCodec"
//   val opensslCodecClass: String = "org.apache.hadoop.crypto.OpensslAesCtrCryptoCodec"

}


