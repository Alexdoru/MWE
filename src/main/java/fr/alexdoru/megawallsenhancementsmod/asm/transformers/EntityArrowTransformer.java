package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.loader.InjectionStatus;
import fr.alexdoru.megawallsenhancementsmod.asm.loader.MWETransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.FieldMapping;
import org.objectweb.asm.tree.ClassNode;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

public class EntityArrowTransformer implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.entity.projectile.EntityArrow"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(0);
        addInterface(classNode, "EntityArrowAccessor");
        classNode.visitField(ACC_PUBLIC, FieldMapping.ENTITYARROW$PINNEDTOPLAYER.name, FieldMapping.ENTITYARROW$PINNEDTOPLAYER.desc, null, 0).visitEnd();
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
