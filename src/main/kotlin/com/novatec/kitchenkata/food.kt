package com.novatec.kitchenkata

import com.novatec.kitchenkata.CookingStep.*
import java.util.*

enum class CookingStep(
        val verb: String
) {
    CUT("cutting"),
    SPICE("spicing"),
    BAKE("baking"),
    GRILL("grilling"),
    PEEL("peeling")
}

open class Food(
        val name: String,
        val cookingSteps: LinkedList<CookingStep>,
        val conditions: MutableList<String> = mutableListOf()
) {

    var isBorked: Boolean = false

    constructor(name: String, vararg steps: CookingStep) : this(name, LinkedList<CookingStep>().apply { addAll(steps) })

    fun addStepResult(step: CookingStep) {
        when (step) {
            CUT -> addStepResult("cut")
            SPICE -> addStepResult("spiced")
            BAKE -> addStepResult("baked")
            GRILL -> addStepResult("grilled")
            PEEL -> addStepResult("peeled")
        }
    }

    fun addMishap(step: CookingStep) {
        isBorked = true
        when (step) {
            CUT -> addStepResult("crooked")
            SPICE -> addStepResult("pepper-covered")
            BAKE -> addStepResult("melted")
            GRILL -> addStepResult("burned")
            PEEL -> addStepResult("blood-covered")
        }
    }

    private fun addStepResult(result: String) {
        conditions.add(result)
    }

    override fun toString(): String = "${conditions.joinToString(" and ")} ${name.capitalize()}".capitalize()

}

class Potatoes : Food("Potatoes", PEEL, GRILL)

class Steak : Food("Steak", CUT, SPICE, GRILL)

class Cheese : Food("Cheese", SPICE, GRILL)

class FruitCake : Food("Fruit Cake", PEEL, CUT, SPICE, BAKE)
