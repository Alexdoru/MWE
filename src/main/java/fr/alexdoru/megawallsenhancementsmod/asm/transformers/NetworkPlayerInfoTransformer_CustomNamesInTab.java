package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.loader.InjectionStatus;
import fr.alexdoru.megawallsenhancementsmod.asm.loader.MWETransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.ClassMapping;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.FieldMapping;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.MethodMapping;
import org.objectweb.asm.Label;
import org.objectweb.asm.tree.*;

public class NetworkPlayerInfoTransformer_CustomNamesInTab implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.network.NetworkPlayerInfo"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(1);
        addInterface(classNode, "NetworkPlayerInfoAccessor");
        final String FIELD_NAME_FINAL_KILLS = "mwenhancements$playerFinalkills";
        final String FIELD_NAME_DISPLAYNAME = "mwenhancements$displayName";
        classNode.visitField(ACC_PUBLIC, FIELD_NAME_FINAL_KILLS, "I", null, 0).visitEnd();
        classNode.visitField(ACC_PRIVATE, FIELD_NAME_DISPLAYNAME, "L" + ClassMapping.ICHATCOMPONENT + ";", null, null).visitEnd();
        addGetterAndSetterMethod(
                classNode,
                "PlayerFinalkills",
                ClassMapping.NETWORKPLAYERINFO,
                FIELD_NAME_FINAL_KILLS,
                "I",
                null
        );
        addSetterMethod(
                classNode,
                "setCustomDisplayname",
                ClassMapping.NETWORKPLAYERINFO,
                FIELD_NAME_DISPLAYNAME,
                "L" + ClassMapping.ICHATCOMPONENT + ";",
                null
        );
        for (final MethodNode methodNode : classNode.methods) {

            if (checkMethodNode(methodNode, MethodMapping.NETWORKPLAYERINFO$INIT)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkFieldInsnNode(insnNode, PUTFIELD, FieldMapping.NETWORKPLAYERINFO$DISPLAYNAME)) {
                        /*
                         * Adds after line 48 :
                         * this.mwenhancements$displayName = NetworkPlayerInfoHook.getDisplayName(this.displayname, this.gameProfile);
                         * this.mwenhancements$playerFinalkills = NetworkPlayerInfoHook.getPlayersFinals(this.gameProfile.getName());
                         */
                        final InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(getNewFieldInsnNode(GETFIELD, FieldMapping.NETWORKPLAYERINFO$GAMEPROFILE));
                        list.add(new MethodInsnNode(INVOKESTATIC, getHookClass("NetworkPlayerInfoHook"), "getDisplayName", "(L" + ClassMapping.GAMEPROFILE + ";)L" + ClassMapping.ICHATCOMPONENT + ";", false));
                        list.add(new FieldInsnNode(PUTFIELD, ClassMapping.NETWORKPLAYERINFO.toString(), FIELD_NAME_DISPLAYNAME, "L" + ClassMapping.ICHATCOMPONENT + ";"));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(getNewFieldInsnNode(GETFIELD, FieldMapping.NETWORKPLAYERINFO$GAMEPROFILE));
                        list.add(new MethodInsnNode(INVOKEVIRTUAL, "com/mojang/authlib/GameProfile", "getName", "()Ljava/lang/String;", false));
                        list.add(new MethodInsnNode(INVOKESTATIC, getHookClass("NetworkPlayerInfoHook"), "getPlayersFinals", "(Ljava/lang/String;)I", false));
                        list.add(new FieldInsnNode(PUTFIELD, ClassMapping.NETWORKPLAYERINFO.toString(), FIELD_NAME_FINAL_KILLS, "I"));
                        methodNode.instructions.insert(insnNode, list);
                        status.addInjection();
                    }
                }
            }

            if (checkMethodNode(methodNode, MethodMapping.NETWORKPLAYERINFO$GETDISPLAYNAME)) {
                methodNode.instructions.clear();
                methodNode.localVariables.clear();
                /*
                 * public IChatComponent getDisplayName() {
                 *     return this.displayName != null ? this.displayName : this.mwenhancements$displayName;
                 * }
                 */
                methodNode.visitCode();

                final Label l0 = new Label();
                final Label isNullLabel = new Label();
                methodNode.visitLabel(l0);
                methodNode.visitVarInsn(ALOAD, 0);
                methodNode.visitFieldInsn(GETFIELD, ClassMapping.NETWORKPLAYERINFO.toString(), FieldMapping.NETWORKPLAYERINFO$DISPLAYNAME.name, "L" + ClassMapping.ICHATCOMPONENT + ";");
                methodNode.visitJumpInsn(IFNULL, isNullLabel);

                final Label l1 = new Label();
                methodNode.visitLabel(l1);
                methodNode.visitVarInsn(ALOAD, 0);
                methodNode.visitFieldInsn(GETFIELD, ClassMapping.NETWORKPLAYERINFO.toString(), FieldMapping.NETWORKPLAYERINFO$DISPLAYNAME.name, "L" + ClassMapping.ICHATCOMPONENT + ";");
                methodNode.visitInsn(ARETURN);

                methodNode.visitLabel(isNullLabel);
                methodNode.visitVarInsn(ALOAD, 0);
                methodNode.visitFieldInsn(GETFIELD, ClassMapping.NETWORKPLAYERINFO.toString(), FIELD_NAME_DISPLAYNAME, "L" + ClassMapping.ICHATCOMPONENT + ";");
                methodNode.visitInsn(ARETURN);

                final Label l2 = new Label();
                methodNode.visitLabel(l2);
                methodNode.visitLocalVariable("this", "L" + ClassMapping.NETWORKPLAYERINFO + ";", null, l0, l2, 0);
                methodNode.visitMaxs(1, 1);
                methodNode.visitEnd();
            }

        }
    }

}
