package witch.smarts

import witch.container.Action
import witch.container.ActionType
import witch.container.FutureRoundState
import witch.container.RoundState
import witch.util.InfoKeeper
import witch.util.TimerHomie

class BrewSearch(private val timer: TimerHomie, info: InfoKeeper, val startRound: Int) {

    // smarts
    private val simulator = BrewSimulator(timer)
    private val prioritizer = ActionPrioritizer(timer, this)
    private val resultKeeper = ResultKeeper(timer)
    private val dataKeeper = DepthKeeper(timer, info)

    // search boi
    fun findNextAction(state: RoundState): Action {

        dataKeeper.start(FutureRoundState(state, emptyList()))

        while (true) {

            while (dataKeeper.currentLevelHasMoreData()) {

                if(!timer.thereIsTimeLeftForSearching()) {
                    return resultKeeper.getNextAction()
                }

                examineState(dataKeeper.getNextState())
            }

            if(dataKeeper.noMoreFutureStates()){
                return resultKeeper.getNextAction()
            } else {
                dataKeeper.initiateNextLevel()
            }
        }
    }

    private fun examineState(state: FutureRoundState) {

        // assess how much we like this node, anything below zero and we ignore this branch
        state.score = prioritizer.calculateStateScore(state)

        //debugPrintOut(state)

        // offer to store this node and the path to it
        resultKeeper.offerState(state)

        // check what nodes we can reach from here
        val possibleActions = simulator.getAllValidActionsForMe(state.roundState)

        // que up inspection of the new nodes we found
        possibleActions
                .map { simulator.getNextState(state, it) }
                .forEach { dataKeeper.queueFutureState(it) }
    }


}