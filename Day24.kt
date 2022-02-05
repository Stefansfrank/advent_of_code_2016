package com.sf.aoc2016

class Day24 : Solver {

    // using my MapInt class from the 2D file
    data class Maze(val mz:MapInt, val start:XY, val numLoc: Int)

    // parsing into a 2D Int array with value 0 = wall, 1 = open, 2,3,4 ... = '0','1','2' ...
    private fun parse(inp:List<String>):Maze {
        val maze = MapInt(inp[0].length, inp.size)
        var start = XY(0,0)
        var numLoc = 0
        for (y in inp.indices) for ((x, c) in inp[y].withIndex()) {
            maze.mp[y][x] = when (c) {
                '#' -> 0
                '.' -> 1
                '0' -> { start = XY(x,y); numLoc += 1; 2 }
                else -> { numLoc += 1; c - '0' + 2 }
            }
        }
        return Maze(maze, start, numLoc)
    }

    // The state of the exploration with location, the amount of moves getting here and
    // 'vis' which is a bit representation of the locations that need to be visited.
    // After visiting all 8 locations, it's value will be 255 (2^8 -1)
    // NOTE: The state is constructed in a way that if I use it as a Map key for cached solutions
    // the amount of moves does not impact the equality test, only the current location and
    // the locations visited. Thus, I can test easily whether this state was already achieved with fewer moves
    data class State(val loc:XY, val vis:Int) {
        var moves = 0
    }

    // A BFS search keeping a cache of locations already visited (incl the collection of previously visited
    // special locations) so I can prune identical solutions with inferior move numbers and prevent cyclic movement
    // 'numLoc' is the number of special locations incl. '0' (8 in my input)
    private fun explore(maze: Maze, back: Boolean):Int {
        var states = listOf( State(maze.start, 1))
        val cache  = mutableMapOf( states[0] to 0 )
        val goal   = (1 shl maze.numLoc) - 1 // the goal for the visited number

        while (states.isNotEmpty()) {
            val newStates = mutableListOf<State>()
            for (st in states) {
                for (nl in st.loc.neighbors(false)) {
                    val c = maze.mz.get(nl)
                    if (c < 1) continue // wall
                    val vis = st.vis or if (c > 1) (1 shl (c-2)) else 0 // maintain the visited number
                    val nst = State(nl, vis).apply { this.moves = st.moves + 1 } // new state
                    val cch = cache[nst]
                    if (cch == null || cch > nst.moves) { // only add if not yet encountered with fewer moves
                        newStates.add(nst)
                        cache[nst] = nst.moves
                    }
                    // win condition -> visited is full for part 1, visited full and back at the start for part 2
                    if (nst.vis == goal) {
                        if (back) {
                            if (nst.loc == maze.start) return nst.moves
                        } else {
                            return nst.moves
                        }
                    }
                }
            }
            states = newStates
        }
        return -1
    }

    override fun solve(file: String) {
        val maze = parse(readTxtFile(file))
        print("\nThe robot visits all locations quickest in $red$bold")
        println("${explore(maze, false)}$reset moves")
        print("The shortest route that brings the robot back to the start is $red$bold")
        println("${explore(maze, true)}$reset moves long")
    }
}