package fr.alexdoru.mwe.asm.transformers;

import fr.alexdoru.mwe.asm.loader.InjectionStatus;
import fr.alexdoru.mwe.asm.loader.MWETransformer;
import fr.alexdoru.mwe.asm.mappings.ClassMapping;
import fr.alexdoru.mwe.asm.mappings.FieldMapping;
import org.objectweb.asm.tree.ClassNode;

public class GuiNewChatTransformer_Accessor implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.gui.GuiNewChat"};
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
