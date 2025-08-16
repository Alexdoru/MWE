package fr.alexdoru.mwe.asm.transformers.mc.entity;

import fr.alexdoru.mwe.asm.mappings.FieldMapping;
import fr.alexdoru.mwe.asm.transformers.InjectionCallback;
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
        classNode.visitField(ACC_PUBLIC, FieldMapping.ENTITYARROW$PINNEDTOPLAYER.name, FieldMapping.ENTITYARROW$PINNEDTOPLAYER.desc, null, 0);
        addGetterMethod(
                classNode,
                "isInGround",
                FieldMapping.ENTITYARROW$ISINGROUND,
                null
        );
        addGetterMethod(
                classNode,
                "isPinnedToPlayer",
                FieldMapping.ENTITYARROW$PINNEDTOPLAYER,
                null
        );
    }

}
