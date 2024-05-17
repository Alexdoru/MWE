package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.loader.InjectionStatus;
import fr.alexdoru.megawallsenhancementsmod.asm.loader.MWETransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.ClassMapping;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.FieldMapping;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.*;

public class EntityPlayerTransformer implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.entity.player.EntityPlayer"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionStatus status) {
        addInterface(classNode, "EntityPlayerAccessor");
        final String PRESTIGE_4_TAG_FIELD_NAME = "mwe$Prestige4Tag";
        final String PRESTIGE_5_TAG_FIELD_NAME = "mwe$Prestige5Tag";
        final String TEAM_COLOR_FIELD_NAME = "mwe$PlayerTeamColor";
        final String TEAM_COLOR_INT_FIELD_NAME = "mwe$PlayerTeamColorI";
        classNode.visitField(ACC_PRIVATE, PRESTIGE_4_TAG_FIELD_NAME, "Ljava/lang/String;", null, null);
        classNode.visitField(ACC_PRIVATE, PRESTIGE_5_TAG_FIELD_NAME, "Ljava/lang/String;", null, null);
        classNode.visitField(ACC_PRIVATE, FieldMapping.ENTITYPLAYER$PLAYERDATASAMPLES.name, FieldMapping.ENTITYPLAYER$PLAYERDATASAMPLES.desc, null, null);
        classNode.visitField(ACC_PRIVATE, TEAM_COLOR_FIELD_NAME, "C", null, '\0');
        classNode.visitField(ACC_PRIVATE, TEAM_COLOR_INT_FIELD_NAME, "I", null, 0xFFFFFF); // HEX int to do white color
        classNode.visitField(ACC_PRIVATE, FieldMapping.ENTITYPLAYER$MWCLASS.name, FieldMapping.ENTITYPLAYER$MWCLASS.desc, null, null);
        addGetterAndSetterMethod(classNode, "Prestige4Tag", ClassMapping.ENTITYPLAYER, PRESTIGE_4_TAG_FIELD_NAME, "Ljava/lang/String;", null);
        addGetterAndSetterMethod(classNode, "Prestige5Tag", ClassMapping.ENTITYPLAYER, PRESTIGE_5_TAG_FIELD_NAME, "Ljava/lang/String;", null);
        addGetterAndSetterMethod(classNode, "PlayerTeamColor", ClassMapping.ENTITYPLAYER, TEAM_COLOR_FIELD_NAME, "C", null);
        addGetterAndSetterMethod(classNode, "PlayerTeamColorInt", ClassMapping.ENTITYPLAYER, TEAM_COLOR_INT_FIELD_NAME, "I", null);
        addGetterAndSetterMethod(classNode, "MWClass", FieldMapping.ENTITYPLAYER$MWCLASS, null);
        addGetterMethod(classNode, "getPlayerDataSamples", FieldMapping.ENTITYPLAYER$PLAYERDATASAMPLES, null);
        status.setInjectionPoints(2);
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
            } else if (checkMethodNode(methodNode, MethodMapping.ENTITYPLAYER$GETDISPLAYNAME)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkMethodInsnNode(insnNode, MethodMapping.SCOREPLAYERTEAM$FORMATPLAYERNAME)) {
                        /*
                         * Replaces line 2422 :
                         * ichatcomponent.appendSibling(new ChatComponentText(ScorePlayerTeam.formatPlayerName(this.getTeam(), this.getDisplayNameString())));
                         * With :
                         * ichatcomponent.appendSibling(new ChatComponentText(EntityPlayerHook.getTransformedDisplayName(ScorePlayerTeam.formatPlayerName(this.getTeam(), this.getDisplayNameString()), this)));
                         */
                        final InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new MethodInsnNode(INVOKESTATIC, getHookClass("EntityPlayerHook"), "getTransformedDisplayName", "(Ljava/lang/String;L" + ClassMapping.ENTITYPLAYER + ";)Ljava/lang/String;", false));
                        methodNode.instructions.insert(insnNode, list);
                        status.addInjection();
                    }
                }
            }
        }
    }

}
