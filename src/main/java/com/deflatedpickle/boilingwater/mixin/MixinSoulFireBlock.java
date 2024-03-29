/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.boilingwater.mixin;

import com.deflatedpickle.boilingwater.BoilingWater;
import com.deflatedpickle.boilingwater.api.HasHeat;
import net.minecraft.block.SoulFireBlock;
import org.spongepowered.asm.mixin.Mixin;

@SuppressWarnings({"unused", "UnusedMixin"})
@Mixin(SoulFireBlock.class)
public class MixinSoulFireBlock implements HasHeat {
  @Override
  public int getHeat() {
    return BoilingWater.SOUL_FIRE;
  }
}
