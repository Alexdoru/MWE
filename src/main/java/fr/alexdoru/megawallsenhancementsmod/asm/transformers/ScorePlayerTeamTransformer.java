package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.ASMLoadingPlugin;
import fr.alexdoru.megawallsenhancementsmod.asm.IMyClassTransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.InjectionStatus;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class ScorePlayerTeamTransformer implements IMyClassTransformer {

    @Override
    public String getTargetClassName() {
        return "net.minecraft.scoreboard.ScorePlayerTeam";
    }

    @Override
    public ClassNode transform(ClassNode classNode, InjectionStatus status) {
        if (!ASMLoadingPlugin.isFeatherLoaded()) {
            status.setInjectionPoints(0);
        } else {
            status.setInjectionPoints(1);
            for (final MethodNode methodNode : classNode.methods) {
                if (checkMethodNode(methodNode, MethodMapping.FORMATPLAYERNAME)) {
                    final InsnList list = new InsnList();
                    list.add(new VarInsnNode(ALOAD, 1));
                    list.add(new MethodInsnNode(INVOKESTATIC, getHookClass("ScorePlayerTeamHook"), "spoofSidebarLine", "(Ljava/lang/String;)Ljava/lang/String;", false));
                    list.add((new VarInsnNode(ASTORE, 2)));
                    list.add(new VarInsnNode(ALOAD, 2));
                    final LabelNode label = new LabelNode();
                    list.add(new JumpInsnNode(IFNULL, label));
                    list.add(new VarInsnNode(ALOAD, 2));
                    list.add(new InsnNode(ARETURN));
                    list.add(label);
                    methodNode.instructions.insert(list);
                    status.addInjection();
                }
            }
        }
        return classNode;
    }

    //LINE A 9
    //ALOAD playername
    //INVOKESTATIC fr/alexdoru/megawallsenhancementsmod/asm/hooks/ScorePlayerTeamHook.spoofSidebarLine(Ljava/lang/String;)Ljava/lang/String;
    //ASTORE s
    //B:
    //LINE B 10
    //ALOAD s
    //IFNULL D
    //C:
    //LINE C 11
    //ALOAD s
    //ARETURN
    //D:
    //LINE D 13
    //ALOAD team
    //IFNONNULL E
    //ALOAD playername
    //GOTO F
    //E:
    //ALOAD team
    //ALOAD playername
    //INVOKEVIRTUAL net/minecraft/scoreboard/Team.func_142053_d(Ljava/lang/String;)Ljava/lang/String;
    //F:
    //ARETURN
    //G:

}
