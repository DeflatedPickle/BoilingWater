/* Copyright (c) 2021-2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.boilingwater

import net.minecraft.item.ItemStack
import net.minecraft.recipe.SmeltingRecipe
import net.minecraft.world.World
import org.quiltmc.loader.api.ModContainer
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer

@Suppress("UNUSED")
object BoilingWater : ModInitializer {
    private const val MOD_ID = "$[id]"
    private const val NAME = "$[name]"
    private const val GROUP = "$[group]"
    private const val AUTHOR = "$[author]"
    private const val VERSION = "$[version]"

    const val REDSTONE_TORCH = 2
    const val TORCH = 10
    const val FIRE = 25
    const val CAMPFIRE = 30
    const val MAGMA = 50
    const val SOUL_FIRE = 75
    const val LAVA = 100

    override fun onInitialize(mod: ModContainer) {
        println(listOf(MOD_ID, NAME, GROUP, AUTHOR, VERSION))
    }

    fun hasCookingRecipe(stack: ItemStack, world: World) = world.recipeManager.values()
        .filterIsInstance<SmeltingRecipe>()
        .any { r ->
            r.ingredients[0].matchingStacks.toList()
                .map { s -> s.item }
                .any { i -> i == stack.item }
        }

    fun getCookingRecipe(stack: ItemStack, world: World) = world.recipeManager.values()
        .filterIsInstance<SmeltingRecipe>()
        .first { r ->
            r.ingredients[0].matchingStacks.toList()
                .map { s -> s.item }
                .any { i -> i == stack.item }
        }
}
