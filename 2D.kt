package com.sf.aoc2016

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

// a point with coordinates c and y
data class XY(val x: Int, val y: Int) {

    // adds a vector to a point
    fun add(p:XY) = XY(x + p.x, y + p.y)

    // returns the next point in the given direction
    // (0 = up, 1 = right, 2 = down, 3 = left)
    private val delX = listOf(0, 1, 0, -1)
    private val delY = listOf(-1, 0, 1, 0)
    fun mv(dir: Int) = XY( x + delX[dir], y + delY[dir])
    fun mv(dir: Int, dist: Int) = XY( x + delX[dir] * dist, y + delY[dir] * dist)
    fun mv(dir: Int, box:Rect) = XY( (x + delX[dir]).coerceIn(box.xRange()), (y + delY[dir]).coerceIn(box.yRange()))

    // returns the 4 or 8 neighbours depending on the flag 'diagonal'
    fun neighbors(diagonal: Boolean):List<XY> {
        var delX = listOf(0, 1, 0, -1)
        var delY = listOf(-1, 0, 1, 0)
        if (diagonal) {
            delX = listOf(0, 1, 1, 1, 0, -1, -1, -1)
            delY = listOf(-1, -1, 0, 1, 1, 1, 0, -1)
        }
        var i = 0
        return List(if (diagonal) 8 else 4) { XY(x + delX[i], y + delY[i++]) }
    }

    fun mDist() = abs(x) + abs(y)
    fun mDist(p: XY) = abs(p.x - x) + abs(p.y - y)
}

// a rectangle defined with the corner points
data class Rect(val from:XY, val to:XY) {

    // range functions for either direction enforcing from < to
    fun xRange() = (min(from.x, to.x) .. max(from.x, to.x))
    fun yRange() = (min(from.y, to.y) .. max(from.y, to.y))
}

// a 2d mutable list of Booleans of dimensions xDim, yDim
// used for positive coordinate systems starting at 0,0
class Mask(val xdim:Int, val ydim:Int, private val default:Boolean = false) {

    // the underlying mask accessible with [y][x] sequence
    val msk = mutableListOf<MutableList<Boolean>>().apply { repeat(ydim) { this.add( MutableList(xdim) { default })} }

    // sets a whole region to true (default - whole mask)
    fun on(bx: Rect = Rect(XY(0,0), XY(xdim, ydim))) {
        msk.forEachIndexed{ y, ln -> if (y in bx.yRange())
            ln.forEachIndexed{ x, _ -> if (x in bx.xRange())
                msk[y][x] = true }}
    }

    // sets a whole region to false (default whole mask)
    fun off(bx: Rect = Rect(XY(0,0), XY(xdim, ydim))) {
        msk.forEachIndexed{ y, ln -> if (y in bx.yRange())
            ln.forEachIndexed{ x, _ -> if (x in bx.xRange())
                msk[y][x] = false }}
    }

    // sets a whole region to the opposite it is set (default whole mask)
    fun tgl(bx: Rect = Rect(XY(0,0), XY(xdim, ydim))) {
        msk.forEachIndexed{ y, ln -> if (y in bx.yRange())
            ln.forEachIndexed{ x, bt -> if (x in bx.xRange())
                msk[y][x] = !bt }}
    }

    // counts the amount of 'true' in the map
    fun cnt() = msk.fold(0) { acc, ln -> acc + ln.fold(0) { acc2, flg -> acc2 + if (flg) 1 else 0 } }

    // prints out a representation to stdout
    fun print() = msk.forEach { it.forEach{ b -> if (b) print("#") else print(".") }; println() }

    // prints out a representation to stdout with a position marked
    fun print(loc: XY, inv: Boolean) = msk.forEachIndexed { y, ln ->
        ln.forEachIndexed { x, b ->
            if (loc == XY(x,y)) print("$red$bold*$reset") else if (b xor inv) print("#") else print(".")
        }
        println("")
    }

    // returns a snapshot of the inner mask
    fun snap():List<List<Boolean>> {
        val nMsk = mutableListOf<List<Boolean>>()
        for (ln in msk) nMsk.add( ln.map{ it } )
        return nMsk
    }
}

// a 2D integer map of dimensions xdim, ydim
class MapInt(val xdim:Int, val ydim:Int, private val default:Int = 0) {

    // the actual map accessible with [y][x] sequence
    val mp = mutableListOf<MutableList<Int>>().apply { repeat(ydim) { this.add( MutableList(xdim) { default })} }

    // adds n to a whole region (defaults n = 1, region = all map)
    fun add(n: Int = 1, bx: Rect = Rect(XY(0,0), XY(xdim, ydim))) {
        mp.forEachIndexed{ y, ln -> if (y in bx.yRange())
            ln.forEachIndexed{ x, vl -> if (x in bx.xRange())
                mp[y][x] = vl + n }}
    }

    // subtracts n from a whole region (defaults n = 1, region = all map)
    // allows the enforcement of positive values with flag 'pos' (default off)
    fun sub(n: Int =1, bx: Rect = Rect(XY(0,0), XY(xdim, ydim)), pos: Boolean = false) {
        if (pos) mp.forEachIndexed{ y, ln -> if (y in bx.yRange())
            ln.forEachIndexed{ x, vl -> if (x in bx.xRange())
                mp[y][x] = max(0, vl - n) }}
        else mp.forEachIndexed{ y, ln -> if (y in bx.yRange())
            ln.forEachIndexed{ x, vl -> if (x in bx.xRange())
                mp[y][x] = vl - n }}
    }

    // adds all values of the map together
    fun cnt() = mp.fold(0) { acc, ln -> acc + ln.fold(0) { acc2, n -> acc2 + n } }

    // adds a simple XY getter
    fun get(xy:XY):Int = mp[xy.y][xy.x]
}

// a 2D integer map of dimensions xdim, ydim
class MapChar(val xdim:Int, val ydim:Int, private val default:Char = '.') {

    // the actual map accessible with [y][x] sequence
    val mp = mutableListOf<MutableList<Char>>().apply { repeat(ydim) { this.add( MutableList(xdim) { default })} }

}



