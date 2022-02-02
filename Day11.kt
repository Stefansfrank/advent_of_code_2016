package com.sf.aoc2016

// This solution is able to solve both parts by brut force simulation using a breath first search
// and a cache in order to prune situations that have already occurred. One pruning I do is that
// the hash identifying the state is blind to which pair of generator and chip is in a given state
// thus I do not try the same solution with different pairs.
//
// Runtime for both pairs: ~1.5 sec
//
// Part 2 should be simple by math, but I wasn't completely sure, so I let the brut force run
// Every additional pair on level 1 should be in level 4 after 12 steps
// thus Part 2 = Part 1 + 2 * 12. When I tried this, I did get it right.
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

        // return all possible combinations of pairs or singles to be moved
        fun pairs() = combinations(things.indices.filter { things[it] } + (-1), 2)

        // sets two indices to true or false (i.e. moves away / into this floor)
        fun set(pr: List<Int>, vl: Boolean) {
            if (pr[0] > -1) things[pr[0]] = vl
            if (pr[1] > -1) things[pr[1]] = vl
        }
    }

    // the overall state for the factory
    inner class FacState(val floors: List<Floor> = List(4){ Floor() }, var lift: Int = 0) {

        // the counter keeping track of the amount of moves
        var moves = 0

        // the hash of the whole state (used to identify whether a situation has already been seen and can be pruned)
        // first I convert each pair into an int with first digit = floor of generator, second = floor of chip
        // the subsequent sorting makes the hash independent of which particular pair is in a given state
        // and thus prunes a lot of solutions that were structurally identical but with different pairs of equipment
        fun hash() = (0 until numP).map{ dir[it] + dir[it + numP]*10 }
            .sorted().fold(lift.toString()){ ac, p -> "$ac|$p" }

        // produces a copy of this state
        fun copy():FacState {
            val new = FacState()
            new.moves = moves
            new.lift  = lift
            new.dir   = dir.toMutableList()
            for (ix in 0..3)
                for (jx in 0 until numP*2)
                    new.floors[ix].things[jx] = floors[ix].things[jx]
            return new
        }

        // detects wins
        fun win() = !dir.any { it != 3 }

        // directory for all things (shows which floor each thing is - redundant with the Boolean
        // representation per floor but helps pruning situations that are equal but with different pairs
        var dir = MutableList(numP * 2){-1}
        fun setDir(pr:List<Int>, flr:Int) = pr.forEach { if (it > -1) dir[it] = flr }
    }

    // creates the initial state from my input
    private fun initFac():FacState {
        val fac = FacState()
        fac.floors[0].things[0] = true; fac.dir[0] = 0
        fac.floors[0].things[numP] = true; fac.dir[numP] = 0
        (1 .. 4).forEach {
            fac.floors[1].things[it] = true; fac.dir[it] = 1
            fac.floors[2].things[it + numP] = true; fac.dir[it + numP] = 2
        }
        if (numP == 7) { // Part 2 additions
            fac.floors[0].things[5] = true; fac.dir[5] = 0
            fac.floors[0].things[numP + 5] = true; fac.dir[5 + numP] = 0
            fac.floors[0].things[6] = true; fac.dir[6] = 0
            fac.floors[0].things[numP + 6] = true; fac.dir[6 + numP] = 0
        }
        return fac
    }

    // The breadth first search trying all potential next moves until a win is found
    private fun findBest(init:FacState):Int {
        var states = mutableListOf( init )

        // the cache for states already reached hashes the state hash against the moves it took to get to it
        val mp = mutableMapOf( states[0].hash() to true )

        // look for next steps until a win is found
        while (true) {

            // create a new list of states
            val newStates = mutableListOf<FacState>()
            for (st in states) {

                // go through potential combinations of one or two things to move up or down
                val next = st.floors[st.lift].pairs()
                for (pr in next) {

                    // first check whether this floor is still valid after removing this pair
                    var nst = st.copy()
                    nst.floors[st.lift].set(pr, false)
                    if (!nst.floors[st.lift].valid()) continue

                    // try to move up
                    if (st.lift < 3) {

                        // move the equipment
                        nst.lift = st.lift + 1
                        nst.floors[nst.lift].set(pr, true)
                        nst.setDir(pr, nst.lift)
                        val hsh = nst.hash()

                        // since I do breadth first, any identical solution encountered
                        // before has less or equal moves, so I check only for
                        // previous existence and validity of the new state
                        if (nst.floors[nst.lift].valid() && mp[hsh] == null) {
                            nst.moves +=1
                            newStates.add(nst)
                            mp[hsh] = true
                            if (nst.win()) return nst.moves
                        }
                    }

                    // try to move down
                    if (st.lift > 0) {

                        // a new copy of the state after trying to move up
                        nst = st.copy()
                        nst.floors[st.lift].set(pr, false)

                        nst.lift = st.lift - 1
                        nst.floors[nst.lift].set(pr, true)
                        nst.setDir(pr, nst.lift)
                        val hsh = nst.hash()

                        // since I do breadth first, any identical solution encountered
                        // before has less or equal moves, so I check only for
                        // previous existence and validity of the new state
                        if (nst.floors[nst.lift].valid() && mp[hsh] == null) {
                            nst.moves +=1
                            newStates.add(nst)
                            mp[hsh] = true
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
        println("With two more generators and chips, it should take $red$bold${p1+24}$reset steps")
        numP = 7
        println("Brute force confirms that it takes $red$bold${findBest(initFac())}$reset steps")
    }
}