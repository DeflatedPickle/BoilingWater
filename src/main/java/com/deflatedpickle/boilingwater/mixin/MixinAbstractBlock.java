/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.boilingwater.mixin;

import com.deflatedpickle.boilingwater.api.Boilable;
import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings({"unused", "UnusedMixin"})
@Mixin(AbstractBlock.class)
public class MixinAbstractBlock {
  @Inject(method = "scheduledTick", at = @At("TAIL"))
  public void onScheduledTick(
      BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
    if ((Object) this instanceof Block) {
      var block = world.getBlockState(pos.down().down()).getBlock();

      if (this instanceof Boilable) {
        world.createAndScheduleBlockTick(pos, (Block) (Object) this, 20);
        ((Boilable) this).setBoiling(block instanceof AbstractFireBlock);
      }
    }
  }
}
