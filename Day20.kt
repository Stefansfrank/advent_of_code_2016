package com.sf.aoc2016

import kotlin.math.max

class Day20 : Solver {

    data class IpRng(val from: Long, val to: Long)

    // parsing the input
    private fun String.parse():IpRng {
        val m = "(\\d+)-(\\d+)".toRegex().find(this)
        if (m != null) return IpRng(m.groupValues[1].toLong() , m.groupValues[2].toLong())
        return IpRng(0,0)
    }

    // simple loop through the ranges sorted by the 'from' value
    override fun solve(file: String) {
        val blocks = readTxtFile(file).map { it.parse() }.sortedBy { it.from }
        var high = 0L // the current highest blocked address
        var sum  = 0L // the running sum of unblocked addresses
        var found = false // indicator that the lowest has been found
        for (range in blocks) {
            if (range.from <= high + 1) high = max(range.to, high) else {

                // gap detected
                sum += range.from - high - 1
                if (!found) { found = true;
                    println("\nThe lowest IP that can pass is $red$bold${high+1}$reset")}
                high = range.to
            }
        }
        sum += 4294967295L - high // we might have a gap at the top
        println("A total of $red$bold$sum$reset IPs can pass the firewall")
    }
}