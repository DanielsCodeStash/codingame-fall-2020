package witch.smarts

import witch.container.*
import witch.util.TimerHomie
import java.lang.RuntimeException

class BrewSimulator(timer: TimerHomie) {


    fun getNextState(state: FutureRoundState, action: Action): FutureRoundState {

        val newPath = listOf(state.path, listOf(action)).flatten()

        if(action.verb == ActionType.CAST) {
            val spell = state.roundState.me.spells.first { it.id == action.thing.toInt() }

            val castMultiple = action.subThing != ""
            val timesToCast = if (castMultiple) action.subThing.toInt() else 1

            val newInventory = state.roundState.me.inventory.getInventoryAfterSpellCast(spell, timesToCast)
            val newSpell = spell.copy(isExhausted = true)

            val newSpellList = listOf(state.roundState.me.spells.filter { it.id != spell.id }, listOf(newSpell)).flatten()

            val newMe = state.roundState.me.copy(inventory =  newInventory, spells = newSpellList)

            val newRoundState = state.roundState.copy(round = state.roundState.round + 1, me = newMe)

            return FutureRoundState(newRoundState, newPath)

        } else if(action.verb == ActionType.BREW) {

            val brew = state.roundState.brews.first{it.id == action.thing.toInt()}

            val newBrewList = state.roundState.brews.filter { it.id != brew.id }
            val newInventory = state.roundState.me.inventory.getInventoryAfterBrewing(brew)

            val newMe = state.roundState.me.copy(inventory = newInventory)

            val newRoundState = state.roundState.copy(me = newMe, brews = newBrewList, round = state.roundState.round + 1)

            return FutureRoundState(newRoundState, newPath)

        } else if(action.verb == ActionType.LEARN) {

            val tome = state.roundState.tomes.first{it.spell.id == action.thing.toInt()}

            val newSpell = Spell(action.thing.toInt(), tome.spell.delta0, tome.spell.delta1, tome.spell.delta2, tome.spell.delta3, false, tome.spell.repeatable)

            val newSpellList = listOf(state.roundState.me.spells, listOf(newSpell)).flatten()
            val newTomeList = state.roundState.tomes.filter { it.spell.id != tome.spell.id }

            // TODO: calculate tax received
            val newInventory = state.roundState.me.inventory.copy(tier0 = (state.roundState.me.inventory.tier0-tome.tomeIndex))

            val newMe = state.roundState.me.copy(spells = newSpellList, inventory = newInventory)

            val newRoundState = state.roundState.copy(me = newMe, tomes = newTomeList, round = state.roundState.round + 1)

            return FutureRoundState(newRoundState, newPath)
        } else if(action.verb == ActionType.REST) {

            val newSpellList = state.roundState.me.spells.map { it.copy(isExhausted = false) }
            val newMe = state.roundState.me.copy(spells = newSpellList)

            val newRoundState = state.roundState.copy(me = newMe, round = state.roundState.round + 1)
            return FutureRoundState(newRoundState, newPath)
        }

        throw RuntimeException("Unexpected Action")
    }

    fun getAllValidActionsForMe(state: RoundState): List<Action> {

        val brews = state.brews
                .filter { canBrew(it, state.me.inventory) }
                .map { Action(ActionType.BREW, it.id) }

        val castsSingle = state.me.spells
                .filter { !it.repeatable }
                .filter { canCast(it, state.me.inventory) }
                .map { Action(ActionType.CAST, it.id) }

        val castsMultiple = mutableListOf<Action>()
        state.me.spells
                .filter { it.repeatable }
                .forEach { spell ->
                    for(i in 0..numberOfTimesSpellCanBeCast(spell, state.me.inventory)) {
                        castsMultiple.add(Action(ActionType.CAST, spell.id, i))
                    }
                }

        val learn = state.tomes
                .filter { canLearn(it, state.me.inventory) }
                .map { Action(ActionType.LEARN, it.spell.id) }


        return listOf(brews, castsSingle, castsMultiple, learn).flatten()
    }

    private fun numberOfTimesSpellCanBeCast(spell: Spell, inventory: Inventory): Int {

        var times = 0
        var currentInventory = inventory

        while (canCast(spell, currentInventory)) {
            times++
            currentInventory = currentInventory.getInventoryAfterSpellCast(spell)
        }

        return times
    }

    private fun canLearn(tome: Tome, inventory: Inventory): Boolean {
        return inventory.tier0 >= tome.tomeIndex
    }

    private fun canBrew(brew: Brew, inventory: Inventory): Boolean {
        // for every tier, is none where the brew cost is bigger than what we have in the inventory?
        return getTierNums().none { brew.getCostOfTier(it) > inventory.getNumOfTier(it) }
    }

    private fun canCast(spell: Spell, inventory: Inventory): Boolean {

        if(spell.isExhausted) {
            return false
        }

        // of the needed tiers, is there any where we don't have enough? if so we can't cast
       return !spell.neededTiers().any { inventory.getNumOfTier(it) < spell.deltaForTier(it) * -1 }
    }

}