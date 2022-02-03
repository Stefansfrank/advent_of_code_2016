package com.sf.aoc2016

class Day16 : Solver {

    // build data to size 'size' and compute checksum
    private fun check(seed: String, size: Int):String {

        var data = List(seed.length) { seed[it] == '1'}

        // build data
        while (data.size < size) {
            val b = List(data.size) { !data[data.size - it - 1] }
            data = data + false + b
        }
        data = data.take(size)

        // check sum
        var check = data
        do {
            check = List(check.size/2){ check[it*2] == check[it*2+1] }
        } while (check.size % 2 == 0)

        return check.joinToString(separator = "") { if (it) "1" else "0" }
    }

    override fun solve(file: String) {
        val inp ="01111010110010011"
        println("\nChecksum for the disk sized 272: $red$bold${check(inp,272)}$reset")
        println("Checksum for the disk sized 35651584: $red$bold${check(inp,35651584)}$reset")

    }
}