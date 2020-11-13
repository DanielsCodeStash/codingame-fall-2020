package witch.container

data class RoundState(
        val brews: List<Brew>,
        val tomes: List<Tome>,
        val me: Witch,
        val enemy: Witch
)