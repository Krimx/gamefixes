package com.krimx.gamefixes.mixin;

import com.krimx.gamefixes.FuelSpeedRegistry;
import com.krimx.gamefixes.FurnaceSpeedAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceBlockEntityMixin implements FurnaceSpeedAccess {
    // In 1.21.4+, litTime was renamed to litTimeRemaining
    @Shadow int litTimeRemaining;
    @Shadow int cookingTimer;
    @Shadow int cookingTotalTime;

    @Unique
    private int currentFuelSpeed = 1;

    @Override
    public int gamefixes$getFuelSpeed() {
        return currentFuelSpeed;
    }

    @Override
    public void gamefixes$setFuelSpeed(int speed) {
        this.currentFuelSpeed = speed;
    }

    // 1. Capture fuel speed at tick start if furnace is unlit or about to ignite new fuel
    @Inject(method = "serverTick", at = @At("HEAD"))
    private static void captureFuelSpeed(ServerLevel level, BlockPos pos, BlockState state, AbstractFurnaceBlockEntity blockEntity, CallbackInfo ci) {
        AbstractFurnaceBlockEntityMixin furnace = (AbstractFurnaceBlockEntityMixin) (Object) blockEntity;

        if (furnace.litTimeRemaining <= 0) {
            ItemStack fuelStack = blockEntity.getItem(1); // Slot 1 is the fuel slot
            if (!fuelStack.isEmpty()) {
                furnace.currentFuelSpeed = FuelSpeedRegistry.getSpeed(fuelStack.getItem());
            } else {
                furnace.currentFuelSpeed = 1;
            }
        }
    }

    // 2. Accelerate cooking progress based on active fuel speed
    @Inject(method = "serverTick", at = @At("TAIL"))
    private static void accelerateCooking(ServerLevel level, BlockPos pos, BlockState state, AbstractFurnaceBlockEntity blockEntity, CallbackInfo ci) {
        FurnaceSpeedAccess access = (FurnaceSpeedAccess) blockEntity;
        int speed = access.gamefixes$getFuelSpeed();

        if (speed > 1 && state.hasProperty(BlockStateProperties.LIT) && state.getValue(BlockStateProperties.LIT)) {
            AbstractFurnaceBlockEntityMixin furnace = (AbstractFurnaceBlockEntityMixin) (Object) blockEntity;
            if (furnace.cookingTimer > 0 && furnace.cookingTimer < furnace.cookingTotalTime) {
                // Capped at cookingTotalTime - 1 so vanilla can trigger the completion event naturally
                furnace.cookingTimer = Math.min(furnace.cookingTotalTime - 1, furnace.cookingTimer + (speed - 1));
            }
        }
    }

    // 3. Save fuel speed state to disk when chunk unloads / game saves
    @Inject(method = "saveAdditional", at = @At("TAIL"))
    private void saveFuelSpeed(ValueOutput output, CallbackInfo ci) {
        output.putInt("GamefixesFuelSpeed", this.currentFuelSpeed);
    }

    // 4. Restore fuel speed state from disk when chunk loads
    @Inject(method = "loadAdditional", at = @At("TAIL"))
    private void loadFuelSpeed(ValueInput input, CallbackInfo ci) {
        this.currentFuelSpeed = input.getIntOr("GamefixesFuelSpeed", 1);
    }
}