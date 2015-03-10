package org.apache.spark.crypto


import java.security.NoSuchAlgorithmException
import javax.crypto.NoSuchPaddingException
import javax.crypto.ShortBufferException
import java.nio.ByteBuffer
import org.scalatest.FunSuite

/**
  */
class OpensslCipherSuite extends FunSuite {
  val key: Array[Byte] = Array(0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
    0x08, 0x09, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16)
  val iv: Array[Byte] = Array(0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
    0x08, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08)

  test("TestGetInstance") {
    assume(OpensslCipher.getLoadingFailureReason == null)
    var cipher: OpensslCipher = OpensslCipher.getInstance("AES/CTR/NoPadding")
    assert(cipher != null)
    try {
      cipher = OpensslCipher.getInstance("AES2/CTR/NoPadding")
      fail("Should specify correct algorithm.")
    }
    catch {
      case e: NoSuchAlgorithmException => {
      }
    }
    try {
      cipher = OpensslCipher.getInstance("AES/CTR/NoPadding2")
      fail("Should specify correct padding.")
    }
    catch {
      case e: NoSuchPaddingException => {
      }
    }
  }

  // not understand this unit test
  test("TestUpdateArguments") {
    assume(OpensslCipher.getLoadingFailureReason == null)
    val cipher: OpensslCipher = OpensslCipher.getInstance("AES/CTR/NoPadding")
    assert(cipher != null)
    cipher.init(OpensslCipher.ENCRYPT_MODE, key, iv)
    var input: ByteBuffer = ByteBuffer.allocate(1024)
    var output: ByteBuffer = ByteBuffer.allocate(1024)
    try {
      cipher.update(input, output)
      fail("Input and output buffer should be direct buffer.")
    }
    catch {
      case e: IllegalArgumentException => {
        assert(e.getMessage.contains("Direct buffers are required"))
      }
    }
    input = ByteBuffer.allocateDirect(1024)
    output = ByteBuffer.allocateDirect(1000)
    try {
      cipher.update(input, output)
      fail("Output buffer length should be sufficient " + "to store output data")
    }
    catch {
      case e: ShortBufferException => {
        assert(e.getMessage.contains("Output buffer is not sufficient"))
      }
    }
  }

  // not understand this unit test
  test("TestDoFinalArguments") {
    assume(OpensslCipher.getLoadingFailureReason == null)
    val cipher: OpensslCipher = OpensslCipher.getInstance("AES/CTR/NoPadding")
    assert(cipher != null)
    cipher.init(OpensslCipher.ENCRYPT_MODE, key, iv)
    val output: ByteBuffer = ByteBuffer.allocate(1024)
    try {
      cipher.doFinal(output)
      fail("Output buffer should be direct buffer.")
    }
    catch {
      case e: IllegalArgumentException => {
        assert(e.getMessage.contains("Direct buffer is required"))
      }
    }
  }
}


