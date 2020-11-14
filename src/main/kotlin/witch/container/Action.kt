package witch.container

data class Action(val verb: ActionType, val thing: String, val subThing: String) {
    constructor(verb: ActionType, id: Int, subId: Int) : this(verb, id.toString(), subId.toString())
    constructor(verb: ActionType, id: Int) : this(verb, id.toString(), "")
    constructor(verb: ActionType) : this(verb, "", "")

    override fun toString(): String {
        return if (thing == "" && subThing == "") {
            "$verb"
        } else if (subThing == "") {
            "$verb $thing"
        } else {
            "$verb $thing $subThing"
        }
    }

    fun debugString(): String {
        return "A(${toString()})"
    }
}