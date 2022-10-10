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

        status.setInjectionPoints(0);
        addInterface(classNode, "GuiNewChatAccessor");

        {
            /*
             * Adds method :
             * public List<ChatLines> getChatLines() {
             *     return this.chatLines;
             * }
             */
            final MethodVisitor mv = classNode.visitMethod(ACC_PUBLIC, "getChatLines", "()Ljava/util/List;", ASMLoadingPlugin.isObf ? "()Ljava/util/List<Lava;>;" : "()Ljava/util/List<Lnet/minecraft/client/gui/ChatLine;>;", null);
            mv.visitCode();
            final Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, ASMLoadingPlugin.isObf ? "avt" : "net/minecraft/client/gui/GuiNewChat",  ASMLoadingPlugin.isObf ? "h" : "chatLines", "Ljava/util/List;");
            mv.visitInsn(ARETURN);
            final Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitLocalVariable("this", ASMLoadingPlugin.isObf ? "Lavt;" : "Lnet/minecraft/client/gui/GuiNewChat;", null, l0, l1, 0);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }

        {
            /*
             * Adds method :
             * public List<ChatLines> getDrawnChatLines() {
             *     return this.drawnChatLines;
             * }
             */
            final MethodVisitor mv = classNode.visitMethod(ACC_PUBLIC, "getDrawnChatLines", "()Ljava/util/List;", ASMLoadingPlugin.isObf ? "()Ljava/util/List<Lava;>;" : "()Ljava/util/List<Lnet/minecraft/client/gui/ChatLine;>;", null);
            mv.visitCode();
            final Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, ASMLoadingPlugin.isObf ? "avt" : "net/minecraft/client/gui/GuiNewChat",  ASMLoadingPlugin.isObf ? "i" : "drawnChatLines", "Ljava/util/List;");
            mv.visitInsn(ARETURN);
            final Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitLocalVariable("this", ASMLoadingPlugin.isObf ? "Lavt;" : "Lnet/minecraft/client/gui/GuiNewChat;", null, l0, l1, 0);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }

        return classNode;

    }

}
