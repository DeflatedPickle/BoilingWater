/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.boilingwater.mixin;

import com.deflatedpickle.boilingwater.api.HasHeat;
import net.minecraft.block.FireBlock;
import org.spongepowered.asm.mixin.Mixin;

@SuppressWarnings({"unused", "UnusedMixin"})
@Mixin(FireBlock.class)
public class MixinFireBlock implements HasHeat {
  @Override
  public int getHeat() {
    return 30;
  }
}
