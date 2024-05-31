package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.loader.InjectionStatus;
import fr.alexdoru.megawallsenhancementsmod.asm.loader.MWETransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.FieldMapping;
import org.objectweb.asm.tree.ClassNode;

public class GuiChatTransformer_Accessor implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.gui.GuiChat"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(0);
        addInterface(classNode, "GuiChatAccessor");
        addGetterAndSetterMethod(classNode, "SentHistoryCursor", FieldMapping.GUICHAT$SENTHISTORYCURSOR, null);
        addGetterMethod(classNode, "getInputField", FieldMapping.GUICHAT$INPUTFIELD, null);
    }

}
