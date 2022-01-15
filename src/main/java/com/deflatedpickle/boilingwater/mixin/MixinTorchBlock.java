/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.boilingwater.mixin;

import com.deflatedpickle.boilingwater.api.HasHeat;
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.block.TorchBlock;
import org.spongepowered.asm.mixin.Mixin;

@SuppressWarnings({"unused", "UnusedMixin"})
@Mixin(TorchBlock.class)
public class MixinTorchBlock implements HasHeat {
  @Override
  public int getHeat() {
    if ((Object) this instanceof RedstoneTorchBlock) {
      return 2;
    }
    return 10;
  }
}
