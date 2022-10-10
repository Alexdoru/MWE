package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.ASMLoadingPlugin;
import fr.alexdoru.megawallsenhancementsmod.asm.IMyClassTransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.InjectionStatus;
import org.objectweb.asm.tree.ClassNode;

public class GuiNewChatTransformer implements IMyClassTransformer {

    @Override
    public String getTargetClassName() {
        return "net.minecraft.client.gui.GuiNewChat";
    }

    @Override
    public ClassNode transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(0);
        addInterface(classNode, "GuiNewChatAccessor");
        addGetterMethod(
                classNode,
                "getChatLines",
                ASMLoadingPlugin.isObf ? "avt" : "net/minecraft/client/gui/GuiNewChat",
                ASMLoadingPlugin.isObf ? "h" : "chatLines",
                "Ljava/util/List;",
                ASMLoadingPlugin.isObf ? "()Ljava/util/List<Lava;>;" : "()Ljava/util/List<Lnet/minecraft/client/gui/ChatLine;>;"
        );
        addGetterMethod(
                classNode,
                "getDrawnChatLines",
                ASMLoadingPlugin.isObf ? "avt" : "net/minecraft/client/gui/GuiNewChat",
                ASMLoadingPlugin.isObf ? "i" : "drawnChatLines",
                "Ljava/util/List;",
                ASMLoadingPlugin.isObf ? "()Ljava/util/List<Lava;>;" : "()Ljava/util/List<Lnet/minecraft/client/gui/ChatLine;>;"
        );
        return classNode;
    }

}
