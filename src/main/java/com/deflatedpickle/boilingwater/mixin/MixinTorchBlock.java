/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.boilingwater.mixin;

import com.deflatedpickle.boilingwater.BoilingWater;
import com.deflatedpickle.boilingwater.api.HasHeat;
import net.minecraft.block.TorchBlock;
import net.minecraft.block.WallTorchBlock;
import org.spongepowered.asm.mixin.Mixin;

@SuppressWarnings({"unused", "UnusedMixin", "ConstantConditions"})
@Mixin(TorchBlock.class)
public class MixinTorchBlock implements HasHeat {
  @Override
  public int getHeat() {
    if ((Object) this instanceof TorchBlock || (Object) this instanceof WallTorchBlock) {
      return BoilingWater.TORCH;
    } else {
      return 0;
    }
  }
}
