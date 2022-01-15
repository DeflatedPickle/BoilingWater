/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.boilingwater.mixin;

import com.deflatedpickle.boilingwater.BoilingWater;
import com.deflatedpickle.boilingwater.api.Boilable;
import com.deflatedpickle.boilingwater.api.Cookable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings({"unused", "UnusedMixin"})
@Mixin(ItemEntity.class)
public abstract class MixinItemEntity extends Entity implements Cookable {
  @Shadow
  public abstract ItemStack getStack();

  @Shadow
  public abstract void setStack(ItemStack stack);

  private static final TrackedData<Integer> COOKING_TICKS;

  static {
    COOKING_TICKS = DataTracker.registerData(ItemEntity.class, TrackedDataHandlerRegistry.INTEGER);
  }

  public MixinItemEntity(EntityType<?> type, World world) {
    super(type, world);
  }

  @Override
  public boolean isCooking() {
    return getBlockStateAtPos().getBlock() instanceof Boilable
        && ((Boilable) getBlockStateAtPos().getBlock()).isBoiling(world, getBlockPos());
  }

  @Override
  public int getCookingTime() {
    return this.dataTracker.get(COOKING_TICKS);
  }

  @Override
  public void setCookingTime(int cookingTime) {
    this.dataTracker.set(COOKING_TICKS, cookingTime);
  }

  @Override
  public int getNeededTime() {
    return (BoilingWater.INSTANCE.getCookingRecipe(getStack(), world).getCookTime() * 2
            + BoilingWater.INSTANCE.getCookingRecipe(getStack(), world).getCookTime() / 3)
        * getStack().getCount();
  }

  @Inject(method = "initDataTracker", at = @At("TAIL"))
  public void initDataTracker(CallbackInfo ci) {
    this.dataTracker.startTracking(COOKING_TICKS, 0);
  }

  @Inject(method = "tick", at = @At("TAIL"))
  public void cook(CallbackInfo ci) {
    if (BoilingWater.INSTANCE.hasCookingRecipe(getStack(), world)) {
      if (isCooking() && getCookingTime() < getNeededTime()) {
        setCookingTime(getCookingTime() + 1);
        world.addParticle(ParticleTypes.SMOKE, getX(), getY(), getZ(), 0, 0, 0);
      } else if (getCookingTime() == getNeededTime()) {
        setStack(BoilingWater.INSTANCE.getCookingRecipe(getStack(), world).getOutput());
      }
    }
  }
}
