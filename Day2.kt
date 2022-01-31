package com.sf.aoc2016

class Day2 : Solver {

    // Builds 3x3 Pad for Part 1
    private fun pad1() = MapInt(3,3).also { mi ->
        var i = 1
        (0..2).forEach { y ->
            (0..2).forEach { x ->
                mi.mp[y][x] = i++
            }
        }
    }

    // Builds 5x5 pad for Part 2 (default value for MapChar is '.')
    private fun pad2() = MapChar(5,5).also { mi ->
        mi.mp[0][2] = '1'
        (0..2).forEach{ mi.mp[1][it+1] = '2' + it }
        (0..4).forEach{ mi.mp[2][it] = '5' + it }
        (0..2).forEach{ mi.mp[3][it+1] = 'A' + it }
        mi.mp[4][2] = 'D'
    }

    // executes one string of directions on the 3x3 pad starting in the middle
    // return the number it ends on as an Int
    private fun exec1(dirs: String):Int {
        val cd = mapOf('U' to 0, 'R' to 1, 'D' to 2, 'L' to 3)
        val bx = Rect(XY(0,0), XY(2,2))
        var loc = XY(1,1)
        dirs.forEach { loc = loc.mv(cd[it]!!, bx) }
        return pad1().mp[loc.y][loc.x]
    }

    // executes one string of directions on the 5x5 pad starting left middle
    // does not execute a move ending on a '.'
    private fun exec2(dirs: String):Char {
        val cd = mapOf('U' to 0, 'R' to 1, 'D' to 2, 'L' to 3)
        val bx = Rect(XY(0,0), XY(4,4))
        var loc = XY(0,2)
        val pad = pad2()
        dirs.forEach {
            val tmp = loc.mv(cd[it]!!, bx)
            if (pad.mp[tmp.y][tmp.x] != '.') loc = tmp
        }
        return pad.mp[loc.y][loc.x]
    }

    // loops through the input lines and calls exec1/2
    override fun solve(file: String) {
        val inp = readTxtFile(file)
        var result1 = ""
        var result2 = ""
        inp.forEach {
            result1 += exec1(it).toString()
            result2 += exec2(it).toString()
        }
        println("\nThe code on a regular pad is $red$bold$result1$reset")
        println("On the advanced pad, the code is $red$bold$result2$reset")
    }
}