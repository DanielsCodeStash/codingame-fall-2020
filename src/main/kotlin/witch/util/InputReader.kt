package witch.util

import witch.container.*
import java.util.*

fun readRoundState(input: Scanner, roundNum: Int) : RoundState {

    val brews = mutableListOf<Brew>()
    val mySpells = mutableListOf<Spell>()
    val enemySpells = mutableListOf<Spell>()
    val tomes = mutableListOf<Tome>()

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

        var spell: Spell? = null

        when(actionType) {
            "CAST", "OPPONENT_CAST" -> spell = Spell(actionId, delta0, delta1, delta2, delta3, !castable, repeatable)
            "LEARN" -> spell = Spell(actionId, delta0, delta1, delta2, delta3, !castable, repeatable, true)
        }

        when(actionType) {
            "BREW" -> brews.add(Brew(actionId, delta0*-1, delta1*-1, delta2*-1, delta3*-1, price+tomeIndex))
            "CAST" -> mySpells.add(spell!!)
            "OPPONENT_CAST " -> mySpells.add(spell!!)
            "LEARN" -> tomes.add(Tome(spell!!, tomeIndex, taxCount))
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

    return RoundState(roundNum, brews, tomes, me, enemy)

}