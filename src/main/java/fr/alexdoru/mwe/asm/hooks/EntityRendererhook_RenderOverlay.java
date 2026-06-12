package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.gui.HUDRenderer;

public class EntityRendererhook_RenderOverlay {

    public static void onPostRenderGameOverlay(float partialTicks) {
        HUDRenderer.onPostRenderGameOverlay(partialTicks);
    }

}
