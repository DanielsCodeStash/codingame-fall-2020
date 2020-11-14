package witch.smarts

import witch.container.FutureRoundState
import witch.util.TimerHomie

class ActionPrioritizer(timer: TimerHomie) {

    private val visitedStates = emptySet<String>()

    fun calculateStateScore(state: FutureRoundState): Int {

        if(visitedStates.contains(getStateHash(state)))
            return -100

        return 0
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