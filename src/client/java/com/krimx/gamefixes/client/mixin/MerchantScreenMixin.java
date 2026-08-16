package com.krimx.gamefixes.client.mixin;

import com.krimx.gamefixes.access.MerchantMenuAccess;
import com.krimx.gamefixes.network.AttemptResearchPayload;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MerchantScreen.class)
public abstract class MerchantScreenMixin {

    private static final int GUI_WIDTH = 276;
    private static final int GUI_HEIGHT = 166;

    private static final int VISIBLE_ROWS = 7;
    private static final int ROW_HEIGHT = 20;

    private static final int LIST_X = 5;
    private static final int LIST_WIDTH = 88;

    @Unique
    private static final int GAMEFIXES_RESEARCH_ICON_WIDTH = 16;

    @Unique
    private static final int GAMEFIXES_RESEARCH_ICON_HEIGHT = 16;

    /*
     * =========================================================
     * SPRITES
     * =========================================================
     */

    @Unique
    private static final Identifier GAMEFIXES_RESEARCH_ICON =
            Identifier.fromNamespaceAndPath(
                    "gamefixes",
                    "textures/gui/research_icon.png"
            );

    @Unique
    private static final Identifier GAMEFIXES_RESEARCH_FAILED_ICON =
            Identifier.fromNamespaceAndPath(
                    "gamefixes",
                    "textures/gui/research_failed.png"
            );

    @Unique
    private static final Identifier GAMEFIXES_BUTTON_SPRITE =
            Identifier.withDefaultNamespace(
                    "widget/button"
            );

    @Unique
    private static final Identifier GAMEFIXES_BUTTON_HIGHLIGHTED_SPRITE =
            Identifier.withDefaultNamespace(
                    "widget/button_highlighted"
            );

    @Unique
    private static final Identifier GAMEFIXES_SCROLLER_SPRITE =
            Identifier.withDefaultNamespace(
                    "container/villager/scroller"
            );

    @Unique
    private static final Identifier GAMEFIXES_SCROLLER_DISABLED_SPRITE =
            Identifier.withDefaultNamespace(
                    "container/villager/scroller_disabled"
            );

    @Unique
    private static final Identifier GAMEFIXES_TRADE_ARROW_SPRITE =
            Identifier.withDefaultNamespace(
                    "container/villager/trade_arrow"
            );

    @Unique
    private static final Identifier GAMEFIXES_TRADE_ARROW_OUT_OF_STOCK_SPRITE =
            Identifier.withDefaultNamespace(
                    "container/villager/trade_arrow_out_of_stock"
            );

    @Unique
    private static final Identifier GAMEFIXES_DISCOUNT_STRIKETHROUGH_SPRITE =
            Identifier.withDefaultNamespace(
                    "container/villager/discount_strikethrough"
            );

    /*
     * =========================================================
     * VANILLA STATE
     * =========================================================
     */

    @Shadow
    private int scrollOff;

    @Shadow
    private boolean isDragging;

    /*
     * =========================================================
     * RESEARCH BUTTON
     * =========================================================
     *
     * These are the values you have already adjusted.
     */

    @Unique
    private static final int GAMEFIXES_RESEARCH_BUTTON_X = 215;

    @Unique
    private static final int GAMEFIXES_RESEARCH_BUTTON_Y = 32;

    @Unique
    private static final int GAMEFIXES_RESEARCH_BUTTON_WIDTH = 26;

    @Unique
    private static final int GAMEFIXES_RESEARCH_BUTTON_HEIGHT = 26;

    /*
     * =========================================================
     * BASIC RESEARCH STATE
     * =========================================================
     */

    @Unique
    private int gamefixes$getResearchSlots() {
        MerchantScreen screen =
                (MerchantScreen) (Object) this;

        if (!(screen.getMenu()
                instanceof MerchantMenuAccess access)) {
            return 0;
        }

        return access.gamefixes$getResearchSlots();
    }

    @Unique
    private boolean gamefixes$isResearchCompleted(
            int researchSlot
    ) {
        MerchantScreen screen =
                (MerchantScreen) (Object) this;

        if (!(screen.getMenu()
                instanceof MerchantMenuAccess access)) {
            return false;
        }

        return access.gamefixes$isResearchSlotCompleted(
                researchSlot
        );
    }

    /*
     * =========================================================
     * UNIFIED LIST
     * =========================================================
     *
     * The list is always:
     *
     *   normal trade 0
     *   normal trade 1
     *   ...
     *   research slot 0
     *   research slot 1
     *   ...
     *
     * A research slot never disappears.
     *
     * If it is empty:
     *
     *   Research
     *
     * If it is completed:
     *
     *   completed research trade
     */

    @Unique
    private int gamefixes$getCompletedResearchCount() {
        int completed = 0;

        for (int i = 0;
             i < gamefixes$getResearchSlots();
             i++) {

            if (gamefixes$isResearchCompleted(i)) {
                completed++;
            }
        }

        return completed;
    }

    @Unique
    private int gamefixes$getNormalTradeCount() {
        MerchantScreen screen =
                (MerchantScreen) (Object) this;

        MerchantOffers offers =
                screen.getMenu().getOffers();

        /*
         * The merchant's offer list contains:
         *
         * normal trades
         * +
         * generated research trades
         *
         * Remove the generated research trades from the
         * count to determine where the research-slot section
         * begins.
         */
        return Math.max(
                0,
                offers.size()
                        - gamefixes$getCompletedResearchCount()
        );
    }

    @Unique
    private int gamefixes$getTotalEntries() {
        /*
         * Every research slot occupies exactly one list entry,
         * whether it is completed or not.
         */
        return gamefixes$getNormalTradeCount()
                + gamefixes$getResearchSlots();
    }

    @Unique
    private int gamefixes$getMaxScroll() {
        return Math.max(
                0,
                gamefixes$getTotalEntries()
                        - VISIBLE_ROWS
        );
    }

    @Unique
    private boolean gamefixes$canScroll() {
        return gamefixes$getTotalEntries()
                > VISIBLE_ROWS;
    }

    /*
     * Find the generated MerchantOffer belonging to a
     * particular research slot.
     *
     * Generated research offers are stored at the END of the
     * merchant's offer list in research-slot order, skipping
     * empty research slots.
     */
    @Unique
    private int gamefixes$getResearchOfferIndex(
            int researchSlot
    ) {
        int normalTradeCount =
                gamefixes$getNormalTradeCount();

        int completedBefore = 0;

        for (int i = 0;
             i < researchSlot;
             i++) {

            if (gamefixes$isResearchCompleted(i)) {
                completedBefore++;
            }
        }

        return normalTradeCount
                + completedBefore;
    }

    /*
     * =========================================================
     * RESEARCH BUTTON
     * =========================================================
     */

    @Unique
    private boolean gamefixes$isResearchButtonHovered(
            double mouseX,
            double mouseY
    ) {
        MerchantScreen screen =
                (MerchantScreen) (Object) this;

        if (!(screen.getMenu()
                instanceof MerchantMenuAccess access)) {
            return false;
        }

        if (!access.gamefixes$isResearchMode()) {
            return false;
        }

        int xo =
                (screen.width - GUI_WIDTH) / 2;

        int yo =
                (screen.height - GUI_HEIGHT) / 2;

        return mouseX >= xo + GAMEFIXES_RESEARCH_BUTTON_X
                && mouseX
                < xo
                + GAMEFIXES_RESEARCH_BUTTON_X
                + GAMEFIXES_RESEARCH_BUTTON_WIDTH
                && mouseY >= yo + GAMEFIXES_RESEARCH_BUTTON_Y
                && mouseY
                < yo
                + GAMEFIXES_RESEARCH_BUTTON_Y
                + GAMEFIXES_RESEARCH_BUTTON_HEIGHT;
    }

    @Unique
    private void gamefixes$attemptResearch() {
        MerchantScreen screen =
                (MerchantScreen) (Object) this;

        if (!(screen.getMenu()
                instanceof MerchantMenuAccess access)) {
            return;
        }

        if (!access.gamefixes$isResearchMode()) {
            return;
        }

        int selectedSlot =
                access.gamefixes$getSelectedResearchSlot();

        if (selectedSlot < 0) {
            return;
        }

        ClientPlayNetworking.send(
                new AttemptResearchPayload(
                        screen.getMenu().containerId,
                        selectedSlot
                )
        );
    }

    /*
     * =========================================================
     * MAIN LIST RENDERING
     * =========================================================
     */

    @Inject(
            method = "extractContents",
            at = @At("TAIL")
    )
    private void gamefixes$drawCombinedList(
            GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY,
            float partialTick,
            CallbackInfo ci
    ) {
        MerchantScreen screen =
                (MerchantScreen) (Object) this;

        if (!(screen.getMenu()
                instanceof MerchantMenuAccess access)) {
            return;
        }

        int researchSlots =
                access.gamefixes$getResearchSlots();

        if (researchSlots <= 0) {
            return;
        }

        MerchantOffers offers =
                screen.getMenu().getOffers();

        int normalTradeCount =
                gamefixes$getNormalTradeCount();

        int totalEntries =
                normalTradeCount
                        + researchSlots;

        int xo =
                (screen.width - GUI_WIDTH) / 2;

        int yo =
                (screen.height - GUI_HEIGHT) / 2;

        /*
         * Clear the complete seven-row list area.
         */
        graphics.fill(
                xo + LIST_X,
                yo + 17,
                xo + LIST_X + LIST_WIDTH,
                yo + 17
                        + VISIBLE_ROWS * ROW_HEIGHT,
                0xFF8B8B8B
        );

        /*
         * Render every visible entry.
         */
        for (int visibleRow = 0;
             visibleRow < VISIBLE_ROWS;
             visibleRow++) {

            int logicalIndex =
                    scrollOff + visibleRow;

            if (logicalIndex >= totalEntries) {
                continue;
            }

            int rowY =
                    yo + 17
                            + visibleRow * ROW_HEIGHT;

            /*
             * -----------------------------------------------------
             * NORMAL TRADE
             * -----------------------------------------------------
             */

            if (logicalIndex < normalTradeCount) {

                if (logicalIndex >= offers.size()) {
                    continue;
                }

                gamefixes$drawTrade(
                        graphics,
                        screen,
                        offers.get(logicalIndex),
                        xo,
                        rowY,
                        mouseX,
                        mouseY
                );

                continue;
            }

            /*
             * -----------------------------------------------------
             * RESEARCH SLOT
             * -----------------------------------------------------
             */

            int researchSlot =
                    logicalIndex
                            - normalTradeCount;

            /*
             * Completed research slots become normal-looking
             * trade entries.
             */
            if (gamefixes$isResearchCompleted(
                    researchSlot
            )) {

                int offerIndex =
                        gamefixes$getResearchOfferIndex(
                                researchSlot
                        );

                if (offerIndex >= 0
                        && offerIndex < offers.size()) {

                    gamefixes$drawTrade(
                            graphics,
                            screen,
                            offers.get(offerIndex),
                            xo,
                            rowY,
                            mouseX,
                            mouseY
                    );
                }

                continue;
            }

            /*
             * Empty research slot.
             */
            gamefixes$drawResearchRow(
                    graphics,
                    screen,
                    access,
                    researchSlot,
                    xo,
                    rowY,
                    mouseX,
                    mouseY
            );
        }

        /*
         * =========================================================
         * RESEARCH BUTTON
         * =========================================================
         */

        if (access.gamefixes$isResearchMode()) {

            int buttonX =
                    xo + GAMEFIXES_RESEARCH_BUTTON_X;

            int buttonY =
                    yo + GAMEFIXES_RESEARCH_BUTTON_Y;

            boolean hovered =
                    gamefixes$isResearchButtonHovered(
                            mouseX,
                            mouseY
                    );

            graphics.blitSprite(
                    RenderPipelines.GUI_TEXTURED,
                    hovered
                            ? GAMEFIXES_BUTTON_HIGHLIGHTED_SPRITE
                            : GAMEFIXES_BUTTON_SPRITE,
                    buttonX,
                    buttonY,
                    GAMEFIXES_RESEARCH_BUTTON_WIDTH,
                    GAMEFIXES_RESEARCH_BUTTON_HEIGHT
            );

            /*
             * Center the research icon automatically.
             */
            int iconX =
                    buttonX
                            + (
                            GAMEFIXES_RESEARCH_BUTTON_WIDTH
                                    - GAMEFIXES_RESEARCH_ICON_WIDTH
                    ) / 2;

            int iconY =
                    buttonY
                            + (
                            GAMEFIXES_RESEARCH_BUTTON_HEIGHT
                                    - GAMEFIXES_RESEARCH_ICON_HEIGHT
                    ) / 2;

            graphics.blit(
                    RenderPipelines.GUI_TEXTURED,
                    GAMEFIXES_RESEARCH_ICON,
                    iconX,
                    iconY,
                    0,
                    0,
                    GAMEFIXES_RESEARCH_ICON_WIDTH,
                    GAMEFIXES_RESEARCH_ICON_HEIGHT,
                    GAMEFIXES_RESEARCH_ICON_WIDTH,
                    GAMEFIXES_RESEARCH_ICON_HEIGHT
            );

            /*
             * Research failure indicator.
             *
             * This is a standalone 9x9 texture with transparency,
             * so it does not include the gray background from the
             * vanilla out-of-stock trade-arrow sprite.
             */
            if (access.gamefixes$researchFailed()) {

                int failureX =
                        xo + 192;

                int failureY =
                        yo + 41;

                graphics.blit(
                        RenderPipelines.GUI_TEXTURED,
                        GAMEFIXES_RESEARCH_FAILED_ICON,
                        failureX,
                        failureY,
                        0,
                        0,
                        9,
                        9,
                        9,
                        9
                );
            }
        }
    }

    /*
     * =========================================================
     * RESEARCH ROW
     * =========================================================
     */

    @Unique
    private void gamefixes$drawResearchRow(
            GuiGraphicsExtractor graphics,
            MerchantScreen screen,
            MerchantMenuAccess access,
            int researchSlot,
            int xo,
            int rowY,
            int mouseX,
            int mouseY
    ) {
        boolean hovered =
                mouseX >= xo + LIST_X
                        && mouseX
                        < xo + LIST_X + LIST_WIDTH
                        && mouseY >= rowY
                        && mouseY
                        < rowY + ROW_HEIGHT;

        boolean selected =
                access.gamefixes$getSelectedResearchSlot()
                        == researchSlot;

        graphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                selected || hovered
                        ? GAMEFIXES_BUTTON_HIGHLIGHTED_SPRITE
                        : GAMEFIXES_BUTTON_SPRITE,
                xo + LIST_X,
                rowY,
                LIST_WIDTH,
                ROW_HEIGHT
        );

        graphics.text(
                screen.getFont(),
                selected
                        ? "Researching"
                        : "Research",
                xo + 12,
                rowY + 6,
                0xFFFFFFFF,
                false
        );
    }

    /*
     * =========================================================
     * TRADE ROW
     * =========================================================
     */

    @Unique
    private void gamefixes$drawTrade(
            GuiGraphicsExtractor graphics,
            MerchantScreen screen,
            MerchantOffer offer,
            int xo,
            int rowY,
            int mouseX,
            int mouseY
    ) {
        boolean hovered =
                mouseX >= xo + LIST_X
                        && mouseX
                        < xo + LIST_X + LIST_WIDTH
                        && mouseY >= rowY
                        && mouseY
                        < rowY + ROW_HEIGHT;

        graphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                hovered
                        ? GAMEFIXES_BUTTON_HIGHLIGHTED_SPRITE
                        : GAMEFIXES_BUTTON_SPRITE,
                xo + LIST_X,
                rowY,
                LIST_WIDTH,
                ROW_HEIGHT
        );

        int decorHeight =
                rowY + 2;

        int sellItem1X =
                xo + 10;

        ItemStack baseCostA =
                offer.getBaseCostA();

        ItemStack costA =
                offer.getCostA();

        ItemStack costB =
                offer.getCostB();

        ItemStack result =
                offer.getResult();

        /*
         * First input.
         */
        graphics.fakeItem(
                costA,
                sellItem1X,
                decorHeight
        );

        if (baseCostA.getCount()
                == costA.getCount()) {

            graphics.itemDecorations(
                    screen.getFont(),
                    costA,
                    sellItem1X,
                    decorHeight
            );

        } else {

            graphics.itemDecorations(
                    screen.getFont(),
                    baseCostA,
                    sellItem1X,
                    decorHeight,
                    baseCostA.getCount() == 1
                            ? "1"
                            : null
            );

            graphics.itemDecorations(
                    screen.getFont(),
                    costA,
                    sellItem1X + 14,
                    decorHeight,
                    costA.getCount() == 1
                            ? "1"
                            : null
            );

            graphics.blitSprite(
                    RenderPipelines.GUI_TEXTURED,
                    GAMEFIXES_DISCOUNT_STRIKETHROUGH_SPRITE,
                    sellItem1X + 7,
                    decorHeight + 12,
                    9,
                    2
            );
        }

        /*
         * Second input.
         */
        if (!costB.isEmpty()) {

            graphics.fakeItem(
                    costB,
                    xo + 40,
                    decorHeight
            );

            graphics.itemDecorations(
                    screen.getFont(),
                    costB,
                    xo + 40,
                    decorHeight
            );
        }

        /*
         * Arrow.
         */
        graphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                offer.isOutOfStock()
                        ? GAMEFIXES_TRADE_ARROW_OUT_OF_STOCK_SPRITE
                        : GAMEFIXES_TRADE_ARROW_SPRITE,
                xo + 60,
                decorHeight + 3,
                10,
                9
        );

        /*
         * Output.
         */
        graphics.fakeItem(
                result,
                xo + 73,
                decorHeight
        );

        graphics.itemDecorations(
                screen.getFont(),
                result,
                xo + 73,
                decorHeight
        );
    }

    /*
     * =========================================================
     * SCROLLING
     * =========================================================
     */

    @Inject(
            method = "mouseScrolled",
            at = @At("HEAD"),
            cancellable = true
    )
    private void gamefixes$mouseScrolled(
            double x,
            double y,
            double scrollX,
            double scrollY,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (!gamefixes$canScroll()) {
            return;
        }

        scrollOff =
                Mth.clamp(
                        (int) (scrollOff - scrollY),
                        0,
                        gamefixes$getMaxScroll()
                );

        cir.setReturnValue(true);
    }

    @Inject(
            method = "mouseDragged",
            at = @At("HEAD"),
            cancellable = true
    )
    private void gamefixes$mouseDragged(
            MouseButtonEvent event,
            double dx,
            double dy,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (!isDragging
                || !gamefixes$canScroll()) {
            return;
        }

        MerchantScreen screen =
                (MerchantScreen) (Object) this;

        int yo =
                (screen.height - GUI_HEIGHT) / 2;

        int fullScrollTopPos =
                yo + 18;

        int fullScrollBottomPos =
                fullScrollTopPos + 139;

        int maxScrollOff =
                gamefixes$getMaxScroll();

        float scrolling =
                ((float) event.y()
                        - fullScrollTopPos
                        - 13.5F)
                        / (
                        fullScrollBottomPos
                                - fullScrollTopPos
                                - 27.0F
                );

        scrolling =
                scrolling * maxScrollOff + 0.5F;

        scrollOff =
                Mth.clamp(
                        (int) scrolling,
                        0,
                        maxScrollOff
                );

        cir.setReturnValue(true);
    }

    /*
     * =========================================================
     * MOUSE CLICKING
     * =========================================================
     */

    @Inject(
            method = "mouseClicked",
            at = @At("HEAD"),
            cancellable = true
    )
    private void gamefixes$mouseClicked(
            MouseButtonEvent event,
            boolean doubleClick,
            CallbackInfoReturnable<Boolean> cir
    ) {
        MerchantScreen screen =
                (MerchantScreen) (Object) this;

        if (!(screen.getMenu()
                instanceof MerchantMenuAccess access)) {
            return;
        }

        if (event.button() != 0) {
            return;
        }

        /*
         * Research button.
         */
        if (gamefixes$isResearchButtonHovered(
                event.x(),
                event.y()
        )) {

            gamefixes$attemptResearch();

            cir.setReturnValue(true);
            return;
        }

        /*
         * Research rows.
         */
        int researchSlots =
                access.gamefixes$getResearchSlots();

        if (researchSlots > 0) {

            int normalTradeCount =
                    gamefixes$getNormalTradeCount();

            int xo =
                    (screen.width - GUI_WIDTH) / 2;

            int yo =
                    (screen.height - GUI_HEIGHT) / 2;

            for (int researchSlot = 0;
                 researchSlot < researchSlots;
                 researchSlot++) {

                /*
                 * Completed slots are trades now and cannot be
                 * selected for another research.
                 */
                if (gamefixes$isResearchCompleted(
                        researchSlot
                )) {
                    continue;
                }

                /*
                 * Every research slot has exactly one logical
                 * position in the combined list.
                 */
                int logicalIndex =
                        normalTradeCount
                                + researchSlot;

                int visibleRow =
                        logicalIndex
                                - scrollOff;

                if (visibleRow < 0
                        || visibleRow >= VISIBLE_ROWS) {
                    continue;
                }

                int rowY =
                        yo + 17
                                + visibleRow * ROW_HEIGHT;

                if (event.x() >= xo + LIST_X
                        && event.x()
                        < xo + LIST_X + LIST_WIDTH
                        && event.y() >= rowY
                        && event.y()
                        < rowY + ROW_HEIGHT) {

                    access.gamefixes$setSelectedResearchSlot(
                            researchSlot
                    );

                    access.gamefixes$setResearchMode(
                            true
                    );

                    access.gamefixes$setResearchFailed(
                            false
                    );

                    cir.setReturnValue(true);
                    return;
                }
            }
        }

        /*
         * Scrollbar.
         */
        if (gamefixes$canScroll()) {

            int xo =
                    (screen.width - GUI_WIDTH) / 2;

            int yo =
                    (screen.height - GUI_HEIGHT) / 2;

            if (event.x() > xo + 94
                    && event.x() < xo + 100
                    && event.y() > yo + 18
                    && event.y() <= yo + 159) {

                isDragging = true;

                cir.setReturnValue(true);
            }
        }
    }

    /*
     * =========================================================
     * SCROLLBAR
     * =========================================================
     */

    @Inject(
            method = "extractScroller",
            at = @At("HEAD"),
            cancellable = true
    )
    private void gamefixes$extractScroller(
            GuiGraphicsExtractor graphics,
            int xo,
            int yo,
            int mouseX,
            int mouseY,
            MerchantOffers offers,
            CallbackInfo ci
    ) {
        int totalEntries =
                gamefixes$getTotalEntries();

        int steps =
                totalEntries + 1
                        - VISIBLE_ROWS;

        if (steps > 1) {

            int leftOver =
                    139
                            - (
                            27
                                    + (steps - 1)
                                    * 139
                                    / steps
                    );

            int stepHeight =
                    1
                            + leftOver / steps
                            + 139 / steps;

            int scrollerYOff =
                    Math.min(
                            113,
                            scrollOff * stepHeight
                    );

            if (scrollOff == steps - 1) {
                scrollerYOff = 113;
            }

            int scrollerX =
                    xo + 94;

            int scrollerY =
                    yo + 18
                            + scrollerYOff;

            graphics.blitSprite(
                    RenderPipelines.GUI_TEXTURED,
                    GAMEFIXES_SCROLLER_SPRITE,
                    scrollerX,
                    scrollerY,
                    6,
                    27
            );

            if (mouseX >= scrollerX
                    && mouseX < scrollerX + 6
                    && mouseY >= scrollerY
                    && mouseY <= scrollerY + 27) {

                graphics.requestCursor(
                        isDragging
                                ? CursorTypes.RESIZE_NS
                                : CursorTypes.POINTING_HAND
                );
            }

        } else {

            graphics.blitSprite(
                    RenderPipelines.GUI_TEXTURED,
                    GAMEFIXES_SCROLLER_DISABLED_SPRITE,
                    xo + 94,
                    yo + 18,
                    6,
                    27
            );
        }

        /*
         * Prevent vanilla from rendering its own scrollbar
         * using only the normal trade count.
         */
        ci.cancel();
    }
}