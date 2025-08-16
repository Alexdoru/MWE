package fr.alexdoru.mwe.asm.transformers.mc.entity;

import fr.alexdoru.mwe.asm.loader.InjectionCallback;
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
                "net.minecraft.entity.boss.EntityWither",
                "net.minecraft.entity.item.EntityItem",
                "net.minecraft.entity.item.EntityItemFrame",
                "net.minecraft.entity.passive.EntityAnimal",
                "net.minecraft.entity.projectile.EntityArrow"
        };
    }

    @Override
    public void transform(ClassNode classNode, InjectionCallback status) {
        status.setInjectionPoints(0);
        addInterface(classNode, "IHitboxRender");
        final Map<String, String> map = new HashMap<>();
        map.put(ABSTRACTCLIENTPLAYER.name, "drawHitboxForPlayers");
        map.put(ENTITYWITHER.name, "drawHitboxForWithers");
        map.put(ENTITYITEM.name, "drawHitboxForDroppedItems");
        map.put(ENTITYITEMFRAME.name, "drawHitboxItemFrame");
        map.put(ENTITYANIMAL.name, "drawHitboxForPassiveMobs");
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
