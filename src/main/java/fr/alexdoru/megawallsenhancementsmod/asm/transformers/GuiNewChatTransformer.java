package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.loader.InjectionStatus;
import fr.alexdoru.megawallsenhancementsmod.asm.loader.MWETransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.ClassMapping;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.FieldMapping;
import org.objectweb.asm.tree.ClassNode;

public class GuiNewChatTransformer implements MWETransformer {

    @Override
    public String getTargetClassName() {
        return "net.minecraft.client.gui.GuiNewChat";
    }

    @Override
    public void transform(ClassNode classNode, InjectionStatus status) {
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
    }

}
