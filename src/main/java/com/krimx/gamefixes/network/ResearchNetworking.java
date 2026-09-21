package com.krimx.gamefixes.network;

import com.krimx.gamefixes.access.MerchantMenuAccess;
import com.krimx.gamefixes.research.ResearchProject;
import com.krimx.gamefixes.research.VillagerResearch;
import com.krimx.gamefixes.research.VillagerResearchData;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.game.ClientboundMerchantOffersPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import com.krimx.gamefixes.advancement.ModCriteria;

public class ResearchNetworking {

    public static void registerCommon() {

        /*
         * ========================================================
         * PAYLOAD REGISTRATION
         * ========================================================
         */

        PayloadTypeRegistry.serverboundPlay().register(
                SelectResearchSlotPayload.TYPE,
                SelectResearchSlotPayload.CODEC
        );

        PayloadTypeRegistry.serverboundPlay().register(
                AttemptResearchPayload.TYPE,
                AttemptResearchPayload.CODEC
        );

        /*
         * ========================================================
         * RESEARCH SLOT SELECTION
         * ========================================================
         */

        ServerPlayNetworking.registerGlobalReceiver(
                SelectResearchSlotPayload.TYPE,
                (payload, context) -> {

                    System.out.println("[GameFixes DEBUG] SERVER received SelectResearchSlotPayload container="
                            + payload.containerId() + " slot=" + payload.slot());

                    ServerPlayer player =
                            context.player();

                    if (player.containerMenu.containerId
                            != payload.containerId()) {
                        System.out.println("[GameFixes DEBUG] SERVER selection STOP: container mismatch actual="
                                + player.containerMenu.containerId + " payload=" + payload.containerId());
                        return;
                    }

                    if (!(player.containerMenu
                            instanceof MerchantMenu merchantMenu)) {
                        System.out.println("[GameFixes DEBUG] SERVER selection STOP: menu is not MerchantMenu");
                        return;
                    }

                    if (!(merchantMenu
                            instanceof MerchantMenuAccess access)) {
                        System.out.println("[GameFixes DEBUG] SERVER selection STOP: menu is not MerchantMenuAccess");
                        return;
                    }

                    if (!merchantMenu.stillValid(player)) {
                        System.out.println("[GameFixes DEBUG] SERVER selection STOP: menu is no longer valid");
                        return;
                    }

                    int slot =
                            payload.slot();

                    if (slot < 0
                            || slot
                            >= access.gamefixes$getResearchSlots()) {
                        System.out.println("[GameFixes DEBUG] SERVER selection STOP: invalid slot=" + slot
                                + " researchSlots=" + access.gamefixes$getResearchSlots());
                        return;
                    }

                    if (!(access.gamefixes$getTrader()
                            instanceof Villager villager)) {
                        System.out.println("[GameFixes DEBUG] SERVER selection STOP: trader is not Villager");
                        return;
                    }

                    if (!VillagerResearch
                            .isResearchSlotAvailable(
                                    villager,
                                    slot
                            )) {
                        System.out.println("[GameFixes DEBUG] SERVER selection STOP: research slot unavailable slot=" + slot);
                        return;
                    }

                    access.gamefixes$setSelectedResearchSlot(
                            slot
                    );

                    System.out.println("[GameFixes DEBUG] SERVER selection ACCEPTED slot=" + slot);
                }
        );

        /*
         * ========================================================
         * RESEARCH ATTEMPT
         * ========================================================
         */

        ServerPlayNetworking.registerGlobalReceiver(
                AttemptResearchPayload.TYPE,
                (payload, context) -> {

                    System.out.println("[GameFixes DEBUG] SERVER received AttemptResearchPayload container="
                            + payload.containerId() + " researchSlot=" + payload.researchSlot());

                    ServerPlayer player =
                            context.player();

                    /*
                     * ------------------------------------------------
                     * 1. Validate the container.
                     * ------------------------------------------------
                     */

                    if (player.containerMenu.containerId
                            != payload.containerId()) {
                        System.out.println("[GameFixes DEBUG] SERVER attempt STOP: container mismatch actual="
                                + player.containerMenu.containerId + " payload=" + payload.containerId());
                        return;
                    }

                    if (!(player.containerMenu
                            instanceof MerchantMenu merchantMenu)) {
                        System.out.println("[GameFixes DEBUG] SERVER attempt STOP: menu is not MerchantMenu");
                        return;
                    }

                    if (!(merchantMenu
                            instanceof MerchantMenuAccess access)) {
                        System.out.println("[GameFixes DEBUG] SERVER attempt STOP: menu is not MerchantMenuAccess");
                        return;
                    }

                    /*
                     * ------------------------------------------------
                     * 2. Validate the villager.
                     * ------------------------------------------------
                     */

                    if (!(access.gamefixes$getTrader()
                            instanceof Villager villager)) {
                        System.out.println("[GameFixes DEBUG] SERVER attempt STOP: trader is not Villager");
                        return;
                    }

                    if (!merchantMenu.stillValid(player)) {
                        System.out.println("[GameFixes DEBUG] SERVER attempt STOP: menu is no longer valid");
                        return;
                    }

                    if (!villager.isAlive()) {
                        System.out.println("[GameFixes DEBUG] SERVER attempt STOP: villager is not alive");
                        return;
                    }

                    /*
                     * ------------------------------------------------
                     * 3. Validate the research slot.
                     * ------------------------------------------------
                     */

                    int researchSlot =
                            payload.researchSlot();

                    if (researchSlot < 0
                            || researchSlot
                            >= access.gamefixes$getResearchSlots()) {
                        System.out.println("[GameFixes DEBUG] SERVER attempt STOP: invalid research slot=" + researchSlot
                                + " researchSlots=" + access.gamefixes$getResearchSlots());
                        return;
                    }

                    if (!VillagerResearch
                            .isResearchSlotAvailable(
                                    villager,
                                    researchSlot
                            )) {
                        System.out.println("[GameFixes DEBUG] SERVER attempt STOP: research slot unavailable slot=" + researchSlot);
                        return;
                    }

                    /*
                     * ------------------------------------------------
                     * 4. Read the actual server-side input slots.
                     * ------------------------------------------------
                     */

                    ItemStack firstInput =
                            merchantMenu
                                    .getSlot(0)
                                    .getItem();

                    ItemStack secondInput =
                            merchantMenu
                                    .getSlot(1)
                                    .getItem();

                    System.out.println("[GameFixes DEBUG] SERVER attempt INPUTS first=" + firstInput
                            + " second=" + secondInput);

                    if (firstInput.isEmpty()
                            || secondInput.isEmpty()) {

                        System.out.println("[GameFixes DEBUG] SERVER attempt STOP: one or both inputs empty");

                        access.gamefixes$setResearchFailed(
                                true
                        );

                        return;
                    }

                    /*
                     * ------------------------------------------------
                     * 5. Find the research recipe.
                     * ------------------------------------------------
                     */

                    ResearchProject project =
                            VillagerResearch.findResearch(
                                    firstInput,
                                    secondInput
                            );

                    System.out.println("[GameFixes DEBUG] SERVER attempt project="
                            + (project == null ? "null" : project.getId()));

                    if (project == null
                            || !VillagerResearch.canResearch(
                            villager,
                            project
                    )) {

                        System.out.println("[GameFixes DEBUG] SERVER attempt STOP: no project or cannot research");

                        access.gamefixes$setResearchFailed(
                                true
                        );

                        return;
                    }

                    /*
                     * ------------------------------------------------
                     * 6. Validate input counts.
                     * ------------------------------------------------
                     */

                    if (firstInput.getCount()
                            < project.getFirstInputCount()
                            || secondInput.getCount()
                            < project.getSecondInputCount()) {

                        System.out.println("[GameFixes DEBUG] SERVER attempt STOP: insufficient input counts");

                        access.gamefixes$setResearchFailed(
                                true
                        );

                        return;
                    }

                    /*
                     * ------------------------------------------------
                     * 7. Determine the normal trade count.
                     * ------------------------------------------------
                     */

                    VillagerResearchData oldData =
                            VillagerResearch.getData(villager);

                    int completedResearchCount = 0;

                    for (int i = 0;
                         i < oldData.slotCount();
                         i++) {

                        if (!oldData.isEmpty(i)) {
                            completedResearchCount++;
                        }
                    }

                    MerchantOffers offers =
                            villager.getOffers();

                    int normalTradeCount =
                            offers.size()
                                    - completedResearchCount;

                    if (normalTradeCount < 0) {
                        System.out.println("[GameFixes DEBUG] SERVER attempt STOP: normalTradeCount < 0");
                        return;
                    }

                    /*
                     * ------------------------------------------------
                     * 8. Remove the existing generated research
                     *    offers.
                     * ------------------------------------------------
                     */

                    while (offers.size()
                            > normalTradeCount) {

                        offers.remove(
                                offers.size() - 1
                        );
                    }

                    /*
                     * ------------------------------------------------
                     * 9. Consume the research inputs.
                     * ------------------------------------------------
                     */

                    firstInput.shrink(
                            project.getFirstInputCount()
                    );

                    secondInput.shrink(
                            project.getSecondInputCount()
                    );

                    merchantMenu
                            .getSlot(0)
                            .set(firstInput);

                    merchantMenu
                            .getSlot(1)
                            .set(secondInput);

                    /*
                     * ------------------------------------------------
                     * 10. Persist the completed research.
                     * ------------------------------------------------
                     */

                    VillagerResearch.setResearch(
                            villager,
                            researchSlot,
                            project
                    );

                    ModCriteria.RESEARCH.trigger(
                            player,
                            project.getId()
                    );

                    /*
                     * ------------------------------------------------
                     * 11. Rebuild generated research trades.
                     * ------------------------------------------------
                     */

                    VillagerResearchData newData =
                            VillagerResearch.getData(villager);

                    for (int i = 0;
                         i < newData.slotCount();
                         i++) {

                        ResearchProject completedProject =
                                newData.getResearch(i);

                        if (completedProject == null) {
                            continue;
                        }

                        MerchantOffer researchOffer =
                                VillagerResearch.createTrade(
                                        villager,
                                        completedProject
                                );

                        if (researchOffer == null) {
                            continue;
                        }

                        offers.add(
                                researchOffer
                        );
                    }

                    /*
                     * ------------------------------------------------
                     * 12. Update the server-side villager offers.
                     * ------------------------------------------------
                     */

                    villager.overrideOffers(
                            offers
                    );

                    /*
                     * ------------------------------------------------
                     * 13. Immediately synchronize the new offers
                     *     to the open client-side merchant menu.
                     * ------------------------------------------------
                     *
                     * Reopening the menu previously caused this
                     * synchronization to happen automatically.
                     *
                     * We now send the same vanilla merchant-offers
                     * packet immediately instead.
                     */

                    player.connection.send(
                            new ClientboundMerchantOffersPacket(
                                    merchantMenu.containerId,
                                    offers,
                                    merchantMenu.getTraderLevel(),
                                    merchantMenu.getTraderXp(),
                                    merchantMenu.showProgressBar(),
                                    merchantMenu.canRestock()
                            )
                    );

                    /*
                     * ------------------------------------------------
                     * 14. Synchronize completed research slots.
                     * ------------------------------------------------
                     */

                    int completedMask = 0;

                    for (int i = 0;
                         i < newData.slotCount();
                         i++) {

                        if (!newData.isEmpty(i)) {
                            completedMask |= (1 << i);
                        }
                    }

                    access.gamefixes$setResearchCompletedMask(
                            completedMask
                    );

                    /*
                     * ------------------------------------------------
                     * 15. Finish research mode.
                     * ------------------------------------------------
                     */

                    access.gamefixes$setResearchFailed(
                            false
                    );

                    access.gamefixes$setResearchMode(
                            false
                    );

                    System.out.println("[GameFixes DEBUG] SERVER attempt ACCEPTED/COMPLETED slot=" + researchSlot);
                }
        );
    }
}