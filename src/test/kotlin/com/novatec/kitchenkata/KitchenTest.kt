package com.novatec.kitchenkata

import io.kotlintest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.shouldBe
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Kitchen Test")
class KitchenTest {

	@Test
	fun `Food Is Cooked Correctly`() = runBlocking {
		val cuttingStation = CuttingStation(perfectFoodHandler())
		val peelingStation = PeelingStation(perfectFoodHandler())
		val spicingStation = SpicingStation(perfectFoodHandler())
		val grillingStation = GrillingStation(perfectFoodHandler())
		val bakingStation = BakingStation(perfectFoodHandler())

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

		kitchen.finishedMeal shouldHaveSize 4

		kitchen.finishedMeal.forEach {
			it.isBorked shouldBe false
		}

		kitchen.finishedMeal.map { it.toString() } shouldContainExactlyInAnyOrder listOf(
			"Peeled and grilled Potatoes",
			"Cut and spiced and grilled Steak",
			"Spiced and grilled Cheese",
			"Peeled and cut and spiced and baked Fruit Cake"
		)
	}

	@Test
	fun `Burned Potatoes`() = runBlocking {
		val peelingStation = PeelingStation(perfectFoodHandler())
		val grillingStation = GrillingStation(failingFoodHandler())

		val kitchen = Kitchen(
			stations = listOf(
				peelingStation,
				grillingStation
			),
			foodToPrepare = mutableListOf(
				Potatoes()
			)
		)

		kitchen.run()

		kitchen.finishedMeal shouldHaveSize 1

		kitchen.finishedMeal.first().isBorked shouldBe true

		kitchen.finishedMeal.first().toString() shouldBe "Peeled and burned Potatoes"
	}

	@Test
	fun `Steak Fails And Potatoes Succeed`() = runBlocking {
		val cuttingStation = CuttingStation(perfectFoodHandler())
		val peelingStation = PeelingStation(perfectFoodHandler())
		val spicingStation = SpicingStation(failingFoodHandler())
		val grillingStation = GrillingStation(perfectFoodHandler())

		val kitchen = Kitchen(
			stations = listOf(
				cuttingStation,
				peelingStation,
				spicingStation,
				grillingStation
			),
			foodToPrepare = mutableListOf(
				Potatoes(),
				Steak()
			)
		)

		kitchen.run()

		kitchen.finishedMeal shouldHaveSize 2

		val steak = kitchen.finishedMeal.first { it.name == "Steak" }
		val potatoes = kitchen.finishedMeal.first { it.name == "Potatoes" }

		with(steak) {
			isBorked shouldBe true
			toString() shouldBe "Cut and pepper-covered Steak"
		}

		with(potatoes) {
			isBorked shouldBe false
			toString() shouldBe "Peeled and grilled Potatoes"
		}
	}

	private fun perfectFoodHandler() = Handler(0)

	private fun failingFoodHandler() = Handler(101)

}

