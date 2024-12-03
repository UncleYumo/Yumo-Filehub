package cn.uncleyumo.filehub.mainapplication

import cn.uncleyumo.filehub.mainapplication.utils.AccessKeyUtil
import cn.uncleyumo.filehub.mainapplication.utils.JwtUtil
import cn.uncleyumo.utils.ColorPrinter
import cn.uncleyumo.utils.LogPrinter
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class MainApplicationTests {

    @Test
    fun contextLoads() {
        LogPrinter.warn("Unit test is running")
    }

//    @Test
    fun testJwt() {
        val map = mapOf("accessKey" to "uncleyumo")
        val token = JwtUtil.generateToken(map)
//        ColorPrinter.printlnCyanRed("token: $token")

        val t = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" +
                ".eyJjbGFpbXMiOnsiYWNjZXNzS2V5IjoidW5jbGV5dW1vIn0sImV4cCI6MTczMzI1ODEyMn0" +
                ".8yx3qiZy-89eCMu3qYr8QDD1yy750uDk5WAsCVtTxQM"

        val mapResult = JwtUtil.validateToken(t)
        ColorPrinter.printlnCyanRed("mapResult: $mapResult")
        val accessKey = mapResult["accessKey"]
        ColorPrinter.printlnCyanBlack("accessKey: $accessKey")

    }

//    @Test
    fun testAccessKeyEncryption() {
        val accessKey = "uncleyumo"
        if (AccessKeyUtil.validateFormat(accessKey)) {
            val s = AccessKeyUtil.encrypt(accessKey)
            ColorPrinter.printlnCyanRed("encrypted: $s")

            val original = AccessKeyUtil.decrypt(s)
            ColorPrinter.printlnCyanBlack("original: $original")
        } else {
            ColorPrinter.printlnCyanRed("invalid accessKey format")
        }
    }

}
