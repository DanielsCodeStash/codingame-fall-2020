package lifegame.container

data class Action(val verb: ActionType, val thing: String) {
    constructor(verb: ActionType, thing: Location) : this(verb, thing.toString())
    constructor(verb: ActionType, sampleId: Int) : this(verb, sampleId.toString())
    constructor(verb: ActionType) : this(verb, "")
}