package fr.alexdoru.mwe.asm.transformers.mc.chat;

import fr.alexdoru.mwe.asm.MWELoadingPlugin;
import fr.alexdoru.mwe.asm.mappings.ClassMapping;
import fr.alexdoru.mwe.asm.transformers.InjectionCallback;
import fr.alexdoru.mwe.asm.transformers.MWETransformer;
import org.objectweb.asm.tree.ClassNode;

public class ChatComponentTextTransformer_ChatHeads implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.util.ChatComponentText"};
    }

    @Override
    public boolean shouldApply(ClassNode classNode) {
        return !MWELoadingPlugin.isFeatherPresent();
    }

    @Override
    public void transform(ClassNode classNode, InjectionCallback status) {
        status.setInjectionPoints(0);
        addInterface(classNode, "ChatComponentTextAccessor");
        final String SKIN_FIELD_NAME = "mwe$skin";
        final String SKIN_DESC = "L" + ClassMapping.SKINCHATHEAD + ";";
        classNode.visitField(ACC_PRIVATE, SKIN_FIELD_NAME, SKIN_DESC, null, null);
        addGetterAndSetterMethod(classNode, "SkinChatHead", ClassMapping.CHATCOMPONENTTEXT, SKIN_FIELD_NAME, SKIN_DESC, null);
    }

}
