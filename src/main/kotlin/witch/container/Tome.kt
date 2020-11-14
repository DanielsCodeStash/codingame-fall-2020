package witch.container

data class Tome(
        val spell: Spell,
        val tomeIndex: Int,
        val taxCount: Int
) {
    fun debugString(): String {
        return "T($tomeIndex $taxCount ${spell.debugString()})"
    }
}