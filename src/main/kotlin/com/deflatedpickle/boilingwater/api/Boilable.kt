/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.boilingwater.api

import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * To be applied to [net.minecraft.block.FluidBlock]
 */
interface Boilable {
    fun isBoiling(world: World, pos: BlockPos): Boolean
    fun getTemperature(world: World, pos: BlockPos): Int
}
