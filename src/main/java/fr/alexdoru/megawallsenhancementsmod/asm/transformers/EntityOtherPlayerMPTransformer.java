package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.IMyClassTransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.InjectionStatus;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.ClassMapping;
import org.objectweb.asm.tree.ClassNode;

import static org.objectweb.asm.Opcodes.ACC_PRIVATE;

public class EntityOtherPlayerMPTransformer implements IMyClassTransformer {

    @Override
    public String getTargetClassName() {
        return "net.minecraft.client.entity.EntityOtherPlayerMP";
    }

    @Override
    public ClassNode transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(0);
        addInterface(classNode, "EntityOtherPlayerMPAccessor");
        final String fieldName = "mwenhancements$PlayerTeamColor";
        classNode.visitField(ACC_PRIVATE, fieldName, "C", null, '\0').visitEnd();
        addGetterAndSetterMethod(classNode,
                "PlayerTeamColor",
                ClassMapping.ENTITYOTHERPLAYERMP,
                fieldName,
                "C",
                null
        );
        return classNode;
    }

}
