package com.appsferry.app

import org.junit.Test

import org.junit.Assert.*
import java.nio.charset.StandardCharsets

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val str = "asdfafawefaef"
        println("src str: $str")
        val byes = str.toByteArray(Charsets.ISO_8859_1)
        val utfStr = byes.toString(StandardCharsets.UTF_8)
        println("iso str: $utfStr")
        val urfBytes = utfStr.toByteArray(StandardCharsets.UTF_8)
        val recoverStr = urfBytes.toString(StandardCharsets.ISO_8859_1)
        println("recoverStr str: $recoverStr")
    }
}