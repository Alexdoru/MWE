package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.IMyClassTransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.InjectionStatus;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.ClassMapping;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.FieldMapping;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class EntityPlayerTransformer implements IMyClassTransformer {

    @Override
    public String getTargetClassName() {
        return "net.minecraft.entity.player.EntityPlayer";
    }

    @Override
    public ClassNode transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(1);
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.GETDISPLAYNAME)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkMethodInsnNode(insnNode, MethodMapping.FORMATPLAYERNAME)) {
                        /*
                         * Replaces line 2422 :
                         * ichatcomponent.appendSibling(new ChatComponentText(ScorePlayerTeam.formatPlayerName(this.getTeam(), this.getDisplayNameString())));
                         * With :
                         * ichatcomponent.appendSibling(new ChatComponentText(EntityPlayerHook.getTransformedDisplayName(ScorePlayerTeam.formatPlayerName(this.getTeam(), this.getDisplayNameString()), this.gameProfile)));
                         */
                        final InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(getNewFieldInsnNode(GETFIELD, FieldMapping.ENTITYPLAYER$GAMEPROFILE));
                        list.add(new MethodInsnNode(INVOKESTATIC, getHookClass("EntityPlayerHook"), "getTransformedDisplayName", "(Ljava/lang/String;L" + ClassMapping.GAMEPROFILE + ";)Ljava/lang/String;", false));
                        methodNode.instructions.insert(insnNode, list);
                        status.addInjection();
                    }
                }
            }
        }
        return classNode;
    }

}
