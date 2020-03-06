package com.novatec.kitchenkata

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

class Kitchen(
        val stations: List<Station>,
        val foodToPrepare: MutableList<Food>,
        val finishedMeal: MutableList<Food> = mutableListOf()
) {
    fun run() {
        val inital = foodToPrepare.size
        val prepareChannel = Channel<Food>(capacity = foodToPrepare.size)
        val returnChannels = stations.map { it.output }

        runBlocking {
            launch {
                while(finishedMeal.size < inital){
                    println(finishedMeal)
                    delay(100)
                }
                prepareChannel.close()
                stations.forEach{ it.close()}
                print("closed all channels")
            }
            doRun(prepareChannel, returnChannels)
        }
    }

    private suspend fun CoroutineScope.doRun(prepareChannel: Channel<Food>, returnChannels: List<Channel<Food>>) {
        foodToPrepare.forEach {
            launch {
                println("Sending $it to $prepareChannel")
                prepareChannel.send(it)
            }
        }

        for (returnChannel in returnChannels) {
            launch {
                for (food in returnChannel) {
                    println("Got Back $food from $returnChannel")
                    if (food.cookingSteps.isNotEmpty()) {
                        prepareChannel.send(food)
                    } else {
                        finishedMeal.add(food)
                        println("#added $food")
                    }
                    println("Received $food")
                }
            }
        }

        stations.forEach {
            launch { it.prepare() }
        }

        for (food in prepareChannel) {
            stations.forEach { station ->
                launch {
                    println("Try to prepare $food with $station")
                    if (station.canPrepare(food)) {
                        println("Sending $food to $station")
                        station.input.send(food)
                    }
                }
            }
        }
    }
}

