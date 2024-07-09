package fr.alexdoru.mwe.asm.transformers;

import fr.alexdoru.mwe.asm.loader.InjectionStatus;
import fr.alexdoru.mwe.asm.loader.MWETransformer;
import fr.alexdoru.mwe.asm.mappings.ClassMapping;
import fr.alexdoru.mwe.asm.mappings.FieldMapping;
import fr.alexdoru.mwe.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.*;

public class NetworkPlayerInfoTransformer_CustomTab implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.network.NetworkPlayerInfo"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(3);
        addInterface(classNode, "NetworkPlayerInfoAccessor");
        classNode.visitField(ACC_PUBLIC, FieldMapping.NETWORKPLAYERINFO$MWE$FINALKILLS.name, FieldMapping.NETWORKPLAYERINFO$MWE$FINALKILLS.desc, null, 0);
        classNode.visitField(ACC_PRIVATE, FieldMapping.NETWORKPLAYERINFO$MWE$DISPLAYNAME.name, FieldMapping.NETWORKPLAYERINFO$MWE$DISPLAYNAME.desc, null, null);
        addGetterAndSetterMethod(classNode, "FinalKills", FieldMapping.NETWORKPLAYERINFO$MWE$FINALKILLS, null);
        addSetterMethod(classNode, "setCustomDisplayname", FieldMapping.NETWORKPLAYERINFO$MWE$DISPLAYNAME, null);
        for (final MethodNode methodNode : classNode.methods) {

            if (isConstructorMethod(methodNode)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkInsnNode(insnNode, RETURN)) {
                        final InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(getNewFieldInsnNode(GETFIELD, FieldMapping.NETWORKPLAYERINFO$GAMEPROFILE));
                        list.add(new MethodInsnNode(INVOKESTATIC, getHookClass("NetworkPlayerInfoHook_CustomTab"), "getDisplayName", "(L" + ClassMapping.GAMEPROFILE + ";)L" + ClassMapping.ICHATCOMPONENT + ";", false));
                        list.add(getNewFieldInsnNode(PUTFIELD, FieldMapping.NETWORKPLAYERINFO$MWE$DISPLAYNAME));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(getNewFieldInsnNode(GETFIELD, FieldMapping.NETWORKPLAYERINFO$GAMEPROFILE));
                        list.add(new MethodInsnNode(INVOKEVIRTUAL, "com/mojang/authlib/GameProfile", "getName", "()Ljava/lang/String;", false));
                        list.add(new MethodInsnNode(INVOKESTATIC, getHookClass("NetworkPlayerInfoHook_CustomTab"), "getPlayersFinals", "(Ljava/lang/String;)I", false));
                        list.add(getNewFieldInsnNode(PUTFIELD, FieldMapping.NETWORKPLAYERINFO$MWE$FINALKILLS));
                        methodNode.instructions.insertBefore(insnNode, list);
                        status.addInjection();
                    }
                }
            }

            if (checkMethodNode(methodNode, MethodMapping.NETWORKPLAYERINFO$GETDISPLAYNAME)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkInsnNode(insnNode, ARETURN)) {
                        final InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(getNewFieldInsnNode(GETFIELD, FieldMapping.NETWORKPLAYERINFO$MWE$DISPLAYNAME));
                        list.add(new MethodInsnNode(
                                INVOKESTATIC,
                                getHookClass("NetworkPlayerInfoHook_CustomTab"),
                                "changeDisplayName",
                                "(L" + ClassMapping.ICHATCOMPONENT + ";L" + ClassMapping.ICHATCOMPONENT + ";)L" + ClassMapping.ICHATCOMPONENT + ";",
                                false
                        ));
                        methodNode.instructions.insertBefore(insnNode, list);
                        status.addInjection();
                    }
                }
            }

        }
    }

}
