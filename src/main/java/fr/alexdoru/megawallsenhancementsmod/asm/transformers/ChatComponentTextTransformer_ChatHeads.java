package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.loader.ASMLoadingPlugin;
import fr.alexdoru.megawallsenhancementsmod.asm.loader.InjectionStatus;
import fr.alexdoru.megawallsenhancementsmod.asm.loader.MWETransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.ClassMapping;
import org.objectweb.asm.tree.ClassNode;

import static org.objectweb.asm.Opcodes.ACC_PRIVATE;

public class ChatComponentTextTransformer_ChatHeads implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.util.ChatComponentText"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionStatus status) {
        if (ASMLoadingPlugin.isFeatherLoaded()) {
            status.skipTransformation();
            return;
        }
        status.setInjectionPoints(0);
        addInterface(classNode, "ChatComponentTextAccessor");
        final String SKIN_FIELD_NAME = "mwe$skin";
        final String SKIN_DESC = "L" + ClassMapping.SKINCHATHEAD + ";";
        classNode.visitField(ACC_PRIVATE, SKIN_FIELD_NAME, SKIN_DESC, null, null).visitEnd();
        addGetterAndSetterMethod(classNode, "SkinChatHead", ClassMapping.CHATCOMPONENTTEXT, SKIN_FIELD_NAME, SKIN_DESC, null);
    }

}
