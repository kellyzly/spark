package org.apache.spark.crypto

import java.io.{ByteArrayInputStream, BufferedOutputStream, ByteArrayOutputStream}
import java.security.SecureRandom

import org.apache.spark.{SparkConf, Logging}
import org.scalatest.FunSuite

/**
 * test JceAesCtrCryptoCodec
 */
class JceAesCtrCryptoCodecSuite extends FunSuite with Logging {

  test("TestCryptoCodecSuite"){
    val random: SecureRandom = new SecureRandom
    val dataLen: Int = 10000000
    val inputData: Array[Byte] = new Array[Byte](dataLen)
    val outputData: Array[Byte] = new Array[Byte](dataLen)
    random.nextBytes(inputData)
    // encrypt
    val sparkConf:SparkConf = new SparkConf()
    val codec: CryptoCodec =  new JceAesCtrCryptoCodec(sparkConf)
    val aos: ByteArrayOutputStream = new ByteArrayOutputStream
    val bos: BufferedOutputStream = new BufferedOutputStream(aos)
    val key: Array[Byte] = new Array[Byte](16)
    val iv: Array[Byte] = new Array[Byte](16)
    random.nextBytes(key)
    random.nextBytes(iv)
    val cos: CryptoOutputStream = new CryptoOutputStream(bos, codec, 1024, key, iv)
    cos.write(inputData, 0, inputData.length)
    cos.flush
    // decrypt
    val cis: CryptoInputStream = new CryptoInputStream(new ByteArrayInputStream(aos.toByteArray),
      codec, 1024, key, iv)
    var readLen: Int = 0
    var outOffset: Int = 0
    while (readLen < dataLen) {
      val n: Int = cis.read(outputData, outOffset, outputData.length - outOffset)
      if (n >= 0) {
        readLen += n
        outOffset += n
      }
    }
    var i: Int = 0
    for(i <- 0 until dataLen )
    {
      if (inputData(i) != outputData(i)) {
        logInfo(s"decrypt failed:$i")
      }
    }
  }
}