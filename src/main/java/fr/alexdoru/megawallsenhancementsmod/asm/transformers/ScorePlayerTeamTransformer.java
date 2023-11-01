package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.loader.ASMLoadingPlugin;
import fr.alexdoru.megawallsenhancementsmod.asm.loader.InjectionStatus;
import fr.alexdoru.megawallsenhancementsmod.asm.loader.MWETransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.*;

public class ScorePlayerTeamTransformer implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.scoreboard.ScorePlayerTeam"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionStatus status) {
        if (!ASMLoadingPlugin.isFeatherLoaded()) {
            status.skipTransformation();
            return;
        }
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
