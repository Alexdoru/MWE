package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.ASMLoadingPlugin;
import fr.alexdoru.megawallsenhancementsmod.asm.IMyClassTransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.InjectionStatus;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.ClassNode;

import static org.objectweb.asm.Opcodes.*;

public class EntityArrowTransformer implements IMyClassTransformer {

    @Override
    public String getTargetClassName() {
        return "net.minecraft.entity.projectile.EntityArrow";
    }

    @Override
    public ClassNode transform(ClassNode classNode, InjectionStatus status) {
        classNode.interfaces.add("fr/alexdoru/megawallsenhancementsmod/asm/accessor/EntityArrowAccessor");

        {
            final FieldVisitor fieldVisitor = classNode.visitField(ACC_PUBLIC, "isPinnedToPlayer", "Z", null, 0);
            fieldVisitor.visitEnd();
        }

        {
            final MethodVisitor mv = classNode.visitMethod(ACC_PUBLIC, "isInGround", "()Z", null, null);
            mv.visitCode();
            final Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, ASMLoadingPlugin.isObf ? "wq" : "net/minecraft/entity/projectile/EntityArrow", ASMLoadingPlugin.isObf ? "i" : "inGround", "Z");
            mv.visitInsn(IRETURN);
            final Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitLocalVariable("this", ASMLoadingPlugin.isObf ? "Lwq;" : "Lnet/minecraft/entity/projectile/EntityArrow;", null, l0, l1, 0);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }

        {
            final MethodVisitor mv = classNode.visitMethod(ACC_PUBLIC, "getIsPinnedToPlayer", "()Z", null, null);
            mv.visitCode();
            final Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, ASMLoadingPlugin.isObf ? "wq" : "net/minecraft/entity/projectile/EntityArrow", "isPinnedToPlayer", "Z");
            mv.visitInsn(IRETURN);
            final Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitLocalVariable("this", ASMLoadingPlugin.isObf ? "Lwq;" : "Lnet/minecraft/entity/projectile/EntityArrow;", null, l0, l1, 0);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        return classNode;
    }

}
