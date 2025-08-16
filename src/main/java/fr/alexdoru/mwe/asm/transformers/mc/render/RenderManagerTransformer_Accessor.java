package fr.alexdoru.mwe.asm.transformers.mc.render;

import fr.alexdoru.mwe.asm.mappings.FieldMapping;
import fr.alexdoru.mwe.asm.transformers.InjectionCallback;
import fr.alexdoru.mwe.asm.transformers.MWETransformer;
import org.objectweb.asm.tree.ClassNode;

public class RenderManagerTransformer_Accessor implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.renderer.entity.RenderManager"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionCallback status) {
        status.setInjectionPoints(0);
        addInterface(classNode, "RenderManagerAccessor");
        addGetterMethod(classNode, "isRenderOutlines", FieldMapping.RENDERMANAGER$RENDEROUTLINES, null);
    }

}
