package com.github.electroluxv2.twerktillitgrows.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.BoneMealItem;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mixin(ServerPlayer.class)
public abstract class PlayerEntityMixin {
    @Unique
    private static final Map<UUID, Boolean> crouchingMap = new HashMap<>();

    @Unique // TODO: Add config
    private static final int range = 3;

    @Inject(method = "tick", at = @At("HEAD"))
    public void tickMovement(CallbackInfo callbackInfo) {
        // noinspection ConstantConditions We might patch ServerPlayerEntity directly, but it would patching player entity anyway
        if (!((Object) this instanceof ServerPlayer target)) return;
        if (!(target.level() instanceof ServerLevel serverLevel)) return;

        var id = target.getGameProfile().getId();
        var wasCrouching = crouchingMap.getOrDefault(id, false);
        var isCrouching = target.getPose().compareTo(Pose.CROUCHING) == 0;

        var change = wasCrouching != isCrouching;
        if (!change) return;

        crouchingMap.put(id, isCrouching);
        if (!isCrouching) return;

        // TODO: Add config
        if (serverLevel.random.nextDouble() > 0.5) return;

        var location = target.getOnPos();

        for (int x = location.getX() - range; x <= location.getX() + range; x++) {
            for (int y = location.getY() - range; y <= location.getY() + range; y++) {
                for (int z = location.getZ() - range; z <= location.getZ() + range; z++) {
                    var pos = new BlockPos(x, y, z);

                    BlockState blockState = serverLevel.getBlockState(pos);
                    if (!(blockState.getBlock() instanceof SaplingBlock saplingBlock)) continue;

                    // TODO: Add config for filtering
                    BoneMealItem.growCrop(ItemStack.EMPTY, serverLevel, pos);
                    serverLevel.levelEvent(1505, saplingBlock.getParticlePos(pos), 15);
                }
            }
        }
    }
}
