package project1
import project1.container.Brew
import project1.container.Inventory
import project1.container.getTierNums
import java.util.*



fun main() {
    val input = Scanner(System.`in`)

    // game loop
    while (true) {
        val actionCount = input.nextInt() // the number of spells and recipes in play


        val brews = mutableListOf<Brew>()
        for (i in 0 until actionCount) {
            val actionId = input.nextInt() // the unique ID of this spell or recipe
            val actionType = input.next() // in the first league: BREW; later: CAST, OPPONENT_CAST, LEARN, BREW
            val delta0 = input.nextInt() // tier-0 ingredient change
            val delta1 = input.nextInt() // tier-1 ingredient change
            val delta2 = input.nextInt() // tier-2 ingredient change
            val delta3 = input.nextInt() // tier-3 ingredient change
            val price = input.nextInt() // the price in rupees if this is a potion
            brews.add(Brew(actionId, delta0*-1, delta1*-1, delta2*-1, delta3*-1, price))

            val tomeIndex = input.nextInt() // in the first two leagues: always 0; later: the index in the tome if this is a tome spell, equal to the read-ahead tax
            val taxCount = input.nextInt() // in the first two leagues: always 0; later: the amount of taxed tier-0 ingredients you gain from learning this spell
            val castable = input.nextInt() != 0 // in the first league: always 0; later: 1 if this is a castable player spell
            val repeatable = input.nextInt() != 0 // for the first two leagues: always 0; later: 1 if this is a repeatable player spell
        }

        var ownInventory: Inventory? = null
        for (i in 0 until 2) {

            val inv0 = input.nextInt() // tier-0 ingredients in inventory
            val inv1 = input.nextInt()
            val inv2 = input.nextInt()
            val inv3 = input.nextInt()

            val score = input.nextInt() // amount of rupees
            if(i == 0) {
                ownInventory = Inventory(inv0, inv1, inv2, inv3)
            }
    }

        // Write an action using println()
        // To debug: System.err.println("Debug messages...");

        val highestPrio = brews
                .sortedByDescending { it.price }.first { canBrew(it, ownInventory!!) }.id


        // in the first league: BREW <id> | WAIT; later: BREW <id> | CAST <id> [<times>] | LEARN <id> | REST | WAIT
        println("BREW $highestPrio")
    }
}

fun canBrew(brew: Brew, inventory: Inventory ) : Boolean {
    return getTierNums().none { brew.getCostOfTier(it) > inventory.getNumOfTier(it) }
}