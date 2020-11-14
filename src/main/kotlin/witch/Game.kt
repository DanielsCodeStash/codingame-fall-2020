package witch
import witch.smarts.BrewSearch
import witch.util.TimerHomie
import witch.util.readRoundState
import java.util.*

fun main() {

    val input = Scanner(System.`in`)

    var roundNum = 0

    // game loop
    while (true) {

        // read state
        val roundState = readRoundState(input, roundNum)

        val timer =  TimerHomie(if(roundNum == 0) 1000 else 50)

        val smartSearch = BrewSearch(timer)

        smartSearch.findNextAction(roundState)
        System.err.println("done")

        roundNum++

        println("WAIT")
    }
}

