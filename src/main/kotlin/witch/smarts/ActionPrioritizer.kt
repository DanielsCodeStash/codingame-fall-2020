package witch.smarts

import witch.container.ActionType
import witch.container.FutureRoundState
import witch.util.TimerHomie
import kotlin.math.roundToInt

class ActionPrioritizer(timer: TimerHomie) {

    fun calculateStateScore(state: FutureRoundState): Int {

        var score = 0
        val inventory = state.roundState.me.inventory

        score += inventory.getNumOfTier(0) * 1
        score += inventory.getNumOfTier(1) * 4
        score += inventory.getNumOfTier(2) * 6
        score += inventory.getNumOfTier(3) * 8

        score += state.roundState.me.score * 15

        score += state.roundState.me.spells.filter { !it.isExhausted }.count()

        score += pathRatingScore(state)

        return score
    }

    private fun pathRatingScore(state: FutureRoundState): Int {

        var score = 0

        var likelihoodsTomeIsGone = 0
        for(i in state.path.indices) {
            if(state.path[i].verb == ActionType.LEARN) {
                likelihoodsTomeIsGone += i * 1
            }
        }
        score -= likelihoodsTomeIsGone


        var likelihoodsBrewIsGone = 0
        for(i in state.path.indices) {
            if(state.path[i].verb == ActionType.BREW) {
                likelihoodsBrewIsGone += i * 1
            }
        }
        score -= likelihoodsBrewIsGone



        return score
    }




}