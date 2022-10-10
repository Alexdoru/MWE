package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.ASMLoadingPlugin;
import fr.alexdoru.megawallsenhancementsmod.asm.IMyClassTransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.InjectionStatus;
import org.objectweb.asm.tree.ClassNode;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

public class EntityArrowTransformer implements IMyClassTransformer {

    @Override
    public String getTargetClassName() {
        return "net.minecraft.entity.projectile.EntityArrow";
    }

    @Override
    public ClassNode transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(0);
        addInterface(classNode, "EntityArrowAccessor");
        classNode.visitField(ACC_PUBLIC, "pinnedToPlayer", "Z", null, 0).visitEnd();
        addGetterMethod(
                classNode,
                "isInGround",
                ASMLoadingPlugin.isObf ? "wq" : "net/minecraft/entity/projectile/EntityArrow",
                ASMLoadingPlugin.isObf ? "i" : "inGround",
                "Z",
                null);
        addGetterMethod(
                classNode,
                "isPinnedToPlayer",
                ASMLoadingPlugin.isObf ? "wq" : "net/minecraft/entity/projectile/EntityArrow",
                "pinnedToPlayer",
                "Z",
                null);
        return classNode;
    }

}
