package com.novatec.kitchenkata

import java.util.*
import kotlin.system.exitProcess

class Kitchen(
	val stations: List<Station>,
	val foodToPrepare: MutableList<Food>,
	val finishedMeal: MutableList<Food> = mutableListOf()
) {
	fun run() {
		while (foodToPrepare.isNotEmpty()) {
			findMoreWork()
		}
	}

	private fun findMoreWork() {
		val done = foodToPrepare.filter { it.cookingSteps.isEmpty() }
		finishedMeal.addAll(done)
		foodToPrepare.removeAll(done)

		for (food in foodToPrepare) {
			for (station in stations) {
				if (station.canPrepare(food)) {
					station.prepare(food)
					return
				}
			}
		}
		if (foodToPrepare.isNotEmpty()) {
			println("Deadlock detected.")
			println("SHUT IT DOWN")
			exitProcess(-25)
		}
	}
}

