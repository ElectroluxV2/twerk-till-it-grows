package com.github.electroluxv2.twerktillitgrows.mixin;

import net.minecraft.particle.ParticleEffect;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(World.class)
public abstract class WorldMixin {
    // Why `Lnet/minecraft/item/BoneMealItem;createParticles(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;I)V` is not resolving as target invoke?
    @Inject(at = @At("HEAD"), method = "addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V")
    protected abstract void addParticle(ParticleEffect particle, double x, double y, double z, double velocityX, double velocityY, double velocityZ, CallbackInfo callbackInfo);
}
