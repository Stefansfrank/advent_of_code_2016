package com.sf.aoc2016

class Day1 : Solver {

    // one segment of the directions
    data class Seg(val dir: Int, val dist: Int)

    // input parsing
    private fun parse(inp: List<String>) =
        inp[0].split(", ").map{ Seg( if (it[0] == 'R') 1 else 3, it.drop(1).toInt()) }

    override fun solve(file: String) {

        val segs = parse(readTxtFile(file))

        // Part 1
        var loc = XY(0,0)
        var dir = 0
        segs.forEach { dir = (dir + it.dir) % 4; loc = loc.mv(dir, it.dist) }
        println("\nYou end up at location (${loc.x},${loc.y}) - " +
                "$red$bold${loc.mDist()}$reset blocks from the start")

        // Part 2
        loc = XY(0,0)
        dir = 0
        val mp = mutableMapOf(XY(0,0) to true) // a map with key type XY keeps track of locations
        segLoop@for (seg in segs) {
            dir = (dir + seg.dir) % 4
            for (i in 1..seg.dist) { // every move needs to be split into one block moves now
                loc = loc.mv(dir)
                if (mp[loc] == null) mp[loc] = true else break@segLoop
            }
        }
        println("The first location familiar is (${loc.x},${loc.y}) - " +
                "$red$bold${loc.mDist()}$reset blocks from the start")
    }
}