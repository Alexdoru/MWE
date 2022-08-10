package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.ASMLoadingPlugin;
import fr.alexdoru.megawallsenhancementsmod.asm.IMyClassTransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.InjectionStatus;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class RenderPlayerTransformer implements IMyClassTransformer {

    @Override
    public String getTargetClassName() {
        return "net.minecraft.client.renderer.entity.RenderPlayer";
    }

    @Override
    public ClassNode transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(1);
        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(ASMLoadingPlugin.isObf ? "a" : "renderOffsetLivingLabel") && methodNode.desc.equals(ASMLoadingPlugin.isObf ? "(Lbet;DDDLjava/lang/String;FD)V" : "(Lnet/minecraft/client/entity/AbstractClientPlayer;DDDLjava/lang/String;FD)V")) {
                for (AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (insnNode instanceof MethodInsnNode && insnNode.getOpcode() == INVOKEVIRTUAL
                            && ((MethodInsnNode) insnNode).owner.equals("java/lang/StringBuilder")
                            && ((MethodInsnNode) insnNode).name.equals("toString")
                            && ((MethodInsnNode) insnNode).desc.equals("()Ljava/lang/String;")) {
                        final AbstractInsnNode nextNode = insnNode.getNext();
                        if (nextNode instanceof VarInsnNode && nextNode.getOpcode() == DLOAD && ((VarInsnNode) nextNode).var == 2) {
                            /*
                             Replaces line 154 :
                             this.renderLivingLabel(entityIn, score.getScorePoints() + " " + scoreobjective.getDisplayName(), x, y, z, 64);
                             With :
                             this.renderLivingLabel(entityIn, score.getScorePoints() + " " + scoreobjective.getDisplayName() + RenderPlayerHook.getArrowCount(entityIn), x, y, z, 64);
                             */
                            InsnList list = new InsnList();
                            list.add(new VarInsnNode(ALOAD, 1));
                            list.add(new MethodInsnNode(INVOKESTATIC, "fr/alexdoru/megawallsenhancementsmod/asm/hooks/RenderPlayerHook", "getArrowCount", ASMLoadingPlugin.isObf ? "(Lbet;)Ljava/lang/String;" : "(Lnet/minecraft/client/entity/AbstractClientPlayer;)Ljava/lang/String;", false));
                            list.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false));
                            methodNode.instructions.insertBefore(insnNode, list);
                            status.addInjection();
                        }
                    }
                }
            }
        }
        return classNode;
    }

}
