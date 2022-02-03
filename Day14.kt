package com.sf.aoc2016

import java.math.BigInteger
import java.security.MessageDigest

class Day14 : Solver {

    // using the Java library for MD5
    private fun md5(inp:String):String =
        BigInteger(1, MessageDigest.getInstance("MD5").digest(inp.toByteArray()))
            .toString(16).padStart(32, '0')

    // one hey with the index of the 3 seq and the index of the corroborating 5 index
    data class Key(val c: Char, val ix3: Long, var ix5: Long)

    // the actual find function with a cutoff amount of values (which I keep higher than 64
    // since I identify keys in sequence of their corroborating test, so the 64th key
    // could be corroborated after the 65th key ...
    private fun find(cutoff: Int, salt: String, rpt: Int):List<Key> {
        val re3 = "([0-9a-f])\\1\\1".toRegex()
        val re5 = "([0-9a-f])\\1\\1\\1\\1".toRegex()

        var cnt  = 0L

        // I keep 16 lists of open keys waiting for corroboration for each hex digit
        val open = List(16){ mutableListOf<Key>() }
        val wins = mutableListOf<Key>()
        while (true) {
            var hash = "$salt$cnt"
            repeat(rpt) { hash = md5(hash) }

            // try to match 5 using Regex
            var m = re5.find(hash)
            if (m != null) {
                val c = m.groupValues[1][0]

                // go through all open keys for this letter
                for (k in open[if (c.isDigit()) c - '0' else c - 'W']) {
                    if (k.ix5 == -1L && (cnt - k.ix3) <= 1000) {
                        k.ix5 = cnt
                        wins.add(k)
                        if (wins.size >= cutoff) return wins
                    }
                }
            }

            // try to match 3
            m = re3.find(hash)
            if (m != null) {
                val c = m.groupValues[1][0]
                open[if (c.isDigit()) c - '0' else c - 'W'].add(Key(c, cnt, -1L))
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