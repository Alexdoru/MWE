package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.ASMLoadingPlugin;
import fr.alexdoru.megawallsenhancementsmod.asm.IMyClassTransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.InjectionStatus;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class EntityFXTransformer implements IMyClassTransformer {

    @Override
    public String getTargetClassName() {
        return "net.minecraft.client.particle.EntityFX";
    }

    @Override
    public ClassNode transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(1);
        for (final MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(ASMLoadingPlugin.isObf ? "a" : "renderParticle") && methodNode.desc.equals(ASMLoadingPlugin.isObf ? "(Lbfd;Lpk;FFFFFF)V" : "(Lnet/minecraft/client/renderer/WorldRenderer;Lnet/minecraft/entity/Entity;FFFFFF)V")) {
                /*
                 * Injects at HEAD:
                 * if(EntityFXHook.shouldHideParticle(this, entityIn)) {
                 *    return;
                 * }
                 */
                methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), hideParticleInsnList());
                status.addInjection();
                break;
            }
        }
        return classNode;
    }

    private InsnList hideParticleInsnList() {
        final InsnList list = new InsnList();
        final LabelNode notCanceled = new LabelNode();
        list.add(new VarInsnNode(ALOAD, 0));
        list.add(new VarInsnNode(ALOAD, 2));
        list.add(new MethodInsnNode(INVOKESTATIC, getHookClass("EntityFXHook"), "shouldHideParticle", ASMLoadingPlugin.isObf ? "(Lbeb;Lpk;)Z" : "(Lnet/minecraft/client/particle/EntityFX;Lnet/minecraft/entity/Entity;)Z", false));
        list.add(new JumpInsnNode(IFEQ, notCanceled));
        list.add(new InsnNode(RETURN));
        list.add(notCanceled);
        return list;
    }

}
