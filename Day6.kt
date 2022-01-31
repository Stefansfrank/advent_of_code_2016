package com.sf.aoc2016

class Day6 : Solver {

    // creates letter distribution tables (as IntArray(26)) for each position in an input string
    private fun parse(inp: List<String>):List<IntArray> {
        val len = inp[0].length
        val cnt = List(len){ IntArray(26){ 0 } }
        for (ln in inp) ln.forEachIndexed { ix, c -> cnt[ix][c - 'a'] += 1 }
        return cnt
    }

    // simply determining the max and min from the tables created in 'parse' solves this day
    override fun solve(file: String) {
        val cnt = parse(readTxtFile(file))

        // Part 1
        print("\nThe message encoded by max letters is: $red$bold")
        cnt.forEach { val max = it.maxOrNull()!!; print('a' + it.indexOf(max)) }

        // Part 2
        print("\n${reset}The message encoded by min letters is: $red$bold")
        cnt.forEach { val min = it.minOrNull()!!; print('a' + it.indexOf(min)) }
        println(reset)
    }

}