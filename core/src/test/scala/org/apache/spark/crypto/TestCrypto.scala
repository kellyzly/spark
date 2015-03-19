package org.apache.spark.crypto

import java.security.SecureRandom
import java.io.{ByteArrayInputStream, BufferedOutputStream, ByteArrayOutputStream}
import org.apache.spark.SparkConf

/**
 * Created with IntelliJ IDEA.
 * User: root
 * Date: 3/17/15
 * Time: 4:44 PM
 * To change this template use File | Settings | File Templates.
 */
object TestCrypto {

  def main(args: Array[String]) = {
  var key: Array[Byte] = Array[Byte](75, -103, 40, -25, -17, 64, 59, -68, -102, 73, -78, -6,
    60, 16, -103, 127)
    var i=0
    for(i <- 0 until 16){
      println(key(i))
    }
  }

  def printByteArray(key: Array[Byte])={
    for(i <- 0 until 16){
      println(key(i))
    }
  }


  def main1(args: Array[String]) = {
    val random: SecureRandom = new SecureRandom


       // val dataLen: Int = 10000000
       val dataLen: Int = 4

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
        val cis: CryptoInputStream = new CryptoInputStream(new ByteArrayInputStream(aos.toByteArray), codec, 1024, key, iv)


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
                println("decrypt failed:"+i)
              }
            }
  }

}
