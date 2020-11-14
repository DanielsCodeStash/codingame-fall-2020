package witch.smarts

import witch.container.Action
import witch.container.ActionType
import witch.container.FutureRoundState
import witch.container.RoundState
import witch.util.TimerHomie

class BrewSearch (private val timer: TimerHomie) {

    // smarts
    private val simulator = BrewSimulator(timer)
    private val prioritizer = ActionPrioritizer(timer)
    private val resultKeeper = ResultKeeper(timer)
    private val dataKeeper = DepthKeeper(timer)

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

            dataKeeper.initiateNextLevel()
        }
    }

    private fun examineState(state: FutureRoundState) {

        // assess how much we like this node, anything below zero and we ignore this branch
        val score = prioritizer.calculateStateScore(state)
        if(score < 0) return
        debugPrintOut(state, score)

        // offer to store this node and the path to it
        resultKeeper.offerState(state, score)

        // check what nodes we can reach from here
        val possibleActions = simulator.getAllValidActionsForMe(state.roundState)

        // que up inspection of the new nodes we found
        possibleActions
                .map { simulator.getNextState(state, it) }
                .forEach { dataKeeper.queueFutureState(it) }
    }


    private fun debugPrintOut(state: FutureRoundState, score: Int) {
        return

        var out = state.path.joinToString(" ") { it.debugString() }
        System.err.println(out)

        return
        if(state.roundState.round > 2 && state.path[0].verb == ActionType.LEARN && state.path[1].thing == state.path[0].thing) {

            var out = state.path.joinToString(" ") { it.debugString() }
            out += " => score $score "
            out += state.roundState.debugString()
            System.err.println(out)
        }
    }

}