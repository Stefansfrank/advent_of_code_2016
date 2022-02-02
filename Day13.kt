package com.sf.aoc2016

class Day13 : Solver {

    // a simple path (current location and moves to get there
    data class Path(val loc: XY, val moves: Int)

    // helper struct to hand back both solutions
    data class Solution(var p1: Int, var p2: Int)

    // the breadth first search to get to the target
    private fun find(start: XY, target: XY, xdim: Int, ydim: Int, magic: Int):Solution {

        // some prep
        val sol = Solution(-1,-1)
        val st  = start.add(XY(1,1))  // coordinate correction as I pad the map
        val tgt = target.add(XY(1,1)) //      --- " ---

        // build the map
        val mp  = Mask(xdim + 2, ydim + 2)
        for (y in 0 until ydim)
            for (x in 0 until xdim)
                mp.msk[y+1][x+1] = ((x * x + 3 * x + 2 * x * y + y + y * y + magic).countOneBits() % 2 == 0)

        // keep track of potential paths and hold a cache with already visited locations
        // since I search all potential next steps (breadth first) every previous visit to
        // a location was better i.e. had less moves to get there
        var paths = mutableListOf( Path(st, 0) )
        val cache = mutableMapOf( paths[0].loc to true )

        // count the iterations so I can take stock at 50
        var cnt = 0
        while (cnt++ < 200) {

            // build a list of next paths to go through
            val newPaths = mutableListOf<Path>()
            for (pt in paths) {

                // go through all 4 possible neighbors
                for (nx in pt.loc.neighbors(false)) {

                    // win condition for part 1 (assumes part 2 has been solved along the way)
                    if (nx == tgt) return sol.apply { sol.p1 = pt.moves + 1 }

                    // check whether that new path is possible (no wall)
                    // and the new location has not yet been visited
                    val nPath = Path(nx, pt.moves + 1)
                    if (mp.msk[nx.y][nx.x] && cache[nPath.loc] == null) {
                        newPaths.add(nPath)
                        cache[nPath.loc] = true
                    }
                }
            }
            paths = newPaths

            // part 2 is easy as 'cache' already keeps track of all visited locations
            if (cnt == 50) sol.p2 = cache.size
        }
        return sol
    }

    override fun solve(file: String) {
        val inp = 1364 // my input
        val sol = find(XY(1,1), XY(31,39), 80, 80, inp)
        println("\nCubicle (31,39) can be reached in $red$bold${sol.p1}$reset moves.")
        println("Within the first 50 moves, $red$bold${sol.p2}$reset cubicles could be visited.")
    }
}