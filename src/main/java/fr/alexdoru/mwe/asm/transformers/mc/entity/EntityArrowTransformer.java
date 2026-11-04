package fr.alexdoru.mwe.asm.transformers.mc.entity;

import fr.alexdoru.mwe.api.asm.InjectionCallback;
import fr.alexdoru.mwe.asm.mappings.FieldMapping;
import fr.alexdoru.mwe.asm.transformers.MWETransformer;
import org.objectweb.asm.tree.ClassNode;

public class EntityArrowTransformer implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.entity.projectile.EntityArrow"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionCallback status) {
        status.setInjectionPoints(0);
        addInterface(classNode, "EntityArrowAccessor");
        addField(classNode, ACC_PUBLIC, FieldMapping.ENTITYARROW$PINNEDENTITY);
        addField(classNode, ACC_PUBLIC, FieldMapping.ENTITYARROW$PINNEDTOENTITY);
        addGetterMethod(
                classNode,
                "mwe$isInGround",
                FieldMapping.ENTITYARROW$ISINGROUND
        );
        addGetterMethod(
                classNode,
                "mwe$PinnedEntity",
                FieldMapping.ENTITYARROW$PINNEDENTITY
        );
        addGetterMethod(
                classNode,
                "mwe$isPinnedToEntity",
                FieldMapping.ENTITYARROW$PINNEDTOENTITY
        );
    }

}
