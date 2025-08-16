package fr.alexdoru.mwe.asm.transformers.mc.gui;

import fr.alexdoru.mwe.asm.mappings.ClassMapping;
import fr.alexdoru.mwe.asm.mappings.FieldMapping;
import fr.alexdoru.mwe.asm.mappings.MethodMapping;
import fr.alexdoru.mwe.asm.transformers.InjectionCallback;
import fr.alexdoru.mwe.asm.transformers.MWETransformer;
import org.objectweb.asm.tree.*;

public class GuiScreenBookTransformer_NewNickKey implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.gui.GuiScreenBook"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionCallback status) {
        status.setInjectionPoints(3);
        for (final MethodNode methodNode : classNode.methods) {

            if (isConstructorMethod(methodNode)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkInsnNode(insnNode, RETURN)) {
                        final InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 2));
                        list.add(new MethodInsnNode(
                                INVOKESTATIC,
                                getHookClass("GuiScreenBookHook"),
                                "onBookInit",
                                "(L" + ClassMapping.ITEMSTACK + ";)V",
                                false
                        ));
                        methodNode.instructions.insertBefore(insnNode, list);
                        status.addInjection();
                    }
                }
            }

            if (checkMethodNode(methodNode, MethodMapping.GUISCREENBOOK$KEYTYPED)) {
                // Injects at head :
                // GuiScreenBookHook.onKeyTyped(keyCode);
                final InsnList list = new InsnList();
                list.add(new VarInsnNode(ILOAD, 2));
                list.add(new MethodInsnNode(INVOKESTATIC, getHookClass("GuiScreenBookHook"), "onKeyTyped", "(I)V", false));
                methodNode.instructions.insert(list);
                status.addInjection();
            }

            if (checkMethodNode(methodNode, MethodMapping.GUISCREENBOOK$DRAWSCREEN)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkMethodInsnNode(insnNode, MethodMapping.GUISCREENBOOK$DRAWTEXTUREDMODALRECT)) {
                        // Injects at line 411 :
                        // GuiScreenBookHook.renderInstructions(this.width, this.bookImageHeight);
                        final InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(getNewFieldInsnNode(GETFIELD, FieldMapping.GUISCREENBOOK$WIDTH));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(getNewFieldInsnNode(GETFIELD, FieldMapping.GUISCREENBOOK$BOOKIMAGEHEIGHT));
                        list.add(new MethodInsnNode(INVOKESTATIC, getHookClass("GuiScreenBookHook"), "renderInstructions", "(II)V", false));
                        methodNode.instructions.insert(insnNode, list);
                        status.addInjection();
                        break;
                    }
                }
            }

        }
    }

}
