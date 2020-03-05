package com.novatec.kitchenkata

fun main() {

	val cuttingStation = CuttingStation(Handler(0))
	val peelingStation = PeelingStation()
	val spicingStation = SpicingStation()
	val grillingStation = GrillingStation()
	val bakingStation = BakingStation()

	val kitchen = Kitchen(
		stations = listOf(
			cuttingStation,
			peelingStation,
			spicingStation,
			grillingStation,
			bakingStation
		),
		foodToPrepare = mutableListOf(
			Potatoes(),
			Steak(),
			Cheese(),
			FruitCake()
		)
	)

	kitchen.run()

	println("Finished Meal:")
	for (food in kitchen.finishedMeal) {
		println(" - $food")
	}
}
