import java.util.*
import java.time.LocalDateTime

data class Action(val verb: ActionType, val thing: String) {
    constructor(verb: ActionType, id: Int) : this(verb, id.toString())
    constructor(verb: ActionType) : this(verb, "")

    override fun toString(): String {
        return "$verb $thing"
    }
}

enum class ActionType {
    BREW, CAST, REST, WAIT
}

data class Brew(
        val id: Int,
        val costTier0: Int,
        val costTier1: Int,
        val costTier2: Int,
        val costTier3: Int,
        val price: Int
) {
    fun getCostOfTier(tier: Int): Int {
        when(tier) {
            0 -> return costTier0
            1 -> return costTier1
            2 -> return costTier2
            3 -> return costTier3
        }
        throw RuntimeException("Faulty tier")
    }
}

data class Inventory(
        val tier0: Int,
        val tier1: Int,
        val tier2: Int,
        val tier3: Int) {

    fun getNumOfTier(tier: Int): Int {
        when(tier) {
            0 -> return tier0
            1 -> return tier1
            2 -> return tier2
            3 -> return tier3
        }
        throw RuntimeException("Faulty tier")
    }

    fun totalNumIngredients() = getTierNums().map { getNumOfTier(it) }.sum()

    fun getInventoryAfterSpellCast(spell: Spell): Inventory {
        val newInventory = Inventory(
                tier0 + spell.delta0,
                tier1 + spell.delta1,
                tier2 + spell.delta2,
                tier3 + spell.delta3
        )

        if(getTierNums().any { newInventory.getNumOfTier(it) < 0 }) {
            throw RuntimeException("Faulty spellcast! $spell $this")
        }

        return newInventory
    }

    fun toDebugString(): String {
        return "Inventory ($tier0, $tier1, $tier2, $tier3)"
    }
}

data class RoundState(
        val brews: List<Brew>,
        val me: Witch,
        val enemy: Witch
)

data class Spell(
        val id: Int,
        val delta0: Int,
        val delta1: Int,
        val delta2: Int,
        val delta3: Int,
        val isExhausted: Boolean
) {
    fun deltaForTier(tier: Int): Int {
        when(tier) {
            0 -> return delta0
            1 -> return delta1
            2 -> return delta2
            3 -> return delta3
        }
        throw RuntimeException("Faulty tier")
    }
    fun neededTiers(): List<Int> {
        return getTierNums().filter { deltaForTier(it) < 0 }
    }
    fun producedTiers(): List<Int> {
        return getTierNums().filter { deltaForTier(it) > 0 }
    }
}

fun getTierNums() = listOf(0, 1, 2, 3)

data class Witch(
        val score: Int,
        val inventory: Inventory,
        val spells: List<Spell>
)

fun main() {
    val input = Scanner(System.`in`)

    // game loop
    while (true) {

        // read state
        val roundState = readRoundState(input)

        val highestPrio = roundState.brews.maxBy { it.price }

        val smarts = BrewDecision().getWaysToMakeBrew(roundState.me.inventory, roundState.me.spells, highestPrio!!)

        // in the first league: BREW <id> | WAIT; later: BREW <id> | CAST <id> [<times>] | LEARN <id> | REST | WAIT
        //println("BREW $highestPrio")
    }
}



class BrewDecision {

    private val waysToComplete = mutableListOf<List<Action>>()
    private val explorationQueue : Queue<SpellNode> = LinkedList<SpellNode>()
    private val startTime = Date()
    private var removedBranches = 0
    private var biggestInventory = Inventory(0, 0, 0, 0)
    private var brokeBecauseOfTime = false

    fun getWaysToMakeBrew(inventory: Inventory, spells: List<Spell>, brew: Brew): List<List<Action>> {



        explorationQueue.add(SpellNode(inventory, spells, brew, emptyList(), 0))
        var depth = 0
        var tested = 0
        do {

            if(Date().time - startTime.time > 950) {
                brokeBecauseOfTime = true
                break
            }

            val node = explorationQueue.remove()
            //System.err.println("handling ${node.madeActions}")

            if (canBrew(brew, node.inventory)) {
                val actionPath = listOf(node.madeActions, listOf(Action(ActionType.BREW, brew.id))).flatten()
                waysToComplete.add(actionPath)
            }

            spells
                    .filter { canCast(it, node.inventory) }
                    .filter { !weHaveALotOfProducedIngredients(it, node.inventory) }
                    .forEach {
                        val newInventory = node.inventory.getInventoryAfterSpellCast(it)
                        val newActionPath = node.madeActions + Action(ActionType.CAST, it.id)
                        explorationQueue.add(SpellNode(newInventory, spells, brew, newActionPath, node.depth + 1))
                    }

            if(node.inventory.totalNumIngredients() > biggestInventory.totalNumIngredients())
                biggestInventory = node.inventory

            tested++
            depth = node.depth
        } while (explorationQueue.isNotEmpty() && node.depth < 20 && waysToComplete.isEmpty() )


        System.err.println("ways: ${waysToComplete.size} brokeBecauseOfTime: $brokeBecauseOfTime depth: $depth expSize: ${explorationQueue.size} tested: $tested removed: $removedBranches")
        System.err.println("runtime: ${Date().time-startTime.time}ms")
        System.err.println("biggest inventory = $biggestInventory" )

        var i = 0
        waysToComplete.forEach{
            System.err.println("$i: ")
            it.forEach { action ->  System.err.print("$action -> ") }
            System.err.println("\n")
            i++
        }

        return waysToComplete.toList()
    }

    private fun weHaveALotOfProducedIngredients(spell: Spell, inventory: Inventory): Boolean {
        //System.err.println("$spell $inventory ${spell.producedTiers().none { inventory.getNumOfTier(it) < 7 }}")


        val weHaveAlot = spell.producedTiers().none { inventory.getNumOfTier(it) < 2 }

        if(!weHaveAlot) {
            removedBranches++
        }

        return weHaveAlot
    }

    fun debugActionList(actions: List<Action>){
        actions.forEach { action ->  System.err.print("$action -> ") }
        System.err.println("\n")
    }

    private fun canCast(spell: Spell, inventory: Inventory): Boolean {

        // of the needed tiers, is there any where we don't have enough? if so we can't cast
        val s = !spell.neededTiers().any { inventory.getNumOfTier(it) < spell.deltaForTier(it) * -1 }

        // System.err.println("$s: $spell")

        return s
    }

    private fun canBrew(brew: Brew, inventory: Inventory): Boolean {
        // for every tier, is none where the brew cost is bigger than what we have in the inventory?
        val s = getTierNums().none { brew.getCostOfTier(it) > inventory.getNumOfTier(it) }

        if(inventory.tier1 > 2 && inventory.tier2 > 2 && inventory.tier3 > 2)
            System.err.println("$s: $brew $inventory")

        return s
    }
}


data class SpellNode(
        val inventory: Inventory,
        val spells: List<Spell>,
        val brew: Brew,
        val madeActions: List<Action>,
        val depth: Int
)


fun readRoundState(input: Scanner) : RoundState {

    val brews = mutableListOf<Brew>()
    val mySpells = mutableListOf<Spell>()
    val enemySpells = mutableListOf<Spell>()

    val actionCount = input.nextInt()
    for (i in 0 until actionCount) {
        val actionId = input.nextInt() // the unique ID of this spell or recipe
        val actionType = input.next() // in the first league: BREW; later: CAST, OPPONENT_CAST, LEARN, BREW

        val delta0 = input.nextInt() // tier-0 ingredient change
        val delta1 = input.nextInt() // tier-1 ingredient change
        val delta2 = input.nextInt() // tier-2 ingredient change
        val delta3 = input.nextInt() // tier-3 ingredient change
        val price = input.nextInt() // the price in rupees if this is a potion


        val tomeIndex = input.nextInt() // in the first two leagues: always 0; later: the index in the tome if this is a tome spell, equal to the read-ahead tax
        val taxCount = input.nextInt() // in the first two leagues: always 0; later: the amount of taxed tier-0 ingredients you gain from learning this spell
        val castable = input.nextInt() != 0 // in the first league: always 0; later: 1 if this is a castable player spell
        val repeatable = input.nextInt() != 0 // for the first two leagues: always 0; later: 1 if this is a repeatable player spell

        when(actionType) {
            "BREW" -> brews.add(Brew(actionId, delta0*-1, delta1*-1, delta2*-1, delta3*-1, price))
            "CAST" -> mySpells.add(Spell(actionId, delta0, delta1, delta2, delta3, castable))
            "OPPONENT_CAST " -> mySpells.add(Spell(actionId, delta0, delta1, delta2, delta3, castable))
        }
    }

    var myInventory: Inventory? = null
    var myScore = 0

    var enemyInventory: Inventory? = null
    var enemyScore = 0

    for (i in 0 until 2) {

        val inv0 = input.nextInt() // tier-0 ingredients in inventory
        val inv1 = input.nextInt()
        val inv2 = input.nextInt()
        val inv3 = input.nextInt()
        val score = input.nextInt() // amount of rupees

        if(i == 0) {
            myInventory = Inventory(inv0, inv1, inv2, inv3)
            myScore = score
        } else {
            enemyInventory = Inventory(inv0, inv1, inv2, inv3)
            enemyScore = score;
        }
    }

    val me = Witch(myScore, myInventory!!, mySpells)
    val enemy = Witch(enemyScore, enemyInventory!!, enemySpells)

    return RoundState(brews, me, enemy)

}
