package com.sf.aoc2016

import java.math.BigInteger
import java.security.MessageDigest

class Day5 : Solver {

    // using the Java library for MD5
    private fun md5(inp:ByteArray) = MessageDigest.getInstance("MD5").digest(inp)

    // not sure anything can be optimized with MD5 other than a brut force loop
    // However, I keep the success condition in the ByteArray space of the MD5 algorithm and convert
    // to String only for potential solutions in order to keep the speed high. That gave an easy 5x boost
    override fun solve(file: String) {

        val inp = "ojvtpuvg".toByteArray() // my input

        var code1 = ""
        val code2 = CharArray(8){ '_' }
        var num = 0
        val b0 = 0.toByte()
        val mp = mutableMapOf<Int,Boolean>() // keeps track of which digits of code2 are found

        while (mp.size < 8) {                // part 2 is inherently slower to finish ...
            val hash = md5(inp + (num++).toString().toByteArray())
            if (hash[0] == b0 && hash[1] == b0 && hash[2].countLeadingZeroBits() > 3) {
                val sHash = BigInteger(1, hash).toString(16).padStart(32, '0')
                code1 += sHash[5]
                if (sHash[5] < '8') {
                    val ix = sHash[5] - '0'
                    if (mp[ix] == null) {
                        code2[ix] = sHash[6]
                        mp[ix] = true
                    }
                }
                println("${sHash.take(7)}... -> ${code1.take(8).padEnd(8,'_')} - ${String(code2)}")
            }
        }

        println("\nCode for the first door is: $red$bold${code1.take(8)}$reset")
        println("For the second door, the code is: $red$bold${String(code2)}$reset")
    }

}