package com.krissirk.mylibrary

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun `testJoinToString`() {
        val listString = mutableListOf<String>()
        for (i in 0 until 9) {
            listString.add(System.currentTimeMillis().toString())
        }
        val list = listString.joinToString { "\n" }
        println(list)
//        assertEquals(listString.size, countLines(list))
    }

    private fun countLines(str: String): Int {
        val lines = str.split("\r\n|\r|\n").toTypedArray()
        return lines.size
    }
}