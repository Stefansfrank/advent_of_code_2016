package com.sf.aoc2016

class Day18 : Solver {

    override fun solve(file: String) {

        // mapping this to a List<Boolean> with true = trap
        var line = readTxtFile(file)[0].map { it == '^' }

        println()
        for (n in listOf(40, 400_000)) {
            var sum = line.count { !it }
            repeat(n - 1) {
                // adding a padding
                line = listOf(false) + line + false

                // the core of this is that the four conditions boil down to the simplified condition:
                // it's a trap if the one above left and the one above right are different
                line = (0 until line.size - 2).map { line[it] != line[it + 2] }
                sum += line.count { !it }
            }
            println("After $n lines, $red$bold$sum$reset safe tiles have been incurred")
        }

    }
}