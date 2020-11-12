package witch.container

data class RoundState(
        val brews: List<Brew>,
        val me: Witch,
        val enemy: Witch
)