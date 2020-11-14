package witch.smarts

import witch.container.Action
import witch.container.ActionType
import witch.container.FutureRoundState
import witch.util.TimerHomie

class ResultKeeper(timer: TimerHomie) {



    fun offerState(state: FutureRoundState, score: Int) {

    }

    fun getNextAction(): Action {
        return Action(ActionType.WAIT)
    }
}