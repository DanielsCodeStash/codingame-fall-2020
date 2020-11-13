package witch.container

data class Action(val verb: ActionType, val thing: String) {
    constructor(verb: ActionType, id: Int) : this(verb, id.toString())
    constructor(verb: ActionType) : this(verb, "")

    override fun toString(): String {
        return if(thing == "")
            "$verb"
        else
            "$verb $thing"
    }
}