package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.IMyClassTransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.InjectionStatus;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.ClassNode;

import static org.objectweb.asm.Opcodes.*;

public class GameProfileTransformer implements IMyClassTransformer {

    @Override
    public String getTargetClassName() {
        return "com.mojang.authlib.GameProfile";
    }

    @Override
    public ClassNode transform(ClassNode classNode, InjectionStatus status) {

        status.setInjectionPoints(0);
        classNode.interfaces.add("fr/alexdoru/megawallsenhancementsmod/asm/accessor/GameProfileAccessor");
        final String MWPlayerData_desc = "Lfr/alexdoru/megawallsenhancementsmod/data/MWPlayerData;";
        classNode.visitField(ACC_PRIVATE, "mwPlayerData", MWPlayerData_desc, null, null).visitEnd();

        {
            final MethodVisitor mv = classNode.visitMethod(ACC_PUBLIC, "getMWPlayerData", "()" + MWPlayerData_desc, null, null);
            mv.visitCode();
            final Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "com/mojang/authlib/GameProfile", "mwPlayerData", MWPlayerData_desc);
            mv.visitInsn(ARETURN);
            final Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitLocalVariable("this", "Lcom/mojang/authlib/GameProfile;", null, l0, l1, 0);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }

        {
            final MethodVisitor mv = classNode.visitMethod(ACC_PUBLIC, "setMWPlayerData", "(" + MWPlayerData_desc + ")V", null, null);
            mv.visitCode();
            final Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitFieldInsn(PUTFIELD, "com/mojang/authlib/GameProfile", "mwPlayerData", MWPlayerData_desc);
            final Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitInsn(RETURN);
            final Label l2 = new Label();
            mv.visitLabel(l2);
            mv.visitLocalVariable("this", "Lcom/mojang/authlib/GameProfile;", null, l0, l2, 0);
            mv.visitLocalVariable("mwPlayerDataIn", MWPlayerData_desc, null, l0, l2, 1);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }

        return classNode;
    }

}
