package com.krimx.gamefixes.mixin;

import com.krimx.gamefixes.Gamefixes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Bee.class)
public class BeeMixin {

    @Inject(
            method = "mobInteract",
            at = @At("HEAD"),
            cancellable = true
    )
    private void gamefixes$harvestPollen(
            Player player,
            InteractionHand hand,
            CallbackInfoReturnable<InteractionResult> cir
    ) {
        Bee bee = (Bee) (Object) this;
        ItemStack stack = player.getItemInHand(hand);

        if (stack.is(Items.SHEARS) && bee.hasNectar()) {

            bee.dropOffNectar();

            ItemStack pollen = new ItemStack(Gamefixes.POLLEN);

            if (!bee.level().isClientSide()) {
                bee.level().addFreshEntity(
                        new net.minecraft.world.entity.item.ItemEntity(
                                bee.level(),
                                bee.getX(),
                                bee.getY(),
                                bee.getZ(),
                                pollen
                        )
                );
            }

            if (!player.getAbilities().instabuild) {
                stack.hurtAndBreak(
                        1,
                        player,
                        hand == InteractionHand.MAIN_HAND
                                ? EquipmentSlot.MAINHAND
                                : EquipmentSlot.OFFHAND
                );
            }

            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }
}