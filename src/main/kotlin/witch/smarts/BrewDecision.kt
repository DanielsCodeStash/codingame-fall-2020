package witch.smarts

import witch.container.*
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.HashSet

class BrewDecision {

    private var endNode: SpellNode? = null
    private var endNodeScore = 0

    private val explorationQueue : Queue<SpellNode> = LinkedList<SpellNode>()
    private val visitedNodes = mutableSetOf<String>()


    private val startTime = Date()
    private var removedBranches = 0
    private var brokeBecauseOfTime = false


    fun getWaysToMakeBrew(inventory: Inventory, spells: List<Spell>, brew: Brew, timeConstraint: Int): SpellNode? {

        explorationQueue.add(SpellNode(inventory, spells, brew, emptyList(), 0))
        var depth = 0
        var tested = 0
        do {

            if(Date().time - startTime.time > timeConstraint) {
                brokeBecauseOfTime = true
                break
            }

            val node = explorationQueue.remove()
            //System.err.println("handling ${node.madeActions}")

            val nodeScore = scoreNodePosition(node, brew)
            if(nodeScore > endNodeScore) {

                if(canBrew(brew, node.inventory)) {
                    val actionPath = listOf(node.madeActions, listOf(Action(ActionType.BREW, brew.id))).flatten()
                    endNode = node.copy(madeActions = actionPath)
                } else {
                    endNode = node
                }
                endNodeScore = nodeScore
            }


            val potentialNodes = mutableListOf<SpellNode>()

            node.spells
                    .filter { canCast(it, node.inventory) }
                    .filter { !weHaveALotOfProducedIngredients(it, node.inventory) }
                    .forEach { castSpell ->
                        val newInventory = node.inventory.getInventoryAfterSpellCast(castSpell)
                        val newActionPath = node.madeActions + Action(ActionType.CAST, castSpell.id)
                        val newSpells = node.spells.map {
                            if(it.id == castSpell.id)
                                Spell(it.id, it.delta0, it.delta1, it.delta2, it.delta3, true)
                            else
                                Spell(it.id, it.delta0, it.delta1, it.delta2, it.delta3, it.isExhausted)
                        }
                        potentialNodes.add(SpellNode(newInventory, newSpells, brew, newActionPath, node.depth + 1))
                    }

            if(node.spells.any{it.isExhausted}) {
                val restedSpells = node.spells.map { Spell(it.id, it.delta0, it.delta1, it.delta2, it.delta3, false) }
                val newActionPath = node.madeActions + Action(ActionType.REST)
                potentialNodes.add(SpellNode(node.inventory, restedSpells, node.brew, newActionPath, node.depth + 1))
            }

            potentialNodes.forEach {
                if(!visitedNodes.contains(it.getStateHash())) {
                    visitedNodes.add(it.getStateHash())
                    explorationQueue.add(it)
                }
            }


            tested++
            depth = node.depth
        } while (explorationQueue.isNotEmpty() && node.depth < 30 && endNodeScore != 100 )


        System.err.println("foundWay: ${endNode != null} brokeBecauseOfTime: $brokeBecauseOfTime depth: $depth expSize: ${explorationQueue.size} tested: $tested removed: $removedBranches")
        System.err.println("runtime: ${Date().time-startTime.time}ms")

        var i = 0
        if(endNode != null) {
                endNode!!.madeActions.forEach { action -> System.err.print("$action -> ") }
                System.err.println("\n")
                i++
        }

        return endNode
    }

    private fun scoreNodePosition(node: SpellNode, brew: Brew): Int {
        if (canBrew(brew, node.inventory)) {
            return 100
        }

        var score = 0


        val usefulIngredentOfTier3 = Math.min(brew.costTier3, node.inventory.tier3)
        score += usefulIngredentOfTier3 * 4

        val usefulIngredentOfTier2 = Math.min(brew.costTier2, node.inventory.tier2)
        score += usefulIngredentOfTier2 * 3

        val usefulIngredentOfTier1 = Math.min(brew.costTier1, node.inventory.tier1)
        score += usefulIngredentOfTier1 * 2

        val usefulIngredentOfTier0 = Math.min(brew.costTier0, node.inventory.tier0)
        score += usefulIngredentOfTier0 * 1

        return score
    }

    private fun weHaveALotOfProducedIngredients(spell: Spell, inventory: Inventory): Boolean {
        //System.err.println("$spell $inventory ${spell.producedTiers().none { inventory.getNumOfTier(it) < 7 }}")

        val weHaveAlot = spell.producedTiers().none { inventory.getNumOfTier(it) < 7 }

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

        if(spell.isExhausted) {
            return false
        }

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