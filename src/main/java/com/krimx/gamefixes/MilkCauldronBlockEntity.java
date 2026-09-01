package com.krimx.gamefixes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class MilkCauldronBlockEntity extends BlockEntity {

    public static final int CHEESE_TIME = 6000;

    private int heatingTicks;

    public MilkCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(Gamefixes.MILK_CAULDRON_BLOCK_ENTITY, pos, state);
    }

    public static void tick(
            Level level,
            BlockPos pos,
            BlockState state,
            MilkCauldronBlockEntity entity
    ) {
        if (level.isClientSide()) {
            return;
        }

        if (!state.is(Gamefixes.MILK_CAULDRON)
                || state.getValue(LayeredCauldronBlock.LEVEL) < 3) {
            entity.resetHeating();
            return;
        }

        BlockState below = level.getBlockState(pos.below());
        boolean heated = below.is(Blocks.CAMPFIRE)
                && below.hasProperty(CampfireBlock.LIT)
                && below.getValue(CampfireBlock.LIT);

        if (!heated) {
            entity.resetHeating();
            return;
        }

        entity.heatingTicks++;

        if (entity.heatingTicks >= CHEESE_TIME) {
            level.setBlock(
                    pos,
                    Gamefixes.CHEESE_CAULDRON.defaultBlockState()
                            .setValue(LayeredCauldronBlock.LEVEL, 3),
                    3
            );
            return;
        }

        entity.setChanged();
    }

    private void resetHeating() {
        if (this.heatingTicks != 0) {
            this.heatingTicks = 0;
            this.setChanged();
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        output.putInt("heating_ticks", this.heatingTicks);
        super.saveAdditional(output);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.heatingTicks = input.getIntOr("heating_ticks", 0);
    }
}
