package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.gui.guiapi.GuiManager;

public class EntityRendererhook_RenderOverlay {
    public static void onPostRenderGameOverlay(float partialTicks) {
        GuiManager.onPostRenderGameOverlay(partialTicks);
    }
}
