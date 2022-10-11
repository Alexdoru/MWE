package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.ClassMapping;
import fr.alexdoru.megawallsenhancementsmod.asm.FieldMapping;
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
                FieldMapping.GUINEWCHAT$CHATLINES,
                "()Ljava/util/List<L" + ClassMapping.CHATLINE + ";>;"
        );
        addGetterMethod(
                classNode,
                "getDrawnChatLines",
                FieldMapping.GUINEWCHAT$DRAWNCHATLINES,
                "()Ljava/util/List<L" + ClassMapping.CHATLINE + ";>;"
        );
        return classNode;
    }

}
