package com.github.electroluxv2.twerktillitgrows.mixin;

import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends WorldMixin {
    @Shadow public abstract <T extends ParticleEffect> int spawnParticles(T particle, double x, double y, double z, int count, double deltaX, double deltaY, double deltaZ, double speed);

    @Override
    protected void addParticle(ParticleEffect particle, double x, double y, double z, double velocityX, double velocityY, double velocityZ, CallbackInfo callbackInfo) {
        this.spawnParticles(particle, x, y, z, 1, velocityX, velocityY, velocityZ, 1);
    }
}
