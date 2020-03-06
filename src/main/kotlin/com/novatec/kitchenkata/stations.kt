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
        delay(1000L * 2)
        if (Random.nextInt(1, 100) <= errorChance) {
            throw RuntimeException("Oops")
        }
    }
}

abstract class Station(
        private val handler: Handler = Handler(),
        val input: Channel<Food> = Channel<Food>(),
        val output: Channel<Food> = Channel<Food>()
) {
    abstract var handlesStep: CookingStep

    suspend fun close(){
        input.close()
        output.close()
    }

    suspend fun prepare() {
        for (food in input) {
            try {
                val step = food.cookingSteps.pop()
                println("Popping $step from $food")
                if (food.isBorked) {
                    println("This ${food.name} is ${food.conditions.last()}, we can't be ${step.verb} that!")
                } else {
                    handler.handle(food, handlesStep)
                    food.addStepResult(step)
                    println("Sending back $food")
                }
            } catch (e: Exception) {
                println("Something went wrong while ${handlesStep.verb} ${food.name}: [${e.message}]")
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
