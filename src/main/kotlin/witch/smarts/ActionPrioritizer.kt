package witch.smarts

import witch.container.ActionType
import witch.container.FutureRoundState
import witch.util.TimerHomie
import kotlin.math.max
import kotlin.math.roundToInt

class ActionPrioritizer(timer: TimerHomie, private val brewSearch: BrewSearch) {

    fun calculateStateScore(state: FutureRoundState): Int {

        var score = 0
        val inventory = state.roundState.me.inventory

        score += inventory.getNumOfTier(0) * 1
        score += inventory.getNumOfTier(1) * 2
        score += inventory.getNumOfTier(2) * 3
        score += inventory.getNumOfTier(3) * 4

        score += state.roundState.me.score * 15

        score += state.roundState.me.spells.filter { !it.isExhausted }.count()

        score += pathRatingScore(state)

        return score
    }

    private fun pathRatingScore(state: FutureRoundState): Int {

        var score = 0

        val baseValueOfLearn = max(5-brewSearch.startRound, 0)
        var learnEarlyScore = 0
        for(i in state.path.indices) {
            if(state.path[i].verb == ActionType.LEARN) {

                learnEarlyScore += baseValueOfLearn + ((15.0-state.roundState.me.spells.count()) / (i+1) ).roundToInt()
            }
        }
        score += learnEarlyScore

        val baseValueOfBrew = brewSearch.startRound
        var brewEarlyScore = 0
        for(i in state.path.indices) {
            if(state.path[i].verb == ActionType.BREW) {

                brewEarlyScore += baseValueOfBrew + (100.0 / (i+1) ).roundToInt()
            }
        }
        score += brewEarlyScore

        return score
    }




}