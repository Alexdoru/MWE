package fr.alexdoru.mwe.asm.transformers.mc.entity;

import fr.alexdoru.mwe.api.asm.InjectionCallback;
import fr.alexdoru.mwe.asm.mappings.ClassMapping;
import fr.alexdoru.mwe.asm.mappings.FieldMapping;
import fr.alexdoru.mwe.asm.transformers.MWETransformer;
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
        addGetterMethod(classNode, "getPlayerDataSamples", FieldMapping.ENTITYPLAYER$PLAYERDATASAMPLES);
        status.setInjectionPoints(1);
        for (final MethodNode methodNode : classNode.methods) {
            if (isConstructorMethod(methodNode)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkInsnNode(insnNode, RETURN)) {
                        final InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new TypeInsnNode(NEW, ClassMapping.PLAYERDATASAMPLES.name));
                        list.add(new InsnNode(DUP));
                        list.add(new MethodInsnNode(INVOKESPECIAL, ClassMapping.PLAYERDATASAMPLES.name, "<init>", "()V", false));
                        list.add(getNewFieldInsnNode(PUTFIELD, FieldMapping.ENTITYPLAYER$PLAYERDATASAMPLES));
                        methodNode.instructions.insertBefore(insnNode, list);
                        status.addInjection();
                    }
                }
            }
        }
    }

}
