package com.sf.aoc2016

class Day12 : Solver {

    // the data structure for operation lines
    data class Op(val opc: Int, val vl: List<Int>, val rg: List<Char>)

    // factory for operations from string input used to parse the given program
    // note that I create two different opcodes for "cpy" and "jnz" depending on whether they are using
    // direct values or indirect / register based values thus I have 6 total operations supported
    fun makeOp(inp: String):Op {
        val ln = inp.split(' ')
        return when (ln[0]) {
            "cpy" ->
                if ( ln[1][ln[1].length-1].isDigit() ) Op( 0, listOf(ln[1].toInt()), listOf(ln[2][0]) )
                else Op( 1, listOf(), listOf(ln[1][0], ln[2][0]) )
            "inc" -> Op( 2, listOf(), listOf(ln[1][0]) )
            "dec" -> Op( 3, listOf(), listOf(ln[1][0]) )
            "jnz" ->
                if ( ln[1][ln[1].length-1].isDigit() ) Op( 4, listOf(ln[1].toInt(), ln[2].toInt()), listOf() )
                else Op( 5, listOf(ln[2].toInt()), listOf(ln[1][0]) )
            else  -> Op(-1, listOf(), listOf())
        }
    }

    // the actual cpu
    inner class CPU(val regs: MutableList<Int> = mutableListOf(0, 0, 0, 0)) {

        // memory
        var mem = mutableListOf<Op>()

        // load a program from text lines into memory
        fun load(inp: List<String>):CPU { mem = inp.map { makeOp(it) }.toMutableList(); return this }

        // run the program from memory starting with the command given
        fun run (from: Int):CPU {
            var ic = from
            while (ic in mem.indices) {
                var jump = 1
                when (mem[ic].opc) {
                    0 -> regs[mem[ic].rg[0]-'a'] = mem[ic].vl[0]
                    1 -> regs[mem[ic].rg[1]-'a'] = regs[mem[ic].rg[0]-'a']
                    2 -> regs[mem[ic].rg[0]-'a'] += 1
                    3 -> regs[mem[ic].rg[0]-'a'] -= 1
                    4 -> if (mem[ic].vl[0] != 0) jump = mem[ic].vl[1]
                    5 -> if (regs[mem[ic].rg[0]-'a'] != 0) jump = mem[ic].vl[0]
                }
                ic += jump
            }
            return this
        }
    }

    override fun solve(file: String) {
        val code = readTxtFile(file)

        print("\nAfter running the provided code, register a is set to $red$bold")
        println("${CPU().load(code).run(0).regs[0]}$reset")
        print("Changing register c to 1, changes this output to $red$bold")
        println("${CPU().apply{ this.regs[2] = 1 }.load(code).run(0).regs[0]}$reset")

    }
}