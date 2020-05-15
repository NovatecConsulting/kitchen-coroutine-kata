package com.novatec.kitchenkata

import com.novatec.kitchenkata.CookingStep.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

class Handler(
        private val errorChance: Int = 10
) {
    suspend fun handle(food: Food, step: CookingStep) {
        println("${step.verb.capitalize()} some ${food.name} ...")
        delay(1000L)
        if (Random.nextInt(1, 100) <= errorChance) {
            throw RuntimeException("Oops")
        }
    }
}

abstract class Station(
        private val handler: Handler = Handler(),
        val input: Channel<Food> = Channel()
) {
    abstract var handlesStep: CookingStep

    fun close() {
        input.close()
    }

    suspend fun prepare(output: Channel<Food>) {
        for (food in input) {
            try {
                val step = food.cookingSteps.pop()
                if (food.isBorked) {
                    println("This ${food.name} is ${food.conditions.last()}, we can't be ${step.verb} that!")
                } else {
                    handler.handle(food, handlesStep)
                    food.addStepResult(step)
                }
            } catch (e: Exception) {
                food.addMishap(handlesStep)
            }
            output.send(food)
        }
    }

    fun canPrepare(food: Food) = food.cookingSteps.peek() == handlesStep
}

class CuttingStation(handler: Handler = Handler()) : Station(handler) {
    override var handlesStep = CUT
}

class PeelingStation(handler: Handler = Handler()) : Station(handler) {
    override var handlesStep = PEEL
}

class GrillingStation(handler: Handler = Handler()) : Station(handler) {
    override var handlesStep = GRILL
}

class BakingStation(handler: Handler = Handler()) : Station(handler) {
    override var handlesStep = BAKE
}

class SpicingStation(handler: Handler = Handler()) : Station(handler) {
    override var handlesStep = SPICE
}
