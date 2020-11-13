package witch.smarts

import witch.container.Action
import witch.container.FutureRoundState
import witch.container.RoundState
import witch.smarts.old.SpellNode
import java.util.*

class BrewSearch () {

    private val simulator = BrewSimulator()
    private val prioritizer = ActionPrioritizer()

    private val futureStateQueue : Queue<FutureRoundState> = LinkedList()

    fun findAction(state: RoundState, maxTimeAvailable: Int): Action {

        val possibleActions = simulator.getAllValidActionsForMe(state)

        val actionsToSearch = prioritizer.getReasonableAction(possibleActions, state)

        actionsToSearch
                .map { simulator.getNextState(state, it) }
                .forEach { futureStateQueue.add(it) }


    }
}