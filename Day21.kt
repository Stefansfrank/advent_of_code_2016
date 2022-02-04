package com.sf.aoc2016

import java.util.*

class Day21 : Solver {

    // one operation
    data class Op(val opc: Int, val num: List<Int>, val let: List<Char>)

    // parsing function
    private fun String.toOp():Op {
        val cmds = listOf("swap position (\\d+) with position (\\d+)", "swap letter (\\w+) with letter (\\w+)",
            "rotate left (\\d+) steps?", "rotate right (\\d+) steps?", "rotate based on position of letter (\\w+)",
            "reverse positions (\\d+) through (\\d+)", "move position (\\d+) to position (\\d+)")
        for ((ix, c) in cmds.withIndex()) {
            val m = c.toRegex().find(this)
            if (m != null) return when (ix) {
                0 -> Op(0, listOf(m.groupValues[1].toInt(), m.groupValues[2].toInt()), listOf())
                1 -> Op(1, listOf(), listOf(m.groupValues[1][0], m.groupValues[2][0]))
                2 -> Op(2, listOf(m.groupValues[1].toInt()), listOf())
                3 -> Op(3, listOf(m.groupValues[1].toInt()), listOf())
                4 -> Op(4, listOf(), listOf(m.groupValues[1][0]))
                5 -> Op(5, listOf(m.groupValues[1].toInt(), m.groupValues[2].toInt()), listOf())
                6 -> Op(6, listOf(m.groupValues[1].toInt(), m.groupValues[2].toInt()), listOf())
                else -> Op(-1, listOf(), listOf())
            }
        }
        println("Could not parse: $this")
        return Op(-1, listOf(), listOf())
    }

    // applying an operation
    private fun MutableList<Char>.exe(op: Op):MutableList<Char> {
        when (op.opc) {
            0 -> this[op.num[0]] = this[op.num[1]].also { this[op.num[1]] = this[op.num[0]] }
            1 -> { val i1 = this.indexOf(op.let[0])
                val i2 = this.indexOf(op.let[1])
                this[i1] = this[i2].also { this[i2] = this[i1] }
            }
            2 -> Collections.rotate(this, -op.num[0])
            3 -> Collections.rotate(this, op.num[0])
            4 -> { val ix = this.indexOf(op.let[0])
                Collections.rotate(this, 1 + ix + if (ix > 3) 1 else 0 )
            }
            5 -> this.subList(op.num[0], op.num[1]+1).reverse()
            // moving of one element to another is actually the same then rotating a sublist between the two by +/-1
            6 -> if (op.num[1] > op.num[0]) Collections.rotate(this.subList(op.num[0],op.num[1]+1), -1)
            else Collections.rotate(this.subList(op.num[1],op.num[0]+1), 1)
        }
        return this
    }

    // applying the inverse of an operation
    private fun MutableList<Char>.rev(op: Op):MutableList<Char> {
        when (op.opc) {

            // swaps are inverse of itself so the first two operations are unchanged
            0 -> this[op.num[0]] = this[op.num[1]].also { this[op.num[1]] = this[op.num[0]] }
            1 -> { val i1 = this.indexOf(op.let[0])
                val i2 = this.indexOf(op.let[1])
                this[i1] = this[i2].also { this[i2] = this[i1] }
            }

            // for the next two (rotation) operations, I just reverse the direction
            2 -> Collections.rotate(this, op.num[0])
            3 -> Collections.rotate(this, -op.num[0])

            // the rotation based on a letter index, I rotate back one by one until I end up
            // in a situation that would have required the amount of rotations I reversed
            4 -> { var cnt = 0; while (true) {
                Collections.rotate(this, -1)
                val ix = this.indexOf(op.let[0])
                if (++cnt == 1 + ix + if (ix > 3) 1 else 0) break
            } }

            // reversions are inverse of themselves and unchanged
            5 -> this.subList(op.num[0], op.num[1]+1).reverse()

            // since the move of on element is actually a rotation of a sublist I just rverse the direction
            6 -> if (op.num[1] > op.num[0]) Collections.rotate(this.subList(op.num[0],op.num[1]+1), 1)
            else Collections.rotate(this.subList(op.num[1],op.num[0]+1), -1)
        }
        return this
    }

    override fun solve(file: String) {

        val code = readTxtFile(file).map{ it.toOp() }

        var seed = "abcdefgh".toCharArray().toMutableList()
        code.forEach { seed.exe(it) }
        print("\nPassword$green abcdefgh$reset would scramble to $red$bold")
        println("${seed.joinToString(separator = "")}$reset")

        seed = "fbgdceah".toCharArray().toMutableList()
        code.reversed().forEach { seed.rev(it) }
        print("Scramble$green fbgdceah$reset results from password $red$bold")
        println("${seed.joinToString(separator = "")}$reset")

    }
}