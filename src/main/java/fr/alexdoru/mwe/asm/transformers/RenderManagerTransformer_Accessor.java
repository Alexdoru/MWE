package fr.alexdoru.mwe.asm.transformers;

import fr.alexdoru.mwe.asm.loader.InjectionStatus;
import fr.alexdoru.mwe.asm.loader.MWETransformer;
import fr.alexdoru.mwe.asm.mappings.FieldMapping;
import org.objectweb.asm.tree.ClassNode;

public class RenderManagerTransformer_Accessor implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.renderer.entity.RenderManager"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(0);
        addInterface(classNode, "RenderManagerAccessor");
        addGetterMethod(classNode, "isRenderOutlines", FieldMapping.RENDERMANAGER$RENDEROUTLINES, null);
    }

}
