package fr.alexdoru.mwe.asm.transformers;

import fr.alexdoru.mwe.asm.loader.InjectionCallback;
import fr.alexdoru.mwe.asm.loader.MWETransformer;
import fr.alexdoru.mwe.asm.mappings.ClassMapping;
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
        classNode.visitField(ACC_PRIVATE, FIELD_NAME, "Ljava/lang/String;", null, null);
        addGetterAndSetterMethod(classNode, FIELD_NAME, ClassMapping.CHATLINE, FIELD_NAME, "Ljava/lang/String;", null);
    }

}
