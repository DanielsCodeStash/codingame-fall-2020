package witch.container

class FutureRoundState (
        val roundState: RoundState,
        val path: List<Action>,
        var score: Int = 0
)