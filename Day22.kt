package com.sf.aoc2016

class Day22 : Solver {

    data class Server(val size: Int, val used: Int, val loc: XY)

    private fun String.toServer():Server {
        val m = "/dev/grid/node-x(\\d+)-y(\\d+) +(\\d+)T +(\\d+)T".toRegex().find(this)
        if (m != null) return Server(m.groupValues[3].toInt(), m.groupValues[4].toInt(),
                                        XY(m.groupValues[1].toInt(), m.groupValues[2].toInt()))
        return Server(0,0, XY(-1,-1))
    }


    override fun solve(file: String) {
        val farm = readTxtFile(file).drop(2).map { it.toServer() }.toMutableList()

        // part 1 is a simple loop
        // (which could be optimized by sorting by size and availability)
        var sum = 0
        for (i1 in 0 until farm.size-1) {
            for (i2 in i1+1 until farm.size) {
                if (farm[i1].used > 0 && farm[i1].used <= farm[i2].size - farm[i2].used ) sum += 1
                if (farm[i2].used > 0 && farm[i2].used <= farm[i1].size - farm[i1].used ) sum += 1
            }
        }

        // for Part 2 I am printing the situation out first following the iconography suggested
        farm.sortBy { it.loc.x }
        farm.sortBy { it.loc.y }
        val xdim = farm[farm.size - 1].loc.x + 1
        val ydim = farm[farm.size - 1].loc.y + 1
        for (y in 0 until ydim) {
            for (x in 0 until xdim) {
                val sv = farm[ xdim * y + x]
                if (sv.loc.x == xdim - 1 && sv.loc.y == 0) { print("G"); continue }
                if (sv.loc.x == 0 && sv.loc.y == 0) { print("O"); continue }
                if (sv.used == 0) { print("_"); continue }
                if (sv.used > 200) { print("#"); continue }
                print(".")
            }
            println()
        }

        // from looking at the printout, the relevant coordinates are:
        val emptyX = 34   // the x coordinate of the empty server
        val emptyY = 26   // the y coordinate of the empty server
        val lastOpenX = 8 // the highest x-coordinate before the wall starts
        val width  = 38   // the overall width of the farm

        var sum2 = 0
        sum2 += emptyX - lastOpenX    // bring the empty node wide enough left in order to go up
        sum2 += emptyY                // bring the empty node up to the zero line
        sum2 += width - 2 - lastOpenX // bring the empty node next to corner (which is at 37,0)
        sum2 += (width - 2) * 5       // move data to the field next to 0,0 by circling empty around it
        sum2 += 1                     // do the last move to the left

        println("\nThere are $red$bold$sum$reset viable pairs in the farm")
        println("It will take $red$bold$sum2$reset moves to get the data out")
    }

}