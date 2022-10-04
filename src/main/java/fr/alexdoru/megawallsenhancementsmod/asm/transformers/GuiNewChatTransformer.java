package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.ASMLoadingPlugin;
import fr.alexdoru.megawallsenhancementsmod.asm.IMyClassTransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.InjectionStatus;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.ClassNode;

import static org.objectweb.asm.Opcodes.*;

public class GuiNewChatTransformer implements IMyClassTransformer {

    @Override
    public String getTargetClassName() {
        return "net.minecraft.client.gui.GuiNewChat";
    }

    @Override
    public ClassNode transform(ClassNode classNode, InjectionStatus status) {

        classNode.interfaces.add("fr/alexdoru/megawallsenhancementsmod/asm/accessor/GuiNewChatAccessor");

        {
            /*
             * Adds method :
             *
             * public void deleteWarningMessagesFor(String playername) {
             *     GuiNewChatHook.deleteWarningMessagesFor(this, playername, this.drawnChatLines, this.chatLines);
             * }
             *
             */
            MethodVisitor mv = classNode.visitMethod(ACC_PUBLIC, "deleteWarningMessagesFor", "(Ljava/lang/String;)V", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, ASMLoadingPlugin.isObf ? "avt" : "net/minecraft/client/gui/GuiNewChat", ASMLoadingPlugin.isObf ? "i" : "drawnChatLines", "Ljava/util/List;");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, ASMLoadingPlugin.isObf ? "avt" : "net/minecraft/client/gui/GuiNewChat", ASMLoadingPlugin.isObf ? "h" : "chatLines", "Ljava/util/List;");
            mv.visitMethodInsn(INVOKESTATIC, "fr/alexdoru/megawallsenhancementsmod/asm/hooks/GuiNewChatHook", "deleteWarningMessagesFor", ASMLoadingPlugin.isObf ? "(Lavt;Ljava/lang/String;Ljava/util/List;Ljava/util/List;)V" : "(Lnet/minecraft/client/gui/GuiNewChat;Ljava/lang/String;Ljava/util/List;Ljava/util/List;)V", false);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitInsn(RETURN);
            Label l2 = new Label();
            mv.visitLabel(l2);
            mv.visitLocalVariable("this", ASMLoadingPlugin.isObf ? "Lavt;" : "Lnet/minecraft/client/gui/GuiNewChat;", null, l0, l2, 0);
            mv.visitLocalVariable("playername", "Ljava/lang/String;", null, l0, l2, 1);
            mv.visitMaxs(5, 2);
            mv.visitEnd();
        }

        {
            /*
             * Adds method :
             *
             * public void deleteAllWarningMessages() {
             *     GuiNewChatHook.deleteAllWarningMessages(this, this.drawnChatLines, this.chatLines);
             * }
             *
             */
            MethodVisitor mv = classNode.visitMethod(ACC_PUBLIC, "deleteAllWarningMessages", "()V", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, ASMLoadingPlugin.isObf ? "avt" : "net/minecraft/client/gui/GuiNewChat", ASMLoadingPlugin.isObf ? "i" : "drawnChatLines", "Ljava/util/List;");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, ASMLoadingPlugin.isObf ? "avt" : "net/minecraft/client/gui/GuiNewChat", ASMLoadingPlugin.isObf ? "h" : "chatLines", "Ljava/util/List;");
            mv.visitMethodInsn(INVOKESTATIC, "fr/alexdoru/megawallsenhancementsmod/asm/hooks/GuiNewChatHook", "deleteAllWarningMessages", ASMLoadingPlugin.isObf ? "(Lavt;Ljava/util/List;Ljava/util/List;)V" : "(Lnet/minecraft/client/gui/GuiNewChat;Ljava/util/List;Ljava/util/List;)V", false);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitInsn(RETURN);
            Label l2 = new Label();
            mv.visitLabel(l2);
            mv.visitLocalVariable("this", ASMLoadingPlugin.isObf ? "Lavt;" : "Lnet/minecraft/client/gui/GuiNewChat;", null, l0, l2, 0);
            mv.visitMaxs(4, 1);
            mv.visitEnd();
        }

        {
            MethodVisitor mv = classNode.visitMethod(ACC_PUBLIC, "getChatLines", "()Ljava/util/List;", ASMLoadingPlugin.isObf ? "()Ljava/util/List<Lava;>;" : "()Ljava/util/List<Lnet/minecraft/client/gui/ChatLine;>;", null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, ASMLoadingPlugin.isObf ? "avt" : "net/minecraft/client/gui/GuiNewChat",  ASMLoadingPlugin.isObf ? "h" : "chatLines", "Ljava/util/List;");
            mv.visitInsn(ARETURN);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitLocalVariable("this", ASMLoadingPlugin.isObf ? "Lavt;" : "Lnet/minecraft/client/gui/GuiNewChat;", null, l0, l1, 0);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }

        return classNode;

    }

}
