package witch.util

data class DepthInfo(val depth: Int, val numCandidates: Int )


class InfoKeeper (timerHomie: TimerHomie) {

    private val depthInfo = mutableListOf<DepthInfo>()

    fun reportDepth(depth: Int, numCandidates: Int) {
        depthInfo.add(DepthInfo(depth, numCandidates))
    }


    fun getReport(): String {
        var out = ""

        val deepest = depthInfo[depthInfo.size-1]
        out += "Depth: ${deepest.depth} ${deepest.numCandidates}"
        return out
    }
}