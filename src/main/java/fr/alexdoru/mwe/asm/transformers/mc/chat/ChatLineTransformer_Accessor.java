package fr.alexdoru.mwe.asm.transformers.mc.chat;

import fr.alexdoru.mwe.api.asm.InjectionCallback;
import fr.alexdoru.mwe.asm.mappings.ClassMapping;
import fr.alexdoru.mwe.asm.transformers.MWETransformer;
import org.objectweb.asm.tree.ClassNode;

public class ChatLineTransformer_Accessor implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.gui.ChatLine"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionCallback status) {
        status.setInjectionPoints(0);
        addInterface(classNode, "ChatLineAccessor");
        final String FIELD_NAME = "mwe$Text";
        addField(classNode, ACC_PRIVATE, FIELD_NAME, "Ljava/lang/String;");
        addGetterAndSetterMethod(classNode, FIELD_NAME, ClassMapping.CHATLINE, FIELD_NAME, "Ljava/lang/String;");
    }

}
