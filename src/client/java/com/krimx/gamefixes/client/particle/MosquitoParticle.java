package com.krimx.gamefixes.client.particle;

import com.krimx.gamefixes.block.CattailBlock;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MosquitoParticle extends SingleQuadParticle {

    // ============================================================
    // Mosquito tuning
    // ============================================================

    // Visual size of each mosquito.
    private static final float SIZE = 0.027F;

    // Base movement speed.
    private static final double MIN_SPEED = 0.12;

    // Additional random speed added to MIN_SPEED.
    private static final double SPEED_VARIATION = 0.08;

    // How often the mosquito chooses a completely new direction.
    private static final int MIN_DIRECTION_CHANGE_TICKS = 5;
    private static final int MAX_DIRECTION_CHANGE_TICKS = 10;

    // How quickly the mosquito turns toward its target direction.
    private static final double STEERING = 0.39;

    // Maximum overall movement speed.
    private static final double MAX_SPEED = 0.20;

    // Maximum distance the mosquito can get from its cattail.
    private static final double SWARM_RADIUS = 4.5;

    // How strongly the mosquito is pushed back toward the cattail
    // when it gets outside the swarm radius.
    private static final double SWARM_RETURN_STRENGTH = 0.08;

    // Distance at which the mosquito starts avoiding blocks.
    private static final double AVOIDANCE_RADIUS = 1.6;

    // Strength of the block avoidance force.
    private static final double AVOIDANCE_STRENGTH = 0.14;

    // Number of blocks checked around the mosquito for obstacles.
    private static final int OBSTACLE_SCAN_RADIUS = 2;

    // ============================================================
    // Internal state
    // ============================================================

    private final RandomSource random;

    private final BlockPos sourcePos;

    private double targetX;
    private double targetY;
    private double targetZ;

    private int directionChangeTimer;

    private MosquitoParticle(
            ClientLevel level,
            double x,
            double y,
            double z,
            SpriteSet sprites,
            RandomSource random,
            BlockPos sourcePos
    ) {
        super(level, x, y, z, sprites.get(random));

        this.random = random;
        this.sourcePos = sourcePos;

        this.quadSize = SIZE;

        // We don't use the normal particle lifetime.
        this.lifetime = Integer.MAX_VALUE;

        this.gravity = 0.0F;
        this.friction = 1.0F;

        chooseNewDirection();

        this.directionChangeTimer = getNextDirectionChangeTime();
    }

    private int getNextDirectionChangeTime() {
        return MIN_DIRECTION_CHANGE_TICKS
                + random.nextInt(
                MAX_DIRECTION_CHANGE_TICKS
                        - MIN_DIRECTION_CHANGE_TICKS
                        + 1
        );
    }

    private void chooseNewDirection() {
        double x = random.nextDouble() * 2.0 - 1.0;
        double y = random.nextDouble() * 2.0 - 1.0;
        double z = random.nextDouble() * 2.0 - 1.0;

        double length = Math.sqrt(x * x + y * y + z * z);

        if (length < 0.001) {
            chooseNewDirection();
            return;
        }

        double speed =
                MIN_SPEED
                        + random.nextDouble() * SPEED_VARIATION;

        this.targetX = (x / length) * speed;
        this.targetY = (y / length) * speed;
        this.targetZ = (z / length) * speed;
    }

    private Vec3 getObstacleAvoidance() {
        Vec3 avoidance = Vec3.ZERO;

        BlockPos center = BlockPos.containing(this.x, this.y, this.z);

        for (int x = -OBSTACLE_SCAN_RADIUS;
             x <= OBSTACLE_SCAN_RADIUS;
             x++) {

            for (int y = -OBSTACLE_SCAN_RADIUS;
                 y <= OBSTACLE_SCAN_RADIUS;
                 y++) {

                for (int z = -OBSTACLE_SCAN_RADIUS;
                     z <= OBSTACLE_SCAN_RADIUS;
                     z++) {

                    BlockPos blockPos = center.offset(x, y, z);

                    VoxelShape shape = this.level.getBlockState(blockPos)
                            .getCollisionShape(
                                    this.level,
                                    blockPos,
                                    CollisionContext.empty()
                            );

                    if (shape.isEmpty()) {
                        continue;
                    }

                    for (AABB box : shape.toAabbs()) {
                        box = box.move(
                                blockPos.getX(),
                                blockPos.getY(),
                                blockPos.getZ()
                        );

                        double closestX =
                                Math.max(
                                        box.minX,
                                        Math.min(this.x, box.maxX)
                                );

                        double closestY =
                                Math.max(
                                        box.minY,
                                        Math.min(this.y, box.maxY)
                                );

                        double closestZ =
                                Math.max(
                                        box.minZ,
                                        Math.min(this.z, box.maxZ)
                                );

                        double awayX = this.x - closestX;
                        double awayY = this.y - closestY;
                        double awayZ = this.z - closestZ;

                        double distanceSquared =
                                awayX * awayX
                                        + awayY * awayY
                                        + awayZ * awayZ;

                        if (distanceSquared
                                >= AVOIDANCE_RADIUS * AVOIDANCE_RADIUS) {
                            continue;
                        }

                        double distance =
                                Math.sqrt(distanceSquared);

                        if (distance < 0.001) {
                            awayX =
                                    this.x
                                            - (box.minX + box.maxX) * 0.5;

                            awayY =
                                    this.y
                                            - (box.minY + box.maxY) * 0.5;

                            awayZ =
                                    this.z
                                            - (box.minZ + box.maxZ) * 0.5;

                            distance = Math.sqrt(
                                    awayX * awayX
                                            + awayY * awayY
                                            + awayZ * awayZ
                            );

                            if (distance < 0.001) {
                                awayY = 1.0;
                                distance = 1.0;
                            }
                        }

                        double strength =
                                (AVOIDANCE_RADIUS - distance)
                                        / AVOIDANCE_RADIUS;

                        strength *= AVOIDANCE_STRENGTH;

                        avoidance = avoidance.add(
                                awayX / distance * strength,
                                awayY / distance * strength,
                                awayZ / distance * strength
                        );
                    }
                }
            }
        }

        return avoidance;
    }

    @Override
    public void tick() {
        // Check whether the cattail still exists.
        BlockState sourceState =
                this.level.getBlockState(sourcePos);

        BlockState upperState =
                this.level.getBlockState(sourcePos.above());

        boolean sourceExists =
                sourceState.getBlock() instanceof CattailBlock
                        && upperState.getBlock() instanceof CattailBlock;

        if (!sourceExists) {
            CattailBlock.removeMosquitoSource(sourcePos);
            this.remove();
            return;
        }

        // Manually advance the particle so its lifetime is truly unlimited.
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        this.age++;

        directionChangeTimer--;

        if (directionChangeTimer <= 0) {
            chooseNewDirection();
            directionChangeTimer = getNextDirectionChangeTime();
        }

        // Keep the swarm within a radius around the cattail itself.
        double distanceX =
                this.x - (sourcePos.getX() + 0.5);

        double distanceY =
                this.y - (sourcePos.getY() + 1.0);

        double distanceZ =
                this.z - (sourcePos.getZ() + 0.5);

        double distanceSquared =
                distanceX * distanceX
                        + distanceY * distanceY
                        + distanceZ * distanceZ;

        if (distanceSquared > SWARM_RADIUS * SWARM_RADIUS) {
            double distance = Math.sqrt(distanceSquared);

            this.targetX +=
                    (-distanceX / distance)
                            * SWARM_RETURN_STRENGTH;

            this.targetY +=
                    (-distanceY / distance)
                            * SWARM_RETURN_STRENGTH;

            this.targetZ +=
                    (-distanceZ / distance)
                            * SWARM_RETURN_STRENGTH;
        }

        // Avoid trees, terrain, buildings, etc.
        Vec3 avoidance = getObstacleAvoidance();

        this.targetX += avoidance.x;
        this.targetY += avoidance.y;
        this.targetZ += avoidance.z;

        // Smoothly steer toward the target direction.
        this.xd +=
                (targetX - this.xd)
                        * STEERING;

        this.yd +=
                (targetY - this.yd)
                        * STEERING;

        this.zd +=
                (targetZ - this.zd)
                        * STEERING;

        double speed = Math.sqrt(
                this.xd * this.xd
                        + this.yd * this.yd
                        + this.zd * this.zd
        );

        if (speed > MAX_SPEED) {
            double scale = MAX_SPEED / speed;

            this.xd *= scale;
            this.yd *= scale;
            this.zd *= scale;
        }

        this.x += this.xd;
        this.y += this.yd;
        this.z += this.zd;
    }

    @Override
    protected SingleQuadParticle.Layer getLayer() {
        return SingleQuadParticle.Layer.TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {

        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(
                SimpleParticleType type,
                ClientLevel level,
                double x,
                double y,
                double z,
                double velocityX,
                double velocityY,
                double velocityZ,
                RandomSource random
        ) {
            BlockPos sourcePos = new BlockPos(
                    (int) velocityX,
                    (int) velocityY,
                    (int) velocityZ
            );

            return new MosquitoParticle(
                    level,
                    x,
                    y,
                    z,
                    sprites,
                    random,
                    sourcePos
            );
        }
    }
}