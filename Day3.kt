package com.sf.aoc2016

class Day3 : Solver {

    // parse the input into a list if Int triples
    // regular (Part 1) rules apply, each line is a triple
    private fun parse(inp: List<String>):List<List<Int>> {
        val triples = mutableListOf<List<Int>>()
        inp.forEach { triples.add(it
            .replace("( )(\\1)+".toRegex()," ")
            .trim(' ')
            .split(' ')
            .map { c -> c.toInt() } )
        }
        return triples
    }

    // takes the list of parsed Int triples and creates a new list
    // using the column logic described in part 2
    private fun reorg(triples: List<List<Int>>):List<List<Int>> {

        val triples2 = mutableListOf<List<Int>>()
        for (tt in triples.chunked(3))
            (0..2).forEach { triples2.add( listOf(tt[0][it], tt[1][it], tt[2][it]) ) }
        return triples2
    }

    override fun solve(file: String) {
        // Part 1
        val triples = parse(readTxtFile(file))
        print("\nThe original list contains $red$bold")
        print("${triples.count { (it.sortedDescending().drop(1).sum() > it.maxOrNull()!!) }}")
        println("$reset possible triangles")

        // Part 2
        val triples2 = reorg(triples)
        print("The column sorted list contains $red$bold")
        print(triples2.count { (it.sortedDescending().drop(1).sum() > it.maxOrNull()!!) })
        println("$reset possible triangles")
    }
}