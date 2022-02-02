package com.sf.aoc2016

// This solution is able to solve both parts by brut force simulation using a breath first search
// and a cache in order to prune situations that have already occurred. I am sure I could improve the pruning
// with this problem and throw out solutions that go into the wrong direction.
//
// Runtime for Part 1: ~2 sec
//
// Part 2 can be easily solved by realizing that any part of equipment can be picked up from the first floor
// in 12 steps thus Part 2 = Part 1 + 2 * 12. When I tried this, I did get it right.
//
// However, I implemented brute force for part 2 as well to confirm but the computation time is 3 mins !!!
class Day11 : Solver {

    // 'things' contains both generators and chips. The first half of the list are generators, the second chips
    // with a constant offset of (number of pairs) between the matching two devices
    inner class Floor(val things:  MutableList<Boolean> = MutableList(2 * numP){ false }) {

        // check whether the floor is valid i.e. whether there is no unprotected chip
        fun valid(): Boolean {

            // no generators present
            if (things.subList(0,numP).none { (it) }) return true

            // any chip without it's associated generator
            if ((0 until numP).any { things[it + numP] && !things[it] }) return false

            return true
        }

        // hash used for keeping track of previous states
        fun hash() = things.fold("|"){ acc, flg -> acc + if (flg) "1" else "0" }

        // return all possible combinations of pairs or single to be moved
        fun pairs() = combinations(things.indices.filter { things[it] } + (-1), 2)

        // sets two indices to true or false (i.e. moves away / into this floor)
        fun set(pr: List<Int>, vl: Boolean) {
            if (pr[0] > -1) things[pr[0]] = vl
            if (pr[1] > -1) things[pr[1]] = vl
        }
    }

    // the overall state for the factory
    inner class FacState(val floors: List<Floor> = List(4){ Floor() }, var lift: Int = 0) {

        // debugging helper in order to print out the state
        private val names = (0 until numP).map{ "G${'A' + it}" } + (0 until numP).map{ "M${'A' + it}" }
        fun dump() {
            println("State - Moves: $moves - Elevator: $lift")
            println("---------------------------------------")
            floors.forEachIndexed { ix, f ->
                print("Fl. ${ix}:")
                f.things.forEachIndexed { jx, flg -> if (flg) print(" ${names[jx]}")}
                println()
            }
            println()
        }

        // the counter keeping track of the moves
        var moves = 0

        // the hash of the whole state for the cache
        fun hash() = floors.fold(lift.toString()){ acc, fl -> acc + fl.hash() }

        // produces a copy of this state
        fun copy():FacState {
            val new = FacState()
            new.moves = moves
            new.lift  = lift
            for (ix in 0..3)
                for (jx in 0 until numP*2)
                    new.floors[ix].things[jx] = floors[ix].things[jx]
            return new
        }

        // detects wins
        fun win() = (floors[3].things.fold(0){ ac, flg -> ac + if (flg) 1 else 0 } == 2*numP)
    }

    // creates the initial state from my input
    private fun initFac():FacState {
        val fac = FacState()
        fac.floors[0].things[0] = true; fac.floors[0].things[numP] = true
        (1 .. 4).forEach { fac.floors[1].things[it] = true; fac.floors[2].things[it + numP] = true }
        if (numP == 7) { // Part 2 additions
            fac.floors[0].things[5] = true; fac.floors[0].things[numP + 5] = true
            fac.floors[0].things[6] = true; fac.floors[0].things[numP + 6] = true
        }
        return fac
    }

    // The BFS fpr solving this
    private fun findBest(init:FacState):Int {
        var states = mutableListOf( init )

        // the cache for states already reached hashes the state hash against the moves it took to get to it
        val mp = mutableMapOf( states[0].hash() to states[0].moves )

        // look for next steps until a win is found
        //var cnt = 0
        while (true) {
            //states.forEach { it.dump() }

            // create a new list of states
            val newStates = mutableListOf<FacState>()
            for (st in states) {

                // combination of potential things to move
                val next = st.floors[st.lift].pairs()
                for (pr in next) {

                    // first check whether the floor is still valid after removing this pair
                    var nst = st.copy()
                    nst.floors[st.lift].set(pr, false)
                    if (!nst.floors[st.lift].valid()) continue

                    // try to move up
                    if (st.lift < 3) {
                        nst.lift = st.lift + 1
                        nst.floors[nst.lift].set(pr, true)
                        if (nst.floors[nst.lift].valid() && mp[nst.hash()] == null) {
                            nst.moves +=1
                            newStates.add(nst)
                            mp[nst.hash()] = nst.moves
                            if (nst.win()) return nst.moves
                        }
                    }

                    // refresh
                    nst = st.copy()
                    nst.floors[st.lift].set(pr, false)

                    // try to move down
                    if (st.lift > 0) {
                        nst.lift = st.lift - 1
                        nst.floors[nst.lift].set(pr, true)
                        if (nst.floors[nst.lift].valid() && mp[nst.hash()] == null) {
                            nst.moves +=1
                            newStates.add(nst)
                            mp[nst.hash()] = nst.moves
                            if (nst.win()) return nst.moves
                        }
                    }
                }
            }
            states = newStates
        }
    }

    var numP = 5
    override fun solve(file: String) {
        val p1 = findBest(initFac())
        print("\nThe shortest way to get everything on the upper floor takes ")
        println("$red$bold$p1$reset steps.")
        println("With two more generators and chips, it will take $red$bold${p1+24}$reset steps")
        // uncomment the following if you want to run the brute force solutions for part 2:
        // numP = 7
        // println("Brute force answer for part 2: $red$bold${findBest(initFac())}$reset steps")
    }
}