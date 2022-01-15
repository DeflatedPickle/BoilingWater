/* Copyright (c) 2021-2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.boilingwater.mixin;

import com.deflatedpickle.boilingwater.api.Boilable;
import com.deflatedpickle.boilingwater.api.HasHeat;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings({"UnusedMixin", "unused"})
@Mixin(FluidBlock.class)
public abstract class MixinFluidBlock extends Block implements Boilable, HasHeat {
  @Shadow @Final protected FlowableFluid fluid;

  public MixinFluidBlock(Settings settings) {
    super(settings);
  }

  @Override
  public int getHeat() {
    if (fluid instanceof LavaFluid) {
      return 100;
    } else {
      return 0;
    }
  }

  public void update(BlockState state, WorldAccess world, BlockPos pos) {
    if (state.getFluidState().getFluid() == Fluids.WATER) {
      world.createAndScheduleBlockTick(pos, this, 20);
    }
  }

  @Override
  public boolean isBoiling(World world, BlockPos pos) {
    return getTemperature(world, pos) > 0;
  }

  @Override
  public int getTemperature(@NotNull World world, @NotNull BlockPos pos) {
    var down = world.getBlockState(pos.down().down()).getBlock();
    var east = world.getBlockState(pos.east().east()).getBlock();
    var west = world.getBlockState(pos.west().west()).getBlock();
    var north = world.getBlockState(pos.north().north()).getBlock();
    var south = world.getBlockState(pos.south().south()).getBlock();
    var up = world.getBlockState(pos.up().up()).getBlock();

    var heat = 0;
    if (down instanceof HasHeat) {
      heat += ((HasHeat) down).getHeat();
    } else if (east instanceof HasHeat) {
      heat += ((HasHeat) east).getHeat() / 0.5;
    } else if (west instanceof HasHeat) {
      heat += ((HasHeat) west).getHeat() / 0.5;
    } else if (north instanceof HasHeat) {
      heat += ((HasHeat) north).getHeat() / 0.5;
    } else if (south instanceof HasHeat) {
      heat += ((HasHeat) south).getHeat() / 0.5;
    } else if (up instanceof HasHeat) {
      heat += ((HasHeat) up).getHeat() / 0.25;
    }

    return heat;
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

    if (isBoiling(world, pos)) {
      ParticleEffect particle = null;
      SoundEvent sound = null;
      float volume = 0f;
      float pitch = 0f;

      if (random.nextInt(10) == 0) {
        particle = ParticleTypes.POOF;
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
