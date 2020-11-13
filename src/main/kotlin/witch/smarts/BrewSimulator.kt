package witch.smarts

import witch.container.Action
import witch.container.FutureRoundState
import witch.container.RoundState

class BrewSimulator () {

    fun getNextState(state: RoundState, action: Action): FutureRoundState {

        return FutureRoundState(state, listOf(action))
    }


    fun getNextState(roundState: FutureRoundState: RoundState, action: Action): FutureRoundState {

        return FutureRoundState(state, listOf(action))
    }

    fun getAllValidActionsForMe(state: RoundState): List<Action> {

        return emptyList()
    }

}