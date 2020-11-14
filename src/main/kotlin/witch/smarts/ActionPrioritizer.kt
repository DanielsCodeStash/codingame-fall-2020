package witch.smarts

import witch.container.FutureRoundState
import witch.util.TimerHomie

class ActionPrioritizer(timer: TimerHomie) {

    private val visitedStates = emptySet<String>()

    fun calculateStateScore(state: FutureRoundState): Int {

        if(visitedStates.contains(getStateHash(state)))
            return -100

        var score = 0
        val inventory = state.roundState.me.inventory

        score += inventory.getNumOfTier(0) * 2
        score += inventory.getNumOfTier(1) * 4
        score += inventory.getNumOfTier(2) * 6
        score += inventory.getNumOfTier(3) * 8

        score += state.roundState.me.score * 10

        score += state.roundState.me.spells.filter { !it.isExhausted }.count()

        return score
    }

    private fun getStateHash(state: FutureRoundState): String {
        val inventory = state.roundState.me.inventory
        val spells = state.roundState.me.spells

        val inventoryStatus =  "${inventory.tier0}${inventory.tier1}${inventory.tier2}${inventory.tier3}"
        val spellStatus = spells
                .map { it.isExhausted }
                .joinToString()

        return "$inventoryStatus$spellStatus"
    }


}