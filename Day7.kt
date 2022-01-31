package com.sf.aoc2016

class Day7 : Solver {

    // the core logic is in the packet class and uses some fun Regex lookahead
    data class Packet(val reg: List<String>, val hyp: List<String>) {

        fun tls():Boolean {
            // the way lookahead works here is that the expression (?!\1) ensures that the next
            // char is not the same then the content of group 1 (i.e. the first char)
            // WITHOUT consuming that next letter. That happens in the next group which is
            // followed by enforcing the content of the second and first group in reverse order
            val re = "(\\w)(?!\\1)(\\w)\\2\\1".toRegex()
            return reg.fold(false) { acc, it -> acc || re.find(it) != null} &&
                    !hyp.fold(false) { acc, it -> acc || re.find(it) != null}
        }

        fun ssl():Boolean {
            // here we need to allow overlapping matches, so we consume only the first letter and then ensure that
            // - the next letter is not the same (copied from above)
            // - the next letter after that is the same (similar to above)
            // however, we do not consume these letter thus I later do not get the whole 3 letter sequence
            // directly from the match object, I only know it's there and use the index of the match to get
            // it from the original string. Not that I only take the first two letters on regular packet data
            // and the second two letters for hyper data - they should match for the condition to be met
            val re = "(\\w)(?!\\1)(?=\\w\\1)".toRegex()
            val mp = mutableMapOf<String, Boolean>() // a map to keep track of valid sequences found in regular data
            for (pk in reg)
                re.findAll(pk).forEach { mp[pk.substring(it.range.first, it.range.first+2)] = true }
            for (pk in hyp)
                for (m in re.findAll(pk))
                    if (mp[pk.substring(m.range.first + 1, m.range.first+3)] != null) return true
            return false
        }
    }

    // parsing the input into a list of regular and hyper strings each
    private fun parse(inp: List<String>):List<Packet> {
        val traffic = mutableListOf<Packet>()
        val reHyp = "\\[(\\w+)]".toRegex()
        val reReg = "(?:^|])(\\w+)(?:$|\\[)".toRegex()
        for (ln in inp) {
            val hyp = reHyp.findAll(ln).toList().map { it.groupValues[1] }
            val reg = reReg.findAll(ln).toList().map { it.groupValues[1] }
            traffic.add(Packet(reg, hyp))
        }
        return traffic
    }

    override fun solve(file: String) {
        val traffic = parse(readTxtFile(file))
        println("\nTLS is supported by $red$bold${traffic.count { it.tls() }}$reset packets")
        println("while SSL is supported by $red$bold${traffic.count { it.ssl() }}$reset packets")
    }
}