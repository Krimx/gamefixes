package com.krimx.gamefixes.loot_bags;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class OutcomeFunctions {

    private static final Map<MinecraftServer, List<ScheduledAction>> SCHEDULED_ACTIONS =
            new HashMap<>();

    static {
        ServerTickEvents.END_SERVER_TICK.register(
                OutcomeFunctions::tickScheduler
        );

        ServerLifecycleEvents.SERVER_STOPPED.register(
                SCHEDULED_ACTIONS::remove
        );
    }

    public static void scheduleAfterTicks(
            ServerLevel level,
            int delay,
            Runnable action
    ) {
        scheduleAfterTicks(
                level.getServer(),
                delay,
                action
        );
    }

    public static void scheduleAfterTicks(
            MinecraftServer server,
            int delay,
            Runnable action
    ) {
        if (delay <= 0) {
            action.run();
            return;
        }

        SCHEDULED_ACTIONS
                .computeIfAbsent(
                        server,
                        ignored -> new ArrayList<>()
                )
                .add(
                        new ScheduledAction(
                                delay,
                                action
                        )
                );
    }

    private static void tickScheduler(
            MinecraftServer server
    ) {
        List<ScheduledAction> actions =
                SCHEDULED_ACTIONS.get(
                        server
                );

        if (
                actions == null
                        || actions.isEmpty()
        ) {
            return;
        }

        List<Runnable> readyActions =
                new ArrayList<>();

        Iterator<ScheduledAction> iterator =
                actions.iterator();

        while (iterator.hasNext()) {

            ScheduledAction scheduledAction =
                    iterator.next();

            scheduledAction.ticksRemaining--;

            if (
                    scheduledAction.ticksRemaining
                            <= 0
            ) {
                readyActions.add(
                        scheduledAction.action
                );

                iterator.remove();
            }
        }

        for (Runnable action : readyActions) {
            action.run();
        }

        if (actions.isEmpty()) {
            SCHEDULED_ACTIONS.remove(server);
        }
    }

    private static class ScheduledAction {

        private int ticksRemaining;

        private final Runnable action;

        private ScheduledAction(
                int ticksRemaining,
                Runnable action
        ) {
            this.ticksRemaining =
                    ticksRemaining;

            this.action =
                    action;
        }
    }

    public static double getRandomDouble(
            ServerLevel level,
            double minimum,
            double maximum
    ) {
        return minimum
                + level.getRandom().nextDouble()
                * (maximum - minimum);
    }

    public static double getRandomOffset(
            ServerLevel level,
            double range
    ) {
        return getRandomDouble(
                level,
                -range / 2.0,
                range / 2.0
        );
    }

    public static Item getRandomItem(
            ServerLevel level,
            Item... items
    ) {
        return items[
                level.getRandom().nextInt(
                        items.length
                )
                ];
    }

    public static Vec3 getPlayerEyePosition(
            ServerPlayer player
    ) {
        return player.getEyePosition();
    }

    public static Vec3 getPlayerLookDirection(
            ServerPlayer player
    ) {
        return player.getLookAngle();
    }

    public static Vec3 getRandomSpread(
            ServerLevel level,
            double spreadX,
            double spreadY,
            double spreadZ
    ) {
        return new Vec3(
                getRandomOffset(
                        level,
                        spreadX
                ),
                getRandomOffset(
                        level,
                        spreadY
                ),
                getRandomOffset(
                        level,
                        spreadZ
                )
        );
    }

    public static Vec3 getDirectionalVelocity(
            Vec3 direction,
            double speed
    ) {
        return direction.scale(
                speed
        );
    }

    public static Vec3 addVelocity(
            Vec3 velocity,
            Vec3 additionalVelocity
    ) {
        return velocity.add(
                additionalVelocity
        );
    }

    public static Vec3 getUpwardVelocity(
            double velocity
    ) {
        return new Vec3(
                0,
                velocity,
                0
        );
    }

    public static Vec3 getProjectileVelocity(
            ServerLevel level,
            Vec3 direction,
            double speed,
            double spreadX,
            double spreadY,
            double spreadZ,
            double upwardVelocity
    ) {
        Vec3 velocity =
                getDirectionalVelocity(
                        direction,
                        speed
                );

        Vec3 spread =
                getRandomSpread(
                        level,
                        spreadX,
                        spreadY,
                        spreadZ
                );

        velocity =
                addVelocity(
                        velocity,
                        spread
                );

        velocity =
                addVelocity(
                        velocity,
                        getUpwardVelocity(
                                upwardVelocity
                        )
                );

        return velocity;
    }

    public static Vec3 getProjectileVelocityFromPlayer(
            ServerLevel level,
            ServerPlayer player,
            double speed,
            double spreadX,
            double spreadY,
            double spreadZ,
            double upwardVelocity
    ) {
        Vec3 direction =
                getPlayerLookDirection(
                        player
                );

        return getProjectileVelocity(
                level,
                direction,
                speed,
                spreadX,
                spreadY,
                spreadZ,
                upwardVelocity
        );
    }

    public static Vec3 getPositionInFrontOfPlayer(
            ServerPlayer player,
            double distance
    ) {
        Vec3 eyePosition =
                getPlayerEyePosition(
                        player
                );

        Vec3 direction =
                getPlayerLookDirection(
                        player
                );

        return eyePosition.add(
                direction.scale(
                        distance
                )
        );
    }

    public static Entity spawnEntity(
            ServerLevel level,
            Entity entity
    ) {
        level.addFreshEntity(
                entity
        );

        return entity;
    }

    public static ItemEntity createItemEntity(
            ServerLevel level,
            Vec3 position,
            ItemStack stack
    ) {
        return new ItemEntity(
                level,
                position.x,
                position.y,
                position.z,
                stack
        );
    }

    public static ItemEntity spawnItem(
            ServerLevel level,
            Vec3 position,
            ItemStack stack
    ) {
        ItemEntity itemEntity =
                createItemEntity(
                        level,
                        position,
                        stack
                );

        spawnEntity(
                level,
                itemEntity
        );

        return itemEntity;
    }

    public static ItemEntity spawnItem(
            ServerLevel level,
            Vec3 position,
            Item item
    ) {
        return spawnItem(
                level,
                position,
                new ItemStack(item)
        );
    }

    public static Entity shootEntity(
            ServerLevel level,
            Entity entity,
            Vec3 velocity
    ) {
        setVelocity(
                entity,
                velocity
        );

        return spawnEntity(
                level,
                entity
        );
    }

    public static ItemEntity shootItem(
            ServerLevel level,
            Vec3 position,
            ItemStack stack,
            Vec3 velocity,
            int pickupDelay
    ) {
        ItemEntity itemEntity =
                createItemEntity(
                        level,
                        position,
                        stack
                );

        setPickupDelay(
                itemEntity,
                pickupDelay
        );

        shootEntity(
                level,
                itemEntity,
                velocity
        );

        return itemEntity;
    }

    public static ItemEntity shootItemFromPlayerFace(
            ServerLevel level,
            ServerPlayer player,
            ItemStack stack,
            double speed,
            double spreadX,
            double spreadY,
            double spreadZ,
            double upwardVelocity,
            int pickupDelay
    ) {
        Vec3 position =
                getPositionInFrontOfPlayer(
                        player,
                        0.0
                );

        Vec3 velocity =
                getProjectileVelocityFromPlayer(
                        level,
                        player,
                        speed,
                        spreadX,
                        spreadY,
                        spreadZ,
                        upwardVelocity
                );

        return shootItem(
                level,
                position.add(new Vec3(0,-.3,0)),
                stack,
                velocity,
                pickupDelay
        );
    }

    public static ItemEntity shootItemFromPlayerFace(
            ServerLevel level,
            ServerPlayer player,
            Item item,
            double speed,
            double spreadX,
            double spreadY,
            double spreadZ,
            double upwardVelocity,
            int pickupDelay
    ) {
        return shootItemFromPlayerFace(
                level,
                player,
                new ItemStack(item),
                speed,
                spreadX,
                spreadY,
                spreadZ,
                upwardVelocity,
                pickupDelay
        );
    }

    public static void setPickupDelay(
            ItemEntity itemEntity,
            int ticks
    ) {
        itemEntity.setPickUpDelay(
                ticks
        );
    }

    public static void setVelocity(
            Entity entity,
            Vec3 velocity
    ) {
        entity.setDeltaMovement(
                velocity
        );
    }
}