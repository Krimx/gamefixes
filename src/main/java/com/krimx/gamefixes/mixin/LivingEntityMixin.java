package com.krimx.gamefixes.mixin;

import com.krimx.gamefixes.DiakreteArmorFloating;
import com.krimx.gamefixes.Gamefixes;
import com.krimx.gamefixes.HoneycombBootsWallJump;
import com.krimx.gamefixes.network.MaceNetworking;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MaceItem;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    // --- Attack ---
    @Unique
    private static final double MACE_ATTACK_RADIUS = 2.5D;

    @Unique
    private static final int MACE_MAX_TARGETS = 8;

    @Unique
    private static final double MACE_KNOCKBACK_STRENGTH = 0.8D;

    @Unique
    private static final int MACE_ATTACK_DELAY_TICKS = 6;

    // --- Swing sound ---
    @Unique
    private static final float MACE_SWING_VOLUME = 1.0F;

    @Unique
    private static final float MACE_SWING_PITCH = 1.0F;

    // --- Charge ---
    @Unique
    private int gamefixes$maceStartTick = 0;

    @Unique
    private boolean gamefixes$maceCharging = false;

    // --- Pending attack ---
    @Unique
    private boolean gamefixes$maceAttackPending = false;

    @Unique
    private int gamefixes$maceAttackDelay = 0;

    @Unique
    private float gamefixes$macePendingDamage = 0.0F;

    // --- Honeycomb Boots wall jump ---
    @Unique
    private boolean gamefixes$honeycombWallJumpUsed = false;

    @Unique
    private boolean gamefixes$honeycombDebugLogged = false;

    // --- Mace attack start ---
    @Inject(method = "startUsingItem", at = @At("HEAD"))
    private void gamefixes$maceStart(
            InteractionHand hand,
            CallbackInfo ci
    ) {
        LivingEntity entity = (LivingEntity) (Object) this;

        Gamefixes.LOGGER.info(
                "startUsingItem fired: {} hand={} item={}",
                entity.getName().getString(),
                hand,
                entity.getItemInHand(hand).getItem()
        );

        if (!(entity.level() instanceof ServerLevel)) {
            return;
        }

        if (!(entity instanceof ServerPlayer player)) {
            return;
        }

        ItemStack stack = entity.getItemInHand(hand);

        if (!(stack.getItem() instanceof MaceItem)) {
            return;
        }

        gamefixes$maceStartTick = entity.tickCount;
        gamefixes$maceCharging = true;
        gamefixes$maceAttackPending = false;

        Gamefixes.LOGGER.info(
                "Mace charging started on server: {} | startTick={}",
                player.getName().getString(),
                gamefixes$maceStartTick
        );

        MaceNetworking.sendChargeStart(player);
    }

    // --- Mace release ---
    @Inject(method = "releaseUsingItem", at = @At("HEAD"))
    private void gamefixes$maceRelease(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;

        Gamefixes.LOGGER.info(
                "releaseUsingItem fired: {} | server={} | charging={}",
                entity.getName().getString(),
                !entity.level().isClientSide(),
                gamefixes$maceCharging
        );

        if (!(entity instanceof ServerPlayer player)) {
            return;
        }

        if (!gamefixes$maceCharging) {
            Gamefixes.LOGGER.info(
                    "Mace release aborted: server charging state is false"
            );
            return;
        }

        if (!(entity.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        int ticksHeld =
                entity.tickCount - gamefixes$maceStartTick;

        gamefixes$maceCharging = false;

        float damage = calculateDamage(ticksHeld);

        // Start the visual swing.
        MaceNetworking.sendSwing(player);

        // Play the swing sound.
        serverLevel.playSound(
                null,
                player.blockPosition(),
                SoundEvents.MACE_SMASH_AIR,
                SoundSource.PLAYERS,
                MACE_SWING_VOLUME,
                MACE_SWING_PITCH
        );

        // Schedule the actual hit.
        gamefixes$maceAttackPending = true;
        gamefixes$maceAttackDelay = MACE_ATTACK_DELAY_TICKS;
        gamefixes$macePendingDamage = damage;

        Gamefixes.LOGGER.info(
                "Mace swing: {} ticks, {} damage, impact in {} ticks",
                ticksHeld,
                damage,
                MACE_ATTACK_DELAY_TICKS
        );
    }

    // --- Reset wall jump when landing ---
    @Inject(method = "tick", at = @At("HEAD"))
    private void gamefixes$resetHoneycombWallJump(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (!(entity instanceof Player player)) {
            return;
        }

        if (!gamefixes$honeycombDebugLogged) {
            gamefixes$honeycombDebugLogged = true;
        }

        if (player.onGround()) {
            gamefixes$honeycombWallJumpUsed = false;
        }
    }

    // --- Jump input / debug ---
    @Inject(method = "setJumping", at = @At("HEAD"))
    private void gamefixes$honeycombWallJump(
            boolean jumping,
            CallbackInfo ci
    ) {
        if (!jumping) {
            return;
        }

        LivingEntity entity = (LivingEntity) (Object) this;

        if (!(entity instanceof Player player)) {
            return;
        }

        if (!(entity.level() instanceof ServerLevel)) {
            return;
        }

        boolean wearingBoots =
                HoneycombBootsWallJump.isWearingHoneycombBoots(player);

        if (!wearingBoots) {
            return;
        }

        if (player.onGround()) {
            return;
        }

        if (gamefixes$honeycombWallJumpUsed) {
            return;
        }

        if (!player.horizontalCollision) {
            return;
        }

        if (!HoneycombBootsWallJump.canWallJump(player)) {
            return;
        }

        HoneycombBootsWallJump.performWallJump(player);

        gamefixes$honeycombWallJumpUsed = true;
    }

    // --- Pending mace attack ---
    @Inject(method = "tick", at = @At("HEAD"))
    private void gamefixes$maceAttackTick(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (!gamefixes$maceAttackPending) {
            return;
        }

        if (!(entity instanceof ServerPlayer player)) {
            return;
        }

        if (!(entity.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        if (--gamefixes$maceAttackDelay > 0) {
            return;
        }

        gamefixes$maceAttackPending = false;

        Gamefixes.LOGGER.info(
                "Mace impact timer reached zero for {}",
                player.getName().getString()
        );

        performMaceAttack(
                serverLevel,
                player,
                gamefixes$macePendingDamage
        );
    }

    // --- Diakrete floating ---
    @Inject(method = "travel", at = @At("TAIL"))
    private void gamefixes$diakreteFloating(
            Vec3 movementInput,
            CallbackInfo ci
    ) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (!(entity instanceof Player player)) {
            return;
        }

        Vec3 velocity = player.getDeltaMovement();

        player.setDeltaMovement(
                DiakreteArmorFloating.applyFloatingVelocity(
                        player,
                        velocity,
                        movementInput
                )
        );
    }

    @Unique
    private static float calculateDamage(int ticksHeld) {
        final int optimalCharge = 20;
        final int fullFalloffTime = 60;
        final float maximumDamage = 7.0F;
        final float minimumDamage = 3.5F;

        if (ticksHeld <= optimalCharge) {
            return maximumDamage *
                    ((float) ticksHeld / optimalCharge);
        }

        if (ticksHeld >= fullFalloffTime) {
            return minimumDamage;
        }

        double progress =
                (double) (ticksHeld - optimalCharge)
                        / (fullFalloffTime - optimalCharge);

        double falloff =
                Math.log1p(progress * 9.0)
                        / Math.log1p(9.0);

        return (float) (
                maximumDamage
                        - (maximumDamage - minimumDamage) * falloff
        );
    }

    @Unique
    private static void performMaceAttack(
            ServerLevel serverLevel,
            ServerPlayer player,
            float damage
    ) {
        /*
         * Use a larger 3D search box so the impact isn't dependent
         * on the target being at exactly the player's Y level.
         */
        AABB searchBox =
                player.getBoundingBox()
                        .inflate(MACE_ATTACK_RADIUS);

        List<LivingEntity> candidates =
                serverLevel.getEntitiesOfClass(
                        LivingEntity.class,
                        searchBox,
                        target ->
                                target != player
                                        && target.isAlive()
                                        && !target.isSpectator()
                );

        int hits = 0;

        for (LivingEntity target : candidates) {
            if (hits >= MACE_MAX_TARGETS) {
                break;
            }

            /*
             * Measure the closest point on the target's entire
             * bounding box to the player.
             */
            AABB box = target.getBoundingBox();

            double closestX = Math.max(
                    box.minX,
                    Math.min(player.getX(), box.maxX)
            );

            double closestY = Math.max(
                    box.minY,
                    Math.min(player.getY(), box.maxY)
            );

            double closestZ = Math.max(
                    box.minZ,
                    Math.min(player.getZ(), box.maxZ)
            );

            double dx = closestX - player.getX();
            double dy = closestY - player.getY();
            double dz = closestZ - player.getZ();

            double distanceSquared =
                    dx * dx +
                            dy * dy +
                            dz * dz;

            if (distanceSquared >
                    MACE_ATTACK_RADIUS * MACE_ATTACK_RADIUS) {
                continue;
            }

            /*
             * Use the normal player attack damage source for now.
             * We can switch this to the vanilla mace damage pipeline
             * once the attack state itself is confirmed to be working.
             */
            if (!target.hurtServer(
                    serverLevel,
                    serverLevel.damageSources().playerAttack(player),
                    damage
            )) {
                Gamefixes.LOGGER.info(
                        "Mace failed to hurt target: {}",
                        target.getName().getString()
                );

                continue;
            }

            hits++;

            double knockbackX =
                    target.getX() - player.getX();

            double knockbackZ =
                    target.getZ() - player.getZ();

            double length =
                    Math.sqrt(
                            knockbackX * knockbackX +
                                    knockbackZ * knockbackZ
                    );

            if (length > 0.0001D) {
                knockbackX /= length;
                knockbackZ /= length;

                target.setDeltaMovement(
                        target.getDeltaMovement().add(
                                knockbackX * MACE_KNOCKBACK_STRENGTH,
                                0.1D,
                                knockbackZ * MACE_KNOCKBACK_STRENGTH
                        )
                );

            }
        }

        Gamefixes.LOGGER.info(
                "Mace impact: {} damage, {} candidate(s), {} target(s) hit",
                damage,
                candidates.size(),
                hits
        );
    }
}