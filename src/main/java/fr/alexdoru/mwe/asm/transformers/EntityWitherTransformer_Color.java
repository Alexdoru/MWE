package fr.alexdoru.mwe.asm.transformers;

import fr.alexdoru.mwe.asm.loader.InjectionStatus;
import fr.alexdoru.mwe.asm.loader.MWETransformer;
import fr.alexdoru.mwe.asm.mappings.ClassMapping;
import org.objectweb.asm.tree.ClassNode;

public class EntityWitherTransformer_Color implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.entity.boss.EntityWither"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(0);
        addInterface(classNode, "IWitherColor");
        final String colorFieldName = "mwe$witherColor";
        classNode.visitField(ACC_PRIVATE, colorFieldName, "I", null, 0);
        addGetterAndSetterMethod(classNode, "mwe$Color", ClassMapping.ENTITYWITHER, colorFieldName, "I", null);
    }

}
