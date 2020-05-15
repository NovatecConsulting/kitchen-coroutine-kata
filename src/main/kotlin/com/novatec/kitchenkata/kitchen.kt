package com.novatec.kitchenkata

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class Kitchen(
        val stations: List<Station>,
        val foodToPrepare: MutableList<Food>,
        val finishedMeal: MutableList<Food> = mutableListOf()
) {
    suspend fun run() = coroutineScope {
        val orderSize = foodToPrepare.size
        val finishedChannel = Channel<Food>(capacity = orderSize)
        val foodQueue = Channel<Food>(capacity = orderSize)
        val stationsOutput = Channel<Food>(capacity = orderSize)

        moveFoodToQueue(foodQueue)
        startStations(stationsOutput)
        startFoodQueueSentinel( foodQueue)
        startStationOutputSentinel( stationsOutput, foodQueue, finishedChannel)
        waitForCookingToComplete(finishedChannel, orderSize, stationsOutput, foodQueue)
    }

    private suspend fun waitForCookingToComplete(
            finishedChannel: Channel<Food>,
            orderSize: Int,
            stationsOutput: Channel<Food>,
            foodQueue: Channel<Food>
    ) {
        for (finishedFood in finishedChannel) {
            finishedMeal.add(finishedFood)
            if (finishedMeal.size >= orderSize) {
                finishedChannel.close()
                stationsOutput.close()
                foodQueue.close()
                stations.forEach { it.close() }
            }
        }
    }

    private fun CoroutineScope.startStationOutputSentinel(
            stationsOutput: Channel<Food>,
            foodQueue: Channel<Food>,
            finishedChannel: Channel<Food>
    ) {
        launch {
            for (food in stationsOutput) {
                if (food.cookingSteps.isNotEmpty()) {
                    foodQueue.send(food)
                } else {
                    finishedChannel.send(food)
                }
            }
        }
    }

    private fun CoroutineScope.startFoodQueueSentinel(foodQueue: Channel<Food>) {
        launch {
            for (food in foodQueue) {
                stations.first { it.canPrepare(food) }.input.send(food)
            }
        }
    }

    private fun CoroutineScope.startStations(stationsOutput: Channel<Food>) {
        stations.forEach {
            launch { it.prepare(stationsOutput) }
        }
    }

    private suspend fun moveFoodToQueue(foodQueue: Channel<Food>) {
        foodToPrepare.forEach {
            foodQueue.send(it)
        }
    }
}

