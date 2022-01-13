/* Copyright (c) 2021-2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.boilingwater.mixin;

import com.deflatedpickle.boilingwater.api.Boiling;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.Fluids;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings({"UnusedMixin", "unused"})
@Mixin(FluidBlock.class)
public class MixinFluidBlock extends Block implements Boiling {
  public boolean boiling = false;

  public MixinFluidBlock(Settings settings) {
    super(settings);
  }

  @Override
  public boolean isBoiling() {
    return boiling;
  }

  @Override
  public void setBoiling(boolean value) {
    boiling = value;
  }

  @Inject(method = "onBlockAdded", at = @At("HEAD"))
  public void onBlockAdded(
      BlockState state,
      World world,
      BlockPos pos,
      BlockState oldState,
      boolean notify,
      CallbackInfo ci) {
    if (state.getFluidState().getFluid() == Fluids.WATER) {
      world.createAndScheduleBlockTick(pos, this, 20);
    }
  }

  @Override
  public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
    super.randomDisplayTick(state, world, pos, random);

    if (isBoiling()) {
      if (random.nextInt(20) == 0) {
        world.playSound(
            pos.getX(),
            pos.getY(),
            pos.getZ(),
            SoundEvents.BLOCK_FIRE_EXTINGUISH,
            SoundCategory.BLOCKS,
            0.2f,
            1.0f,
            true);
      }

      world.addParticle(
          ParticleTypes.SMOKE,
          pos.getX() + random.nextDouble(1),
          pos.getY() + random.nextDouble(0.8, 1.0),
          pos.getZ() + random.nextDouble(1),
          0.0,
          0.0,
          0.0);
    }
  }
}
