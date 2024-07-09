package fr.alexdoru.mwe.asm.transformers;

import fr.alexdoru.mwe.asm.loader.InjectionStatus;
import fr.alexdoru.mwe.asm.loader.MWETransformer;
import fr.alexdoru.mwe.asm.mappings.FieldMapping;
import org.objectweb.asm.tree.*;

import java.util.HashMap;
import java.util.Map;

import static fr.alexdoru.mwe.asm.mappings.ClassMapping.*;

public class HitboxRenderTransformer implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{
                "net.minecraft.client.entity.AbstractClientPlayer",
                "net.minecraft.entity.projectile.EntityArrow",
                "net.minecraft.entity.item.EntityItem",
                "net.minecraft.entity.passive.EntityAnimal",
                "net.minecraft.entity.item.EntityItemFrame"
        };
    }

    @Override
    public void transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(0);
        addInterface(classNode, "IHitboxRender");
        final Map<String, String> map = new HashMap<>();
        map.put(ABSTRACTCLIENTPLAYER.name, "drawHitboxForPlayers");
        map.put(ENTITYITEM.name, "drawHitboxForDroppedItems");
        map.put(ENTITYANIMAL.name, "drawHitboxForPassiveMobs");
        map.put(ENTITYITEMFRAME.name, "drawHitboxItemFrame");
        if (map.containsKey(classNode.name)) {
            final MethodNode methodNode = new MethodNode(ACC_PUBLIC, "mwe$shouldRenderHitbox", "()Z", null, null);
            classNode.methods.add(methodNode);
            methodNode.instructions.add(getNewConfigFieldInsnNode(map.get(classNode.name)));
            methodNode.instructions.add(new InsnNode(IRETURN));
            methodNode.visitMaxs(1, 1);
        } else if (ENTITYARROW.name.equals(classNode.name)) {
            final MethodNode methodNode = new MethodNode(ACC_PUBLIC, "mwe$shouldRenderHitbox", "()Z", null, null);
            classNode.methods.add(methodNode);
            methodNode.instructions.add(new VarInsnNode(ALOAD, 0));
            methodNode.instructions.add(getNewFieldInsnNode(GETFIELD, FieldMapping.ENTITYARROW$ISINGROUND));
            final LabelNode l0 = new LabelNode();
            methodNode.instructions.add(new JumpInsnNode(IFEQ, l0));
            methodNode.instructions.add(getNewConfigFieldInsnNode("drawHitboxForGroundedArrows"));
            methodNode.instructions.add(new InsnNode(IRETURN));
            methodNode.instructions.add(l0);
            methodNode.instructions.add(new VarInsnNode(ALOAD, 0));
            methodNode.instructions.add(getNewFieldInsnNode(GETFIELD, FieldMapping.ENTITYARROW$PINNEDTOPLAYER));
            final LabelNode l1 = new LabelNode();
            methodNode.instructions.add(new JumpInsnNode(IFEQ, l1));
            methodNode.instructions.add(getNewConfigFieldInsnNode("drawHitboxForPinnedArrows"));
            methodNode.instructions.add(new InsnNode(IRETURN));
            methodNode.instructions.add(l1);
            methodNode.instructions.add(getNewConfigFieldInsnNode("drawHitboxForFlyingArrows"));
            methodNode.instructions.add(new InsnNode(IRETURN));
            methodNode.visitMaxs(1, 1);
        }
    }

}
