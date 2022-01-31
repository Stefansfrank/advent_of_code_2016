package com.sf.aoc2016

class Day8 : Solver {

    // each operation has a type and two parameters (0 = rect, 1 = rot row, 2 = rot col)
    data class Op(val typ: Int, val p1: Int, val p2: Int)

    // parses operations from input
    private fun parse(inp: List<String>):List<Op> {
        val ops  = mutableListOf<Op>()
        val reXs = listOf( "rect (\\d+)x(\\d+)".toRegex(),
            "rotate row y=(\\d+) by (\\d+)".toRegex(),
            "rotate column x=(\\d+) by (\\d+)".toRegex() )
        for (ln in inp) {
            reXs.forEachIndexed { ix, re ->
                val m = re.find(ln)
                if (m != null) ops.add(Op(ix, m.groupValues[1].toInt(), m.groupValues[2].toInt()))
            }
        }
        return ops
    }

    // executes the code and initializes the display. Since the numbers involved are small and
    // performance does not matter, I represent the display as a List<List<Boolean>> and
    // implemented some simple loops for the operations
    private fun run(code: List<Op>, xdim: Int, ydim: Int): List<List<Boolean>> {
        val display = (0 until ydim).map{ MutableList(xdim){ false } }.toMutableList()
        for (op in code) {
            when (op.typ) {
                0 -> (0 until op.p2).forEach { y ->
                    (0 until op.p1).forEach { x ->
                        display[y][x] = true
                    }
                }
                1 -> {
                    val newRow = MutableList(xdim){ false }
                    display[op.p1].forEachIndexed { ix, flg -> newRow[(ix + op.p2) % xdim] = flg }
                    display[op.p1] = newRow
                }
                2 -> {
                    val newCol = MutableList(ydim){ false}
                    (0 until ydim).forEach { newCol[(it + op.p2) % ydim] = display[it][op.p1] }
                    newCol.forEachIndexed { ix, flg -> display[ix][op.p1] = flg }
                }
            }
        }
        return display
    }

    // prints out the display to the terminal
    private fun displayOut(display: List<List<Boolean>>) {
        for (ln in display) {
            print("$yellow$bold")
            println( ln.fold("") { acc, it -> acc + if (it) "#" else " " } )
            print(reset)
        }
    }

    override fun solve(file: String) {
        val code = parse(readTxtFile(file))

        val display = run(code, 50, 6)
        println("\n$red$bold${display.fold(0){ acc, ln -> acc + ln.count{ (it) }}}$reset dots are lit:\n")
        displayOut(display)
    }
}