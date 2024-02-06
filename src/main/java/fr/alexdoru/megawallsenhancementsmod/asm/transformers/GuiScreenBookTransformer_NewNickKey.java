package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.loader.InjectionStatus;
import fr.alexdoru.megawallsenhancementsmod.asm.loader.MWETransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.ClassMapping;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.FieldMapping;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.*;

public class GuiScreenBookTransformer_NewNickKey implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.gui.GuiScreenBook"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(3);
        for (final MethodNode methodNode : classNode.methods) {

            if (checkMethodNode(methodNode, MethodMapping.GUISCREENBOOK$INIT)) {
                /*
                 * Injects at head of constructor :
                 * GuiScreenBookHook.onBookInit(book);
                 */
                final InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 2));
                list.add(new MethodInsnNode(INVOKESTATIC, getHookClass("GuiScreenBookHook"), "onBookInit", "(L" + ClassMapping.ITEMSTACK + ";)V", false));
                methodNode.instructions.insert(list);
                status.addInjection();
            }

            if (checkMethodNode(methodNode, MethodMapping.GUISCREENBOOK$KEYTYPED)) {
                /*
                 * Injects at head :
                 * GuiScreenBookHook.onKeyTyped(keyCode);
                 */
                final InsnList list = new InsnList();
                list.add(new VarInsnNode(ILOAD, 2));
                list.add(new MethodInsnNode(INVOKESTATIC, getHookClass("GuiScreenBookHook"), "onKeyTyped", "(I)V", false));
                methodNode.instructions.insert(list);
                status.addInjection();
            }

            if (checkMethodNode(methodNode, MethodMapping.GUISCREENBOOK$DRAWSCREEN)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkMethodInsnNode(insnNode, MethodMapping.GUISCREENBOOK$DRAWTEXTUREDMODALRECT)) {
                        /*
                         * Injects at line 411 :
                         * GuiScreenBookHook.renderInstructions(this.width, this.bookImageHeight);
                         */
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
