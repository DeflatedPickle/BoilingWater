/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.boilingwater.mixin;

import com.deflatedpickle.boilingwater.BoilingWater;
import com.deflatedpickle.boilingwater.api.HasHeat;
import net.minecraft.block.CampfireBlock;
import org.spongepowered.asm.mixin.Mixin;

@SuppressWarnings({"unused", "UnusedMixin"})
@Mixin(CampfireBlock.class)
public class MixinCampfireBlock implements HasHeat {
  @Override
  public int getHeat() {
    return BoilingWater.CAMPFIRE;
  }
}
