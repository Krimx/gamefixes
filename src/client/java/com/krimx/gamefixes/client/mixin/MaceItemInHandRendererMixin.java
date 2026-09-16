package com.krimx.gamefixes.client.mixin;

/*
 * Minecraft 26.3 changed the first-person held-item rendering path.
 *
 * The previous implementation targeted:
 *
 *     net.minecraft.client.renderer.ItemInHandRenderer
 *
 * That class is no longer available under the 26.3 mappings used by
 * this project, so the old first-person mixin cannot safely be retained.
 *
 * The first-person mace animation needs to be migrated to the new
 * 26.3 rendering/submission path separately.
 */
public final class MaceItemInHandRendererMixin {
}