package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.IMyClassTransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.InjectionStatus;
import org.objectweb.asm.tree.ClassNode;

import static org.objectweb.asm.Opcodes.ACC_PRIVATE;

public class GameProfileTransformer implements IMyClassTransformer {

    @Override
    public String getTargetClassName() {
        return "com.mojang.authlib.GameProfile";
    }

    @Override
    public ClassNode transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(0);
        addInterface(classNode, "GameProfileAccessor");
        final String MWPlayerData_desc = "Lfr/alexdoru/megawallsenhancementsmod/data/MWPlayerData;";
        classNode.visitField(ACC_PRIVATE, "mwPlayerData", MWPlayerData_desc, null, null).visitEnd();
        addGetterMethod(
                classNode,
                "getMWPlayerData",
                "com/mojang/authlib/GameProfile",
                "mwPlayerData",
                MWPlayerData_desc,
                null);
        addSetterMethod(
                classNode,
                "setMWPlayerData",
                "com/mojang/authlib/GameProfile",
                "mwPlayerData",
                MWPlayerData_desc,
                null);
        return classNode;
    }

}
