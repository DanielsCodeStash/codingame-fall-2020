package witch
import witch.container.Action
import witch.smarts.BrewDecision
import witch.util.readRoundState
import java.util.*

fun main() {
    val input = Scanner(System.`in`)

    val actionList = mutableListOf<Action>()

    var roundNum = 0

    // game loop
    while (true) {

        // read state
        val roundState = readRoundState(input)

        val highestPrio = roundState.brews.sortedByDescending { it.price }

        if(roundNum == 0) {
            val smarts1 = BrewDecision().getWaysToMakeBrew(roundState.me.inventory, roundState.me.spells, highestPrio[0], 900)
            actionList.addAll(smarts1!!.madeActions)
        }

        val inInitalMode = actionList.size > roundNum
        if(inInitalMode) {
            System.err.print("InitalMode: ")
            actionList.subList(roundNum, actionList.size).forEach { System.err.print("$it -> ") }
            System.err.println()
            println(actionList[roundNum])
        } else {
            System.err.println("On the fly mode going for ${highestPrio[0]}")
            val smarts1 = BrewDecision().getWaysToMakeBrew(roundState.me.inventory, roundState.me.spells, highestPrio[0], 40)
            println(smarts1!!.madeActions.first())
        }



        // in the first league: BREW <id> | WAIT; later: BREW <id> | CAST <id> [<times>] | LEARN <id> | REST | WAIT

        roundNum++

    }
}

