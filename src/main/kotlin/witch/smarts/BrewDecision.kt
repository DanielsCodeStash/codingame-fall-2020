package witch.smarts

import witch.container.*
import java.time.LocalDateTime
import java.util.*

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
                    // todo: remove duplicate paths
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