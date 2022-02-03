package com.sf.aoc2016

import java.security.MessageDigest
import kotlin.experimental.and

class Day14 : Solver {

    // using the Java library for MD5
    private fun md5(inp:String):ByteArray = MessageDigest.getInstance("MD5").digest(inp.toByteArray())

    // checks whether three subsequent half bytes are the same and return its value as Int
    // the code is slightly ugly but fast
    private fun check3(b1:Byte, b2:Byte):Int {
        val d1 = b1.and(-16).rotateRight(4); val d2 = b1.and(15)
        val d3 = b2.and(-16).rotateRight(4); val d4 = b2.and(15)
        if (d2 == d3 && (d1 == d2 || d3 == d4)) return d2.toInt()
        return -1
    }

    // checks whether three subsequent half bytes are the same and return its value as Int
    // the code is slightly ugly but fast
    private fun check5(b1:Byte, b2:Byte, b3:Byte):Int {
        val d1 = b1.and(-16).rotateRight(4); val d2 = b1.and(15)
        val d3 = b2.and(-16).rotateRight(4); val d4 = b2.and(15)
        val d5 = b3.and(-16).rotateRight(4); val d6 = b3.and(15)
        if (d2 == d3 && d3 == d4 && d4 == d5 && (d1 == d2 || d5 == d6)) return d2.toInt()
        return -1
    }

    // converting a byte into a string with two hex letters - trying to be fast
    private val bmp = listOf('0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f')
    private fun b2s(b:Byte):String {
        val d1 = b.and(-16).rotateRight(4); val d2 = b.and(15)
        return "${bmp[d1.toInt()]}${bmp[d2.toInt()]}"
    }

    // one key with the index of the 3 seq and the index of the corroborating 5 seq
    data class Key(val c: Int, val ix3: Long, var ix5: Long)

    // the actual find function with a cutoff amount of values (which I keep higher than 64
    // since I identify keys in sequence of their corroborating test, so the 64th key
    // could be corroborated after the 65th key ...)
    private fun find(cutoff: Int, salt: String, rpt: Int):List<Key> {

        var cnt  = 0L

        // I keep 16 lists of open keys waiting for corroboration for each hex digit
        val open = List(16){ mutableListOf<Key>() }
        val wins = mutableListOf<Key>()
        while (true) {

            // for speed reasons I keep both a byte array of the new hash and a string variant around
            var sHash = "$salt$cnt"
            var bHash = byteArrayOf()
            repeat(rpt) {
                bHash = md5(sHash)
                sHash = bHash.fold(""){ac, b -> ac + b2s(b) }
            }

            // try to find the sequences using byte manipulations
            // I do both 3/5 detection in one loop
            var found5 = false
            var found3 = false
            for (ix in 0 .. 13) { // MD5 byte hashes are always 16 bytes long
                if (!found5) {
                    val t5 = check5(bHash[ix], bHash[ix+1], bHash[ix+2])
                    if (t5 > -1) {
                        for (k in open[t5]) {
                            if (k.ix5 == -1L && (cnt - k.ix3) <= 1000) {
                                k.ix5 = cnt
                                wins.add(k)
                                if (wins.size >= cutoff) return wins
                            }
                        }
                        found5 = true
                    }
                }
                if (!found3) {
                    val t3 = check3(bHash[ix], bHash[ix+1])
                    if (t3 > -1) { open[t3].add(Key(t3, cnt, -1L)); found3 = true }
                }
            }
            // since I stopped in the loop above at 13, I need to test one more byte pair
            if (!found3) {
                val t3 = check3(bHash[14], bHash[15])
                if (t3 > -1) open[t3].add(Key(t3, cnt, -1L))
            }
            cnt++
        }
    }

    override fun solve(file: String) {
        val salt = "cuanljph" // my input
        print("\nThe 64th key defined with simple MD5 application occurs at $red$bold")
        println("${find(80, salt, 1).sortedBy { it.ix3 }[63].ix3}$reset")
        print("The 64th key defined with key stretching occurs at $red$bold")
        println("${find(80, salt, 2017).sortedBy { it.ix3 }[63].ix3}$reset")
    }
}