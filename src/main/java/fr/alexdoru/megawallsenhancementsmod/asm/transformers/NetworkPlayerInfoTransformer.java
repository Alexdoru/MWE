package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.IMyClassTransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.InjectionStatus;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.ClassMapping;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.FieldMapping;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class NetworkPlayerInfoTransformer implements IMyClassTransformer {

    @Override
    public String getTargetClassName() {
        return "net.minecraft.client.network.NetworkPlayerInfo";
    }

    @Override
    public ClassNode transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(2);
        addInterface(classNode, "NetworkPlayerInfoAccessor");
        classNode.visitField(ACC_PUBLIC, "playerFinalkills", "I", null, 0).visitEnd();
        addSetterMethod(
                classNode,
                "setPlayerFinalkills",
                ClassMapping.NETWORKPLAYERINFO,
                "playerFinalkills",
                "I",
                null
        );
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.NETWORKPLAYERINFO$INIT)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkFieldInsnNode(insnNode, PUTFIELD, FieldMapping.NETWORKPLAYERINFO$DISPLAYNAME)) {
                        /*
                         * Replace line 48 with :
                         * this.displayname = NetworkPlayerInfoHook.getDisplayName(this.displayname, this.gameProfile)
                         */
                        final InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(getNewFieldInsnNode(GETFIELD, FieldMapping.NETWORKPLAYERINFO$GAMEPROFILE));
                        list.add(new MethodInsnNode(INVOKESTATIC, getHookClass("NetworkPlayerInfoHook"), "getDisplayName", "(L" + ClassMapping.ICHATCOMPONENT + ";L" + ClassMapping.GAMEPROFILE + ";)L" + ClassMapping.ICHATCOMPONENT + ";", false));
                        methodNode.instructions.insertBefore(insnNode, list);
                        status.addInjection();
                        /*
                         * Adds after line 48 :
                         * this.playersFinalKills = NetworkPlayerInfoHook.getPlayersFinals(this.gameProfile.getName())
                         */
                        final InsnList list2 = new InsnList();
                        list2.add(new VarInsnNode(ALOAD, 0));
                        list2.add(new VarInsnNode(ALOAD, 0));
                        list2.add(getNewFieldInsnNode(GETFIELD, FieldMapping.NETWORKPLAYERINFO$GAMEPROFILE));
                        list2.add(new MethodInsnNode(INVOKEVIRTUAL, "com/mojang/authlib/GameProfile", "getName", "()Ljava/lang/String;", false));
                        list2.add(new MethodInsnNode(INVOKESTATIC, getHookClass("NetworkPlayerInfoHook"), "getPlayersFinals", "(Ljava/lang/String;)I", false));
                        list2.add(new FieldInsnNode(PUTFIELD, ClassMapping.NETWORKPLAYERINFO.toString(), "playerFinalkills", "I"));
                        methodNode.instructions.insert(insnNode, list2);
                        status.addInjection();
                        return classNode;
                    }
                }
            }
        }
        return classNode;
    }

}
