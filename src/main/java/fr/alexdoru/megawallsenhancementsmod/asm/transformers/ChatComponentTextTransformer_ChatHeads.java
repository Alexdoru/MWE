package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.ASMLoadingPlugin;
import fr.alexdoru.megawallsenhancementsmod.asm.IMyClassTransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.InjectionStatus;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.ClassMapping;
import org.objectweb.asm.tree.ClassNode;

import static org.objectweb.asm.Opcodes.ACC_PRIVATE;

public class ChatComponentTextTransformer_ChatHeads implements IMyClassTransformer {

    @Override
    public String getTargetClassName() {
        return "net.minecraft.util.ChatComponentText";
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
        classNode.visitField(ACC_PRIVATE, SKIN_FIELD_NAME, "L" + ClassMapping.RESOURCELOCATION + ";", null, null).visitEnd();
        addGetterAndSetterMethod(classNode, "Skin", ClassMapping.CHATCOMPONENTTEXT, SKIN_FIELD_NAME, "L" + ClassMapping.RESOURCELOCATION + ";", null);
    }

}
