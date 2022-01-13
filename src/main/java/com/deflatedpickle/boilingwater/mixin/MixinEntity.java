/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.boilingwater.mixin;

import com.deflatedpickle.boilingwater.api.Boiling;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings({"UnusedMixin", "rawtypes"})
@Mixin(Entity.class)
public abstract class MixinEntity {
  private static final TrackedData<Integer> BOILING_TICKS;

  static {
    BOILING_TICKS = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.INTEGER);
  }

  public int getBoilingTicks() {
    return dataTracker.get(BOILING_TICKS);
  }

  public void setBoilingTicks(int ticks) {
    dataTracker.set(BOILING_TICKS, ticks);
  }

  public void setBoilingFor(int seconds) {
    int i = seconds * 20;
    if (getBoilingTicks() < i) {
      setBoilingTicks(i);
    }
  }

  @Shadow @Final protected DataTracker dataTracker;

  @Shadow
  public abstract BlockPos getBlockPos();

  @Shadow
  public abstract World getWorld();

  @Shadow
  public abstract boolean damage(DamageSource source, float amount);

  @Inject(
      method = "<init>",
      at =
          @At(
              value = "INVOKE",
              shift = At.Shift.BEFORE,
              target = "Lnet/minecraft/entity/Entity;initDataTracker()V"))
  public void init(EntityType type, World world, CallbackInfo ci) {
    this.dataTracker.startTracking(BOILING_TICKS, 0);
  }

  @Inject(method = "baseTick", at = @At("TAIL"))
  public void scold(CallbackInfo ci) {
    var block = getWorld().getBlockState(getBlockPos()).getBlock();

    if ((Object) this instanceof LivingEntity && block instanceof FluidBlock) {
      if (block instanceof Boiling && ((Boiling) block).isBoiling()) {
        // setBoilingTicks(getBoilingTicks() + 1);
        if (getBoilingTicks() == 0) {
          setBoilingFor(4);
        }
        damage(DamageSource.IN_FIRE, 1);
      }
    }

    if (getBoilingTicks() > 0) {
      if (getBoilingTicks() % 10 == 0) {
        damage(DamageSource.ON_FIRE, 1);
      }
      setBoilingTicks(getBoilingTicks() - 1);
    }
  }

  @Inject(
      method = "writeNbt",
      at =
          @At(
              value = "INVOKE",
              shift = At.Shift.AFTER,
              target = "Lnet/minecraft/entity/Entity;getVelocity()Lnet/minecraft/util/math/Vec3d;"))
  public void writeNbt(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> cir) {
    if (getBoilingTicks() > 0) {
      nbt.putInt("TicksBoiling", getBoilingTicks());
    }
  }

  @Inject(
      method = "readNbt",
      at =
          @At(
              value = "INVOKE",
              shift = At.Shift.AFTER,
              target = "Lnet/minecraft/entity/Entity;setFrozenTicks(I)V"))
  public void readNbt(NbtCompound nbt, CallbackInfo ci) {
    setBoilingTicks(nbt.getInt("TicksBoiling"));
  }
}
