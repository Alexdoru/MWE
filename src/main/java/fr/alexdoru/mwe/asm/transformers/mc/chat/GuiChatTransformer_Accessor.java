package fr.alexdoru.mwe.asm.transformers.mc.chat;

import fr.alexdoru.mwe.asm.mappings.FieldMapping;
import fr.alexdoru.mwe.asm.transformers.InjectionCallback;
import fr.alexdoru.mwe.asm.transformers.MWETransformer;
import org.objectweb.asm.tree.ClassNode;

public class GuiChatTransformer_Accessor implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.gui.GuiChat"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionCallback status) {
        status.setInjectionPoints(0);
        addInterface(classNode, "GuiChatAccessor");
        addGetterAndSetterMethod(classNode, "SentHistoryCursor", FieldMapping.GUICHAT$SENTHISTORYCURSOR, null);
        addGetterMethod(classNode, "getInputField", FieldMapping.GUICHAT$INPUTFIELD, null);
    }

}
