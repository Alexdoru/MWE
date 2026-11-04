package fr.alexdoru.mwe.asm.transformers.mc.entity;

import fr.alexdoru.mwe.api.asm.InjectionCallback;
import fr.alexdoru.mwe.asm.mappings.ClassMapping;
import fr.alexdoru.mwe.asm.mappings.FieldMapping;
import fr.alexdoru.mwe.asm.transformers.MWETransformer;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.*;

public class EntityPlayerTransformer_Accessor implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.entity.player.EntityPlayer"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionCallback status) {
        addInterface(classNode, "EntityPlayerAccessor");
        final String TEAM_COLOR_FIELD_NAME = "mwe$PlayerTeamColor";
        final String TEAM_COLOR_INT_FIELD_NAME = "mwe$PlayerSpecialRenderColor";
        addField(classNode, ACC_PRIVATE, FieldMapping.ENTITYPLAYER$PLAYERDATASAMPLES);
        addField(classNode, ACC_PRIVATE, TEAM_COLOR_FIELD_NAME, "C");
        classNode.visitField(ACC_PRIVATE, TEAM_COLOR_INT_FIELD_NAME, "I", null, 0xFFFFFF); // HEX int to do white color
        addField(classNode, ACC_PRIVATE, FieldMapping.ENTITYPLAYER$MWCLASS);
        addGetterAndSetterMethod(classNode, "PlayerTeamColor", ClassMapping.ENTITYPLAYER, TEAM_COLOR_FIELD_NAME, "C");
        addGetterAndSetterMethod(classNode, "PlayerSpecialRenderColor", ClassMapping.ENTITYPLAYER, TEAM_COLOR_INT_FIELD_NAME, "I");
        addGetterAndSetterMethod(classNode, "MWClass", FieldMapping.ENTITYPLAYER$MWCLASS);
        addSetterMethod(classNode, "setPlayerDataSamples", FieldMapping.ENTITYPLAYER$PLAYERDATASAMPLES);
        addPlayerDataSamplesGetter(classNode);
        status.setInjectionPoints(1);
        for (final MethodNode methodNode : classNode.methods) {
            if (isConstructorMethod(methodNode)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkInsnNode(insnNode, RETURN)) {
                        final InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new VarInsnNode(ALOAD, 1));
                        list.add(new MethodInsnNode(
                                INVOKESTATIC,
                                getHookClass("mc/entity/EntityPlayerHook_Accessor"),
                                "createDataSample",
                                "(" + ClassMapping.WORLD.desc() + ")" + ClassMapping.PLAYERDATASAMPLES.desc(),
                                false
                        ));
                        list.add(getNewFieldInsnNode(PUTFIELD, FieldMapping.ENTITYPLAYER$PLAYERDATASAMPLES));
                        methodNode.instructions.insertBefore(insnNode, list);
                        status.addInjection();
                    }
                }
            }
        }
    }

    private void addPlayerDataSamplesGetter(ClassNode classNode) {
        final FieldMapping field = FieldMapping.ENTITYPLAYER$PLAYERDATASAMPLES;
        final MethodVisitor mv = classNode.visitMethod(ACC_PUBLIC, "getPlayerDataSamples", "()" + field.desc(), null, null);
        mv.visitCode();
        final Label start = new Label();
        mv.visitLabel(start);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, field.owner(), field.fieldName(), field.desc());
        mv.visitMethodInsn(
                INVOKESTATIC,
                getHookClass("mc/entity/EntityPlayerHook_Accessor"),
                "getPlayerDataSamples",
                "(" + ClassMapping.ENTITYPLAYER.desc() + field.desc() + ")" + field.desc(),
                false
        );
        mv.visitInsn(ARETURN);
        final Label end = new Label();
        mv.visitLabel(end);
        mv.visitLocalVariable("this", ClassMapping.ENTITYPLAYER.desc(), null, start, end, 0);
        mv.visitMaxs(2, 1);
        mv.visitEnd();
    }

}
