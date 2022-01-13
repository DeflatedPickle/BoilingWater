/* Copyright (c) 2021-2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.boilingwater.mixin;

import com.deflatedpickle.boilingwater.api.Boiling;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.Fluids;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings({"UnusedMixin", "unused"})
@Mixin(FluidBlock.class)
public abstract class MixinFluidBlock extends Block implements Boiling {
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

  public void update(BlockState state, WorldAccess world, BlockPos pos) {
    if (state.getFluidState().getFluid() == Fluids.WATER) {
      world.createAndScheduleBlockTick(pos, this, 20);
    }
  }

  @Override
  public void prepare(
      BlockState state, WorldAccess world, BlockPos pos, int flags, int maxUpdateDepth) {
    update(state, world, pos);
  }

  @Inject(method = "onBlockAdded", at = @At("HEAD"))
  public void onBlockAdded(
      BlockState state,
      World world,
      BlockPos pos,
      BlockState oldState,
      boolean notify,
      CallbackInfo ci) {
    update(state, world, pos);
  }

  @Inject(method = "getStateForNeighborUpdate", at = @At("HEAD"))
  public void getStateForNeighborUpdate(
      BlockState state,
      Direction direction,
      BlockState neighborState,
      WorldAccess world,
      BlockPos pos,
      BlockPos neighborPos,
      CallbackInfoReturnable<BlockState> cir) {
    update(state, world, pos);
  }

  @Inject(method = "neighborUpdate", at = @At("HEAD"))
  public void neighborUpdate(
      BlockState state,
      World world,
      BlockPos pos,
      Block block,
      BlockPos fromPos,
      boolean notify,
      CallbackInfo ci) {
    update(state, world, pos);
  }

  @Override
  public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
    super.randomDisplayTick(state, world, pos, random);

    if (isBoiling()) {
      ParticleEffect particle = null;
      SoundEvent sound = null;
      float volume = 0f;
      float pitch = 0f;

      if (random.nextInt(20) == 0) {
        particle = ParticleTypes.SMOKE;
        sound = SoundEvents.BLOCK_FIRE_EXTINGUISH;
        volume = 0.2f;
        pitch = 1f;
      } else if (random.nextInt(2) == 0) {
        particle = ParticleTypes.BUBBLE;
        sound = SoundEvents.BLOCK_BUBBLE_COLUMN_BUBBLE_POP;
        volume = 2.5f;
        pitch = random.nextFloat(0.5f, 1);
      }

      if (particle != null && sound != null) {
        var x = pos.getX() + random.nextDouble(1);
        var z = pos.getZ() + random.nextDouble(1);

        world.addParticle(particle, x, pos.getY() + 1, z, 0, 0, 0);
        world.playSound(x, pos.getY(), z, sound, SoundCategory.BLOCKS, volume, pitch, true);
      }
    }
  }
}
