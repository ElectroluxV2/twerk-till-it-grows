package com.github.electroluxv2.twerktillitgrows.mixin;

import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Unique
    private static final Map<UUID, Boolean> crouchingMap = new HashMap<>();

    @Unique // TODO: Add config
    private static final int range = 3;

    @Inject(method = "tickMovement", at = @At("HEAD"))
    public void tickMovement(CallbackInfo callbackInfo) {
        // noinspection ConstantConditions We might patch ServerPlayerEntity directly, but it would patching player entity anyway
        if (!((Object) this instanceof ServerPlayerEntity target)) return;
        if (!(target.getWorld() instanceof ServerWorld serverWorld)) return;

        var id = target.getGameProfile().getId();
        var wasCrouching = crouchingMap.getOrDefault(id, false);
        var isCrouching = target.getPose().compareTo(EntityPose.CROUCHING) == 0;

        var change = wasCrouching != isCrouching;
        if (!change) return;

        crouchingMap.put(id, isCrouching);
        if (!isCrouching) return;

        // TODO: Add config
        if (serverWorld.random.nextDouble() > 0.5) return;

        var location = target.getBlockPos();

        for (int x = location.getX() - range; x <= location.getX() + range; x++) {
            for (int y = location.getY() - range; y <= location.getY() + range; y++) {
                for (int z = location.getZ() - range; z <= location.getZ() + range; z++) {
                    var pos = new BlockPos(x, y, z);

                    // TODO: Add config for filtering
                    BoneMealItem.useOnFertilizable(ItemStack.EMPTY, serverWorld, pos);
                    BoneMealItem.createParticles(serverWorld, pos, serverWorld.random.nextBetween(0, 15));
                }
            }
        }
    }
}
