package fr.alexdoru.mwe.asm.transformers.mc.gui;

import fr.alexdoru.mwe.api.asm.InjectionCallback;
import fr.alexdoru.mwe.asm.mappings.FieldMapping;
import org.objectweb.asm.tree.ClassNode;

public class GuiPlayerTabOverlayTransformer_PlayerCount extends GuiPlayerTabOverlayTransformer_HeaderModifier {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.gui.GuiPlayerTabOverlay"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionCallback status) {
        status.setInjectionPoints(1);
        injectAfterHeaderCheck(classNode, status, FieldMapping.GUIPLAYERTABOVERLAY$HEADER, "GuiPlayerTabOverlayHook_PlayerCount");
    }

}
