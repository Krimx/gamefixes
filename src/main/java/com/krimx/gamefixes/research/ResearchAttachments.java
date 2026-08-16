package com.krimx.gamefixes.research;

import com.krimx.gamefixes.Gamefixes;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.resources.Identifier;

public final class ResearchAttachments {

    public static final AttachmentType<VillagerResearchData>
            VILLAGER_RESEARCH = AttachmentRegistry.create(
            Identifier.fromNamespaceAndPath(
                    Gamefixes.MOD_ID,
                    "villager_research"
            ),
            builder -> builder
                    .initializer(
                            () -> VillagerResearchData.empty(0)
                    )
                    .persistent(
                            VillagerResearchData.CODEC
                    )
                    .syncWith(
                            VillagerResearchData.STREAM_CODEC,
                            AttachmentSyncPredicate.all()
                    )
    );

    private ResearchAttachments() {
    }

    public static void initialize() {
        // Touches the class so the attachment is registered during mod init.
    }
}