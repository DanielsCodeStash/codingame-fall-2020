package witch.util

import witch.container.RoundState

fun getTauntMessage(roundState: RoundState): String {

    val weAreWinning = roundState.me.score >= roundState.enemy.score

    return if (weAreWinning) {
        when ((0..15).random()) {
            1 -> "Yer going down"
            2 -> "Luuuuhuusshher"
            4 -> "Du gamla, du fria"
            5 -> "I'm Einstein"
            6 -> "Mmmm goodass winning brew"
            7 -> "WART-FACE"
            8 -> "{Winning taunt}"
            else -> ""
        }
    } else {
        when ((0..15).random()) {
            1 -> "Only lucky."
            2 -> "I'm coming for you"
            3 -> "Hermione wannabe"
            4 -> "I'll get you"
            5 -> "Heavy breathing"
            6 -> "LUCK!!!"
            7 -> "{Losing taunt}"
            else -> ""
        }
    }
}