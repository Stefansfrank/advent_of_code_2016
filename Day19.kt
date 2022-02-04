package com.sf.aoc2016

class Day19 : Solver {

    override fun solve(file: String) {

        val num = 3001330

        // a super simple solution using a list where the index is the elf number
        // and the content is the next number which is modified as the next elf is robbed
        // of their present
        val elves = MutableList(num){ (it + 1) % num }
        var curr = 1
        while (elves[curr] != curr) {
            elves[curr] = elves[elves[curr]]
            curr = elves[curr]
        }
        println("\nThe lucky elf after Pert 1 rules is: $red$bold$curr$reset")

        // on this one I use two double ended queues in order to represent the two half's
        // of the circle and easily remove the element opposite
        // the current elf is always the one at first position in first
        val first  = ArrayDeque((1..num / 2).toList())
        val second = ArrayDeque((num / 2 + 1..num).toList())

        while (second.size > 0) {

            if (first.size > second.size) first.removeLast() // odd number of elves
            else second.removeFirst()                        // even number of elves

            // circle around
            second.addLast(first.removeFirst())
            first.addLast(second.removeFirst())
        }

        println("With Part 2 rules, elf $red$bold${first[0]}$reset is the winner")
    }
}