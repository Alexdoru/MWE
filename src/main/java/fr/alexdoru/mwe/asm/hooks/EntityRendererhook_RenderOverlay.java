package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.MWE;

public class EntityRendererhook_RenderOverlay {

    public static void onPostRenderGameOverlay(float partialTicks) {
        MWE.INSTANCE().getRendererManager().onPostRenderGameOverlay(partialTicks);
    }

}
