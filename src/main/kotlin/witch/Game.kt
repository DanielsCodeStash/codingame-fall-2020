package witch
import witch.smarts.BrewDecision
import witch.util.readRoundState
import java.util.*

fun main() {
    val input = Scanner(System.`in`)

    // game loop
    while (true) {

        // read state
        val roundState = readRoundState(input)

        val highestPrio = roundState.brews.maxBy { it.price }

        val smarts = BrewDecision().getWaysToMakeBrew(roundState.me.inventory, roundState.me.spells, highestPrio!!)

        // in the first league: BREW <id> | WAIT; later: BREW <id> | CAST <id> [<times>] | LEARN <id> | REST | WAIT
        //println("BREW $highestPrio")
    }
}

