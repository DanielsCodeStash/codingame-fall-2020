package witch
import witch.smarts.BrewSearch
import witch.util.InfoKeeper
import witch.util.TimerHomie
import witch.util.getTauntMessage
import witch.util.readRoundState
import java.util.*

fun main() {

    val input = Scanner(System.`in`)
    var roundNum = 0

    // game loop
    while (true) {

        // read state
        val roundState = readRoundState(input, roundNum)

        // init our workers
        val timer = TimerHomie(if(roundNum == 0) 1000 else 50)
        val info = InfoKeeper(timer)

        // find best action
        val smartSearch = BrewSearch(timer, info)
        val nextAction = smartSearch.findNextAction(roundState)

        // output round info and action
        System.err.println(info.getReport())
        println("$nextAction ${getTauntMessage(roundState)}")

        // end round
        roundNum++
    }
}

