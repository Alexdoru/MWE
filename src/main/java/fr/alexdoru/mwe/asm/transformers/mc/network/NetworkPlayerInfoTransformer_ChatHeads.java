package fr.alexdoru.mwe.asm.transformers.mc.network;

import fr.alexdoru.mwe.api.asm.InjectionCallback;
import fr.alexdoru.mwe.asm.MWELoadingPlugin;
import fr.alexdoru.mwe.asm.mappings.ClassMapping;
import fr.alexdoru.mwe.asm.mappings.FieldMapping;
import fr.alexdoru.mwe.asm.transformers.MWETransformer;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.ClassNode;

public class NetworkPlayerInfoTransformer_ChatHeads implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.network.NetworkPlayerInfo"};
    }

    @Override
    public boolean shouldApply(ClassNode classNode) {
        return !MWELoadingPlugin.isFeatherPresent();
    }

    @Override
    public void transform(ClassNode classNode, InjectionCallback status) {
        status.setInjectionPoints(0);
        addInterface(classNode, "NetworkPlayerInfoAccessor_ChatHeads");
        addField(classNode, ACC_PRIVATE, FieldMapping.NETWORKPLAYERINFO$MWE$SKIN);
        addSetterMethod(classNode, "setSkinChatHead", FieldMapping.NETWORKPLAYERINFO$MWE$SKIN);

        // add method :
        // public void mwe$onFinishedSkinLoading() {
        //     if(this.mwe$skin != null) {
        //      this.mwe$skin.setSkin(this.locationSkin);
        //      this.mwe$skin = null;
        //     }
        // }

        final String SKIN_FIELD_NAME = FieldMapping.NETWORKPLAYERINFO$MWE$SKIN.name;
        final String SKIN_FIELD_DESC = FieldMapping.NETWORKPLAYERINFO$MWE$SKIN.desc;

        final MethodVisitor mv = classNode.visitMethod(ACC_PUBLIC, "mwe$onFinishedSkinLoading", "()V", null, null);
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
        mv.visitMethodInsn(INVOKEVIRTUAL, ClassMapping.SKINCHATHEAD.name, "setSkin", "(" + ClassMapping.RESOURCELOCATION.desc() + ")V", false);
        final Label l2 = new Label();
        mv.visitLabel(l2);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitInsn(ACONST_NULL);
        mv.visitFieldInsn(PUTFIELD, ClassMapping.NETWORKPLAYERINFO.name, SKIN_FIELD_NAME, SKIN_FIELD_DESC);
        mv.visitLabel(l3);
        mv.visitInsn(RETURN);
        mv.visitLocalVariable("this", ClassMapping.NETWORKPLAYERINFO.desc(), null, l0, l3, 0);
        mv.visitMaxs(2, 1);
        mv.visitEnd();

    }

}
