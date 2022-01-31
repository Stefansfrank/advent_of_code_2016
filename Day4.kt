package com.sf.aoc2016

class Day4 : Solver {

    // room class
    data class Room(val code: String, val check: String, val id: Int) {

        // a helper class to sort letters by their amount
        data class Letter(val let: Char, var amt: Int)

        // tests whether the room is real or a decoy
        // creates a list of the letter structure above, counts letter and use sorting by amount and letter
        fun real():Boolean {
            val a2z = MutableList(26) { Letter('a' + it, 0) }
            code.replace("-","").forEach { a2z[it - 'a'].amt += 1 }
            a2z.sortByDescending { it.amt } // sort is stable in Kotlin println(a2z)
            return (a2z.take(check.length).fold(""){ acc, it -> acc + it.let.toString()} == check)
        }

        // decodes the name by adding the ID modulo 26
        fun decode() =
            String(code.map { if (it == '-') ' ' else 'a' + (((it-'a') + id) % 26) }.toCharArray())
    }

    // parse the file into the Room class
    private fun parse(inp:List<String>):List<Room> {
        val rooms = mutableListOf<Room>()
        val rCode = "[a-z-]+\\d".toRegex()
        val rCheck = "\\[\\w+\\]".toRegex()
        val rId = "-\\d+\\[".toRegex()
        inp.forEach { rooms.add(Room(
            rCode.find(it)!!.value.dropLast(2),
            rCheck.find(it)!!.value.drop(1).dropLast(1),
            rId.find(it)!!.value.drop(1).dropLast(1).toInt())) }
        return rooms
    }

    override fun solve(file: String) {
        val rooms = parse(readTxtFile(file))

        // Part 1
        print("\nThe sum of all room IDs of rooms passing the decoy detection is: $red$bold")
        println("${rooms.filter { it.real() }.fold(0) { acc, rm -> acc + rm.id }}$reset")

        // Part 2
        rooms.filter{ it.decode().contains("pole") }
            .forEach{ println("ID# $red$bold${it.id}$reset - ${it.decode()}") }
    }
}