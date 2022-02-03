package com.sf.aoc2016

class Day15 : Solver {

    // One disc with it's frequency and starting position
    data class Disc(val freq: Int, val init: Int)

    // preparing the discs from my puzzle input
    private fun input():MutableList<Disc> {
        val discs = mutableListOf<Disc>()
        discs.add(Disc(17,1))
        discs.add(Disc(7,0))
        discs.add(Disc(19,2))
        discs.add(Disc(5,0))
        discs.add(Disc(3,0))
        discs.add(Disc(13,5))
        return discs
    }

    override fun solve(file: String) {

        var tm = 0 // button press time
        val discs = input()
        var found = false
        while (!found) {

            // use the cycle to calculate the position of each disv
            if (discs.foldIndexed(true) { ix, ac, d ->
                    ac && (tm + ix + 1 + d.init) % d.freq == 0 }) {
                print("\nWith ${discs.size} discs, pressing the button at $red$bold")
                println("$tm$reset sec has the ball pass")
                found = true
            }

            // loop through starting times
            tm++
        }

        // add part 2 disc and run identical code
        discs.add(Disc(11,0))
        while (true) {
            if (discs.foldIndexed(true) { ix, ac, d ->
                    ac && (tm + ix + 1 + d.init) % d.freq == 0 }) {
                print("With ${discs.size} discs, pressing the button at $red$bold")
                println("$tm$reset sec has the ball pass")
                return
            }
            tm++
        }
    }
}