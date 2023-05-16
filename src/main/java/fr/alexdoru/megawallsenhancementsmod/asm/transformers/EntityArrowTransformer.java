package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.IMyClassTransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.InjectionStatus;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.ClassMapping;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.FieldMapping;
import org.objectweb.asm.tree.ClassNode;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

public class EntityArrowTransformer implements IMyClassTransformer {

    @Override
    public String getTargetClassName() {
        return "net.minecraft.entity.projectile.EntityArrow";
    }

    @Override
    public void transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(0);
        addInterface(classNode, "EntityArrowAccessor");
        classNode.visitField(ACC_PUBLIC, "pinnedToPlayer", "Z", null, 0).visitEnd();
        addGetterMethod(
                classNode,
                "isInGround",
                FieldMapping.ENTITYARROW$ISINGROUND,
                null
        );
        addGetterMethod(
                classNode,
                "isPinnedToPlayer",
                ClassMapping.ENTITYARROW,
                "pinnedToPlayer",
                "Z",
                null
        );
    }

}
