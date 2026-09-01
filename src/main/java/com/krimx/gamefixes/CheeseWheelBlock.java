package com.krimx.gamefixes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CheeseWheelBlock extends Block {

    public static final IntegerProperty BITES =
            IntegerProperty.create("bites", 0, 5);

    private static final VoxelShape[] SHAPES = new VoxelShape[]{
            Block.box(1, 0, 1, 15, 8, 15),
            Block.box(3, 0, 1, 15, 8, 15),
            Block.box(5, 0, 1, 15, 8, 15),
            Block.box(7, 0, 1, 15, 8, 15),
            Block.box(9, 0, 1, 15, 8, 15),
            Block.box(11, 0, 1, 15, 8, 15)
    };

    public CheeseWheelBlock(Properties properties) {
        super(properties);

        registerDefaultState(
                stateDefinition.any().setValue(BITES, 0)
        );
    }

    @Override
    protected void createBlockStateDefinition(
            StateDefinition.Builder<Block, BlockState> builder
    ) {
        builder.add(BITES);
    }

    @Override
    protected VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return SHAPES[state.getValue(BITES)];
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hitResult
    ) {
        if (!player.canEat(false)) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide()) {
            int bites = state.getValue(BITES);

            player.getFoodData().eat(
                    2,
                    0.3F
            );

            if (bites >= 5) {
                level.removeBlock(pos, false);
            } else {
                level.setBlock(
                        pos,
                        state.setValue(BITES, bites + 1),
                        3
                );
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    protected BlockState updateShape(
            BlockState state,
            LevelReader level,
            net.minecraft.world.level.ScheduledTickAccess scheduledTickAccess,
            BlockPos pos,
            net.minecraft.core.Direction direction,
            BlockPos neighborPos,
            BlockState neighborState,
            net.minecraft.util.RandomSource random
    ) {
        if (direction == net.minecraft.core.Direction.DOWN
                && !canSupportCenter(level, pos.below())) {
            return Blocks.AIR.defaultBlockState();
        }

        return state;
    }

    private static boolean canSupportCenter(
            BlockGetter level,
            BlockPos pos
    ) {
        return Block.isFaceFull(
                level.getBlockState(pos).getShape(level, pos),
                net.minecraft.core.Direction.UP
        );
    }
}