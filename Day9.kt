package com.sf.aoc2016

class Day9 : Solver {

    // counts the length of the expanded string without building the new string
    // 'nest' controls whether nested expansion groups should be expanded (part 1: no, part2: yes)
    private fun count(inp: String, nest: Boolean):Long {

        var ix  = 0  // the current position (within the unexpanded string)
        var len = 0L // the length counter continuously update

        // loop until no more expansion groups found
        while (true) {
            val m = "\\((\\d+)x(\\d+)\\)".toRegex().find(inp, ix)
            if (m == null) break else {

                // first add anything between the last manipulation and the open parenthesis
                len += m.range.first - ix

                // slice the string that would be multiplied
                val rpt = inp.substring(m.range.last + 1 until m.range.last + 1 + m.groupValues[1].toInt())

                // recursion if that slice still contains parentheses
                if (nest && rpt.contains("\\((\\d+)x(\\d+)\\)".toRegex()))
                    len += count(rpt, true) * m.groupValues[2].toInt()
                else
                    len += rpt.length * m.groupValues[2].toInt()

                // set the index to continue traverse the original string after the repeated piece
                ix = m.range.last + 1 + m.groupValues[1].toInt()
            }
        }

        // clean up any tail that was not repeated
        len += inp.length - ix
        return len
    }

    override fun solve(file: String) {
        val inp = readTxtFile(file)
        print("\nWithout nested expansion, the length of the expanded string is ")
        println("$red$bold${count(inp[0], false)}$reset")
        print("With nested expansion, that length grows to ")
        println("$red$bold${count(inp[0], true)}$reset")
    }
}