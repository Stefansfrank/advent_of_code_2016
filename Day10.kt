package com.sf.aoc2016

class Day10: Solver {

    // most logic is in the member functions of Bot
    // not that the target values tgLow and tgHigh are >= 1000 for outputs and below for bots
    data class Bot(val num:Int, var high:Int, var low:Int, var tgLow: Int, var tgHigh: Int) {

        // set if the bot has handed of the chips
        var resolved = false

        // add a value sorting out high and low
        // also detects solution of part 1
        fun addVal(vl: Int) {
            if (high == -1) high = vl else low = vl
            if (low > high) high = low.also { low = high }
            if (high == 61 && low == 17)
                println("\nThe bot comparing 61 and 17 is #$red$bold$num$reset")
        }

        // tries to hand of chips if all necessary information is present
        fun resolve(net: Net):Boolean {
            if (resolved) return true else if (high > -1 && low > -1 && tgHigh > -1 && tgLow > -1) {
                if (tgHigh > 999) net.outs[tgHigh - 1000] = high else net.bots[tgHigh].addVal(high)
                if (tgLow > 999) net.outs[tgLow - 1000] = low else net.bots[tgLow].addVal(low)
                resolved = true
            }
            return resolved
        }
    }

    // outputs and bots
    data class Net(val bots: List<Bot> = List(210){Bot(it, -1, -1, -1, -1)},
                    val outs: MutableList<Int> = MutableList(21){ -1 })

    // parse input file into bots and outputs
    private fun parse(inp:List<String>):Net {
        val net = Net()
        val re1 = "value (\\d+) goes to bot (\\d+)".toRegex()
        val re2 = "bot (\\d+) gives low to (bot|output) (\\d+) and high to (bot|output) (\\d+)".toRegex()
        for (ln in inp) {
            var m = re1.find(ln)
            if (m != null)
                net.bots[m.groupValues[2].toInt()].addVal(m.groupValues[1].toInt())
            else {
                m = re2.find(ln)
                if (m != null) {
                    net.bots[m.groupValues[1].toInt()].tgLow = m.groupValues[3].toInt() +
                            if (m.groupValues[2] == "output") 1000 else 0
                    net.bots[m.groupValues[1].toInt()].tgHigh = m.groupValues[5].toInt() +
                            if (m.groupValues[4] == "output") 1000 else 0
                }
                else println("Line not parsed: $ln")
            }
        }
        return net
    }

    override fun solve(file: String) {
        val net = parse(readTxtFile(file))

        // loop through all bots repeatedly until they are all resolved
        var resolved = false
        while (!resolved)
            resolved = net.bots.fold(true){ acc, bt -> bt.resolve(net) && acc }
        println("The first three outputs multiplied result in $red$bold${net.outs[0] * net.outs[1] * net.outs[2]}$reset")
    }
}