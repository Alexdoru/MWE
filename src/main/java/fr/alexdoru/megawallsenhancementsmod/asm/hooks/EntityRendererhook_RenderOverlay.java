package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import fr.alexdoru.megawallsenhancementsmod.gui.guiapi.GuiManager;

@SuppressWarnings("unused")
public class EntityRendererhook_RenderOverlay {
    public static void onPostRenderGameOverlay(float partialTicks) {
        GuiManager.onPostRenderGameOverlay(partialTicks);
    }
}
