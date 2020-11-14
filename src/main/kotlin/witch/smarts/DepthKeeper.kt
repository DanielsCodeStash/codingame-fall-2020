package witch.smarts

import witch.container.FutureRoundState
import witch.util.InfoKeeper
import witch.util.TimerHomie

data class DepthKeeper(val timer: TimerHomie, val info: InfoKeeper) {

    private val maxCandidatesPerLevel = 200

    private var activeLevel = 0
    private var activeIndex = 0
    private var currentLevel = emptyList<FutureRoundState>()
    private var nextLevel = mutableListOf<FutureRoundState>()

    fun start(state: FutureRoundState) {
        queueFutureState(state)
        initiateNextLevel()
    }

    fun initiateNextLevel() {
        activeLevel++
        activeIndex = 0
        switchNextAndCurrent()
        nextLevel = mutableListOf()

        info.reportDepth(activeLevel, currentLevel.size)
    }

    private fun switchNextAndCurrent() {

        currentLevel = if(nextLevel.size > maxCandidatesPerLevel) {
            nextLevel
                    .sortedByDescending { it.score }
                    .subList(0, maxCandidatesPerLevel)


        } else {
            nextLevel
        }
    }

    fun queueFutureState(state: FutureRoundState) {
        nextLevel.add(state)
    }

    fun currentLevelHasMoreData(): Boolean {
        return activeIndex < currentLevel.size
    }

    fun getNextState(): FutureRoundState {
        return currentLevel[activeIndex++]
    }

    fun noMoreFutureStates(): Boolean {
        return nextLevel.isEmpty()
    }
}