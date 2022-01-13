/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.boilingwater.api

interface Cookable {
    val isCooking: Boolean
    var cookingTime: Int
    val neededTime: Int
}
