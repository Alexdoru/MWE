package fr.alexdoru.mwe.asm.transformers.mc.network;

import fr.alexdoru.mwe.asm.loader.InjectionCallback;
import fr.alexdoru.mwe.asm.loader.MWETransformer;
import fr.alexdoru.mwe.asm.mappings.FieldMapping;
import org.objectweb.asm.tree.ClassNode;

public class S19PacketEntityStatusTransformer implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.network.play.server.S19PacketEntityStatus"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionCallback status) {
        status.setInjectionPoints(0);
        addInterface(classNode, "S19PacketEntityStatusAccessor");
        addGetterMethod(classNode, "getEntityId", FieldMapping.S19PACKETENTITYSTATUS$ENTITYID, "()I");
    }

}
