package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.loader.ASMLoadingPlugin;
import fr.alexdoru.megawallsenhancementsmod.asm.loader.InjectionStatus;
import fr.alexdoru.megawallsenhancementsmod.asm.loader.MWETransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.ClassMapping;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.FieldMapping;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.ClassNode;

import static org.objectweb.asm.Opcodes.*;

public class NetworkPlayerInfoTransformer_ChatHeads implements MWETransformer {

    @Override
    public String getTargetClassName() {
        return "net.minecraft.client.network.NetworkPlayerInfo";
    }

    @Override
    public void transform(ClassNode classNode, InjectionStatus status) {
        if (ASMLoadingPlugin.isFeatherLoaded()) {
            status.skipTransformation();
            return;
        }
        status.setInjectionPoints(0);
        addInterface(classNode, "NetworkPlayerInfoAccessor_ChatHeads");
        final String SKIN_FIELD_NAME = "mwe$skin";
        final String SKIN_FIELD_DESC = "L" + ClassMapping.SKINCHATHEAD + ";";
        classNode.visitField(ACC_PRIVATE, SKIN_FIELD_NAME, SKIN_FIELD_DESC, null, null).visitEnd();
        addSetterMethod(classNode, "setSkinChatHead", ClassMapping.NETWORKPLAYERINFO, SKIN_FIELD_NAME, SKIN_FIELD_DESC, null);

        // add method :
        // public void onFinishedSkinLoading() {
        //     if(this.mwe$skin != null) {
        //      this.mwe$skin.setSkin(this.locationSkin);
        //      this.mwe$skin = null;
        //     }
        // }

        final MethodVisitor mv = classNode.visitMethod(ACC_PUBLIC, "onFinishedSkinLoading", "()V", null, null);
        mv.visitCode();
        final Label l0 = new Label();
        final Label l3 = new Label();
        mv.visitLabel(l0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, ClassMapping.NETWORKPLAYERINFO.name, SKIN_FIELD_NAME, SKIN_FIELD_DESC);
        mv.visitJumpInsn(IFNULL, l3);
        final Label l1 = new Label();
        mv.visitLabel(l1);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, ClassMapping.NETWORKPLAYERINFO.name, SKIN_FIELD_NAME, SKIN_FIELD_DESC);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, ClassMapping.NETWORKPLAYERINFO.name, FieldMapping.NETWORKPLAYERINFO$LOCATIONSKIN.name, FieldMapping.NETWORKPLAYERINFO$LOCATIONSKIN.desc);
        mv.visitMethodInsn(INVOKEVIRTUAL, ClassMapping.SKINCHATHEAD.name, "setSkin", "(L" + ClassMapping.RESOURCELOCATION + ";)V", false);
        final Label l2 = new Label();
        mv.visitLabel(l2);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitInsn(ACONST_NULL);
        mv.visitFieldInsn(PUTFIELD, ClassMapping.NETWORKPLAYERINFO.name, SKIN_FIELD_NAME, SKIN_FIELD_DESC);
        mv.visitLabel(l3);
        mv.visitInsn(RETURN);
        mv.visitLocalVariable("this", "L" + ClassMapping.NETWORKPLAYERINFO + ";", null, l0, l3, 0);
        mv.visitEnd();

    }

}
