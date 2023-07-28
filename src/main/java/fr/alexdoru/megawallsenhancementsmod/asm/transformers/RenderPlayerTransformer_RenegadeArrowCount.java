package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.loader.IMyClassTransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.loader.InjectionStatus;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.ClassMapping;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

public class RenderPlayerTransformer_RenegadeArrowCount implements IMyClassTransformer {

    @Override
    public String getTargetClassName() {
        return "net.minecraft.client.renderer.entity.RenderPlayer";
    }

    @Override
    public void transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(1);
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.RENDEROFFSETLIVINGLABEL)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkMethodInsnNode(insnNode, MethodMapping.SCOREOBJECTIVE$GETDISPLAYNAME)) {
                        final AbstractInsnNode secondNode = insnNode.getNext();
                        if (checkMethodInsnNode(secondNode, MethodMapping.STRINGBUILDER$APPEND_STRING)) {
                            final AbstractInsnNode thirdNode = secondNode.getNext();
                            if (checkMethodInsnNode(thirdNode, MethodMapping.STRINGBUILDER$TOSTRING)) {
                                /*
                                 * Replaces line 154 :
                                 * this.renderLivingLabel(entityIn, score.getScorePoints() + " " + scoreobjective.getDisplayName(), x, y, z, 64);
                                 * With :
                                 * this.renderLivingLabel(entityIn, score.getScorePoints() + " " + scoreobjective.getDisplayName() + RenderPlayerHook_RenegadeArrowCount.getArrowCount(entityIn), x, y, z, 64);
                                 */
                                final InsnList list = new InsnList();
                                list.add(new VarInsnNode(ALOAD, 1));
                                list.add(new MethodInsnNode(INVOKESTATIC, getHookClass("RenderPlayerHook_RenegadeArrowCount"), "getArrowCount", "(Ljava/lang/StringBuilder;L" + ClassMapping.ABSTRACTCLIENTPLAYER + ";)Ljava/lang/StringBuilder;", false));
                                methodNode.instructions.insert(secondNode, list);
                                status.addInjection();
                            }
                        }
                    }
                }
            }
        }
    }

}
