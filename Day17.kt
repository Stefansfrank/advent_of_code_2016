package com.sf.aoc2016

import java.security.MessageDigest
import kotlin.experimental.and

class Day17 : Solver {

    // using the Java library for MD5
    // no reconverting into string necessary since I can actually use the first two bytes / four half-Bytes
    // directly to determine open / closed
    private fun md5(inp:ByteArray):ByteArray = MessageDigest.getInstance("MD5").digest(inp)

    // accessing the nth half-byte of an array
    private fun ByteArray.digit(n: Int) =
        if (n % 2 == 0) this[n/2].and(-16).rotateRight(4).toInt()
            else this[n/2].and(15).toInt()

    // reindexing the given sequence of directions (up, down, left, right) in the MD5 result
    // to be accessed from the directions I use in my 2D libraries (up, right, down, left)
    private val reIx = listOf(0,3,1,2)
    private val dir  = listOf('U','R','D','L')

    // result data class listing wins and continuation paths
    data class Next(val next: List<Path>, val wins: List<Path>)

    // the path including the determination of valid next steps
    inner class Path(private val loc: XY, val trc: String) {

        // valid next steps from here
        fun next():Next {
            val next = mutableListOf<Path>()
            val wins = mutableListOf<Path>()
            val hash = md5(trc.toByteArray())
            for ((i, n) in loc.neighbors(false).withIndex()) {
                if (n.x in 0..3 && n.y in 0..3 && hash.digit(reIx[i]) > 10) {
                    if (n.x == 3 && n.y == 3) wins.add(Path(n, trc + dir[i]))
                    else next.add(Path(n, trc + dir[i]))
                }
            }
            return Next(next, wins)
        }
    }

    // BFS path-finding with no cache since the MD5 makes every situation unique
    // MD5 also leads to every solution eventually getting stuck thus this will end
    // 'stop = true' will make this return when finding the first (shortest) win
    private fun find(seed: String, stop: Boolean): List<Path> {
        var paths = mutableListOf(Path(XY(0,0), seed))
        val wins  = mutableListOf<Path>()
        while (paths.size > 0) {
            val nPaths = mutableListOf<Path>()
            for (p in paths) {
                val nxt = p.next()
                if (nxt.wins.isNotEmpty()) if (stop) return nxt.wins else wins += nxt.wins
                nPaths += nxt.next
            }
            paths = nPaths
        }
        return wins
    }

    override fun solve(file: String) {
        val inp = "awrkjxxr"
        print("\nThe shortest path to the vault is $red$bold")
        println("${find(inp, true)[0].trc.drop(inp.length)}$reset")
        print("The longest potential path to the vault is $red$bold")
        print("${find(inp, false).sortedByDescending { it.trc.length }[0].trc.length - inp.length}")
        println("$reset steps long")
    }
}