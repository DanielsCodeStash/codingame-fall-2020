package witch.util

import java.util.*

class TimerHomie(private val maxTime: Int) {



    private val searchCutoff = if(maxTime > 100) 100 else 10


    private var graceQuestions = 0
    private val startTime = Date().time

    fun thereIsTimeLeftForSearching(): Boolean {

        // for performance reasons we only really respond to evey 100 calls
        if(graceQuestions < 10) {
            graceQuestions++
            return true
        }
        graceQuestions = 0

        val timeSpent = Date().time - startTime
        val timeLeft = timeSpent < maxTime - searchCutoff
        if(!timeLeft) {
            System.err.println("Bailing after " + timeSpent + "ms")
        }
        return timeLeft
    }
}