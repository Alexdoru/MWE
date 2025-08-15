package fr.alexdoru.mwe.asm.transformers;

import fr.alexdoru.mwe.asm.loader.InjectionCallback;
import fr.alexdoru.mwe.asm.loader.MWETransformer;
import fr.alexdoru.mwe.asm.mappings.FieldMapping;
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
