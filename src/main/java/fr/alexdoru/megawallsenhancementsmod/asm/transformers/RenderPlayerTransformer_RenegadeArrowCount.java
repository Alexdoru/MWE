package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.IMyClassTransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.InjectionStatus;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.ClassMapping;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class RenderPlayerTransformer_RenegadeArrowCount implements IMyClassTransformer {

    @Override
    public String getTargetClassName() {
        return "net.minecraft.client.renderer.entity.RenderPlayer";
    }

    @Override
    public ClassNode transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(1);
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.RENDEROFFSETLIVINGLABEL)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkMethodInsnNode(insnNode, MethodMapping.STRINGBUILDER$TOSTRING) && checkVarInsnNode(insnNode.getNext(), DLOAD, 2)) {
                        /*
                         * Replaces line 154 :
                         * this.renderLivingLabel(entityIn, score.getScorePoints() + " " + scoreobjective.getDisplayName(), x, y, z, 64);
                         * With :
                         * this.renderLivingLabel(entityIn, score.getScorePoints() + " " + scoreobjective.getDisplayName() + RenderPlayerHook_RenegadeArrowCount.getArrowCount(entityIn), x, y, z, 64);
                         */
                        final InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 1));
                        list.add(new MethodInsnNode(INVOKESTATIC, getHookClass("RenderPlayerHook_RenegadeArrowCount"), "getArrowCount", "(L" + ClassMapping.ABSTRACTCLIENTPLAYER + ";)Ljava/lang/String;", false));
                        list.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false));
                        methodNode.instructions.insertBefore(insnNode, list);
                        status.addInjection();
                    }
                }
            }
        }
        return classNode;
    }

}
