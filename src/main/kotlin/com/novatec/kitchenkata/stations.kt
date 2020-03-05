package com.novatec.kitchenkata

import com.novatec.kitchenkata.CookingStep.*
import kotlin.random.Random

class Handler(
	private val errorChance: Int = 10
) {
	fun handle(food: Food, step: CookingStep) {
		println("${step.verb.capitalize()} some ${food.name} ...")
		Thread.sleep(1000L * Random.nextInt(1, 4))
		if (Random.nextInt(1, 100) <= errorChance) {
			throw RuntimeException("Oops")
		}
	}
}

abstract class Station(
	private val handler: Handler = Handler()
) {
	abstract var handlesStep: CookingStep

	fun prepare(food: Food) {
		try {
			val step = food.cookingSteps.pop()
			if (food.isBorked) {
				println("This ${food.name} is ${food.conditions.last()}, we can't be ${step.verb} that!")
			} else {
				handler.handle(food, handlesStep)
				food.addStepResult(step)
			}
		} catch (e: Exception) {
			println("Something went wrong while ${handlesStep.verb} ${food.name}: [${e.message}]")
			food.addMishap(handlesStep)
		}
	}

	fun canPrepare(food: Food) = food.cookingSteps.peek() == handlesStep
}

class CuttingStation(handler: Handler = Handler()): Station(handler) {
	override var handlesStep = CUT
}

class PeelingStation(handler: Handler = Handler()): Station(handler) {
	override var handlesStep = PEEL
}

class GrillingStation(handler: Handler = Handler()): Station(handler) {
	override var handlesStep = GRILL
}

class BakingStation(handler: Handler = Handler()): Station(handler) {
	override var handlesStep = BAKE
}

class SpicingStation(handler: Handler = Handler()): Station(handler) {
	override var handlesStep = SPICE
}
