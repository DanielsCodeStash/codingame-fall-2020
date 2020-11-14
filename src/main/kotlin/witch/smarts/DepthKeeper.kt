package witch.smarts

import witch.container.FutureRoundState
import witch.util.TimerHomie

data class DepthKeeper(val timer: TimerHomie) {

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
        currentLevel = if(nextLevel.size > 100) nextLevel.subList(0, 100) else nextLevel
        nextLevel = mutableListOf()

        System.err.println("Depth $activeLevel size: ${currentLevel.size}")
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
}