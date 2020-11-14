package witch.smarts

import witch.container.Action
import witch.container.ActionType
import witch.container.FutureRoundState
import witch.util.TimerHomie

class ResultKeeper(timer: TimerHomie) {

    var bestState: FutureRoundState? = null

    fun offerState(state: FutureRoundState) {
        if(bestState == null || bestState!!.score < state.score) {
            bestState = state
        }
    }

    fun getNextAction(): Action {
        System.err.println(bestState!!.path)
        return bestState!!.path[0]
    }
}