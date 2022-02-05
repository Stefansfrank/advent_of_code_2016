package com.sf.aoc2016

// this code is not analyzing and optimizing the code input since it finished in under
// the time it took to start thinking about optimization (~40 sec) for part 2
// it's obvious that there are parts of the given input code that can be optimized
// maybe I'll get around analyzing the input one of these days
class Day23 : Solver {

    // the data structure for operation lines
    data class Op(var opc: Int, val vl: List<Int>, val rg: List<Char>)

    // factory for operations from string input used to parse the given program
    // note that I create different opcodes for "cpy" and "jnz" depending on whether they are using
    // direct values or indirect / register based values
    fun makeOp(inp: String):Op {
        val ln = inp.split(' ')
        return when (ln[0]) {
            "cpy" -> // v0: cpy 3 a / v1: cpy a b (they have a v8 and v9 after a tgl on jnz which is a NOP)
                if ( ln[1][ln[1].length-1].isDigit() ) Op( 0, listOf(ln[1].toInt()), listOf(ln[2][0]) )
                else Op( 1, listOf(), listOf(ln[1][0], ln[2][0]) )
            "inc" -> Op( 2, listOf(), listOf(ln[1][0]) )
            "dec" -> Op( 3, listOf(), listOf(ln[1][0]) )
            "jnz" -> // v4: jnz 3 3 / v5: jnz a 2 / v6: jnz 2 a / v7: jnz a b
                if ( ln[1][ln[1].length-1].isDigit() ) {
                    if ( ln[2][ln[2].length-1].isDigit() ) Op( 4, listOf(ln[1].toInt(), ln[2].toInt()), listOf() )
                    else Op( 6, listOf(ln[1].toInt()), listOf(ln[2][0]) )
                } else {
                    if ( ln[2][ln[2].length-1].isDigit() ) Op( 5, listOf(ln[2].toInt()), listOf(ln[1][0]) )
                    else Op( 7, listOf(), listOf(ln[1][0], ln[2][0]) )
                }
            "tgl" -> Op( 10, listOf(), listOf(ln[1][0]) )
            else  -> Op(-1, listOf(), listOf())
        }
    }

    // the actual cpu
    inner class CPU(val regs: MutableList<Int> = mutableListOf(0, 0, 0, 0)) {

        // memory
        private var mem = mutableListOf<Op>()

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
                    6 -> if (mem[ic].vl[0] != 0) jump = regs[mem[ic].rg[0] - 'a']
                    7 -> if (regs[mem[ic].rg[0]-'a'] != 0) jump = regs[mem[ic].rg[1] - 'a']
                    10 -> {
                        val tix = ic + regs[mem[ic].rg[0] - 'a']
                        if (tix in mem.indices) mem[tix].opc = when (mem[tix].opc) {
                            0 -> 6
                            1 -> 7
                            2 -> 3
                            3, 10 -> 2
                            4 -> 8
                            5 -> 9
                            6 -> 0
                            7 -> 1
                            8 -> 4
                            9 -> 5
                            else -> -1
                        }
                    }
                }
                ic += jump
            }
            return this
        }
    }

    override fun solve(file: String) {
        val code = readTxtFile(file)

        print("\nAfter running the provided code, register a is set to $red$bold")
        println("${CPU().apply{ this.regs[0] = 7 }.load(code).run(0).regs[0]}$reset")
        print("Changing register a to 12, changes this output to $red$bold")
        println("${CPU().apply{ this.regs[0] = 12 }.load(code).run(0).regs[0]}$reset")

    }
}