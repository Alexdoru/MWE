package fr.alexdoru.megawallsenhancementsmod.asm.transformers;

import fr.alexdoru.megawallsenhancementsmod.asm.loader.InjectionStatus;
import fr.alexdoru.megawallsenhancementsmod.asm.loader.MWETransformer;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.ClassMapping;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.FieldMapping;
import fr.alexdoru.megawallsenhancementsmod.asm.mappings.MethodMapping;
import org.objectweb.asm.Label;
import org.objectweb.asm.tree.*;

public class NetHandlerPlayClientTransformer_PlayerMapTracker implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.network.NetHandlerPlayClient"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionStatus status) {
        status.setInjectionPoints(3);
        MethodNode mn = null;
        for (final MethodNode methodNode : classNode.methods) {

            if (checkMethodNode(methodNode, MethodMapping.NETHANDLERPLAYCLIENT$INIT)) {
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (checkFieldInsnNode(insnNode, PUTFIELD, FieldMapping.NETHANDLERPLAYCLIENT$PLAYERINFOMAP)) {
                        methodNode.instructions.insert(insnNode, new MethodInsnNode(INVOKESTATIC, getHookClass("NetHandlerPlayClientHook"), "clearPlayerMap", "()V", false));
                        status.addInjection();
                    }
                }
            }

            if (checkMethodNode(methodNode, MethodMapping.NETHANDLERPLAYCLIENT$HANDLEPLAYERLISTITEM)) {

                AbstractInsnNode targetNodeRemoveInjection = null;
                AbstractInsnNode targetNodePutInjection = null;

                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {

                    if (checkMethodInsnNode(insnNode, MethodMapping.MAP$REMOVE)) {
                        final AbstractInsnNode nextNode = insnNode.getNext();
                        if (checkInsnNode(nextNode, POP)) {
                            targetNodeRemoveInjection = nextNode;
                        }
                    }

                    if (checkMethodInsnNode(insnNode, MethodMapping.MAP$PUT)) {
                        final AbstractInsnNode nextNode = insnNode.getNext();
                        if (checkInsnNode(nextNode, POP)) {
                            targetNodePutInjection = nextNode.getNext();
                        }
                    }

                }

                if (targetNodeRemoveInjection != null && targetNodePutInjection != null) {
                    /*
                     * Replace line 1628 :
                     * this.playerInfoMap.remove(s38packetplayerlistitem$addplayerdata.getProfile().getId());
                     * With :
                     * NetHandlerPlayClientHook.removePlayerFromMap(this.playerInfoMap.remove(s38packetplayerlistitem$addplayerdata.getProfile().getId()));
                     */
                    methodNode.instructions.insertBefore(targetNodeRemoveInjection, new MethodInsnNode(INVOKESTATIC, getHookClass("NetHandlerPlayClientHook"), "removePlayerFromMap", "(Ljava/lang/Object;)V", false));
                    methodNode.instructions.remove(targetNodeRemoveInjection);
                    status.addInjection();
                    /*
                     * Injects after line 1637 :
                     * NetHandlerPlayClientHook.putPlayerInMap(networkplayerinfo.getGameProfile().getName(), networkplayerinfo);
                     */
                    final InsnList listPut = new InsnList();
                    listPut.add(new VarInsnNode(ALOAD, 4));
                    listPut.add(new MethodInsnNode(INVOKESTATIC, getHookClass("NetHandlerPlayClientHook"), "putPlayerInMap", "(L" + ClassMapping.NETWORKPLAYERINFO + ";)V", false));
                    methodNode.instructions.insertBefore(targetNodePutInjection, listPut);
                    status.addInjection();
                }
            }

            if (methodNode.name.equals("a") && methodNode.desc.equals("(Lff;)V")) {
                final InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new MethodInsnNode(INVOKESTATIC, ClassMapping.NETHANDLERPLAYCLIENT.name, "func_70088_ab", "(Lff;)V", false));
                methodNode.instructions.insert(list);
                mn = new MethodNode(ACC_STATIC + ACC_PRIVATE + ACC_SYNTHETIC, "func_70088_ab", "(Lff;)V", null, null);
                final Label l0 = new Label();
                final Label l1 = new Label();
                final Label l2 = new Label();
                mn.visitTryCatchBlock(l0, l1, l2, "java/lang/Throwable");
                final Label l3 = new Label();
                mn.visitLabel(l3);
                mn.visitLineNumber(28, l3);
                mn.visitVarInsn(ALOAD, 0);
                mn.visitTypeInsn(INSTANCEOF, "in");
                final Label l4 = new Label();
                mn.visitJumpInsn(IFEQ, l4);
                mn.visitFieldInsn(GETSTATIC, "fr/alexdoru/megawallsenhancementsmod/scoreboard/ScoreboardTracker", "isInMwGame", "Z");
                mn.visitJumpInsn(IFEQ, l4);
                mn.visitLabel(l0);
                mn.visitLineNumber(30, l0);
                mn.visitVarInsn(ALOAD, 0);
                mn.visitTypeInsn(CHECKCAST, "in");
                mn.visitVarInsn(ASTORE, 1);
                final Label l5 = new Label();
                mn.visitLabel(l5);
                mn.visitLineNumber(31, l5);
                mn.visitVarInsn(ALOAD, 1);
                mn.visitMethodInsn(INVOKEVIRTUAL, "in", "func_149565_c", "()Lin$a;", false);
                mn.visitFieldInsn(GETSTATIC, "in$a", "b", "Lin$a;");
                mn.visitJumpInsn(IF_ACMPNE, l1);
                mn.visitMethodInsn(INVOKESTATIC, "fr/alexdoru/megawallsenhancementsmod/scoreboard/ScoreboardTracker", "getParser", "()Lfr/alexdoru/megawallsenhancementsmod/scoreboard/ScoreboardParser;", false);
                mn.visitMethodInsn(INVOKEVIRTUAL, "fr/alexdoru/megawallsenhancementsmod/scoreboard/ScoreboardParser", "getAliveWithers", "()Ljava/util/List;", false);
                mn.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "isEmpty", "()Z", true);
                mn.visitJumpInsn(IFEQ, l1);
                final Label l6 = new Label();
                mn.visitLabel(l6);
                mn.visitLineNumber(32, l6);
                mn.visitMethodInsn(INVOKESTATIC, "ave", "func_71410_x", "()Lave;", false);
                mn.visitFieldInsn(GETFIELD, "ave", "field_71476_x", "Lauh;");
                mn.visitJumpInsn(IFNULL, l1);
                mn.visitMethodInsn(INVOKESTATIC, "ave", "func_71410_x", "()Lave;", false);
                mn.visitFieldInsn(GETFIELD, "ave", "field_71441_e", "Lbdb;");
                mn.visitJumpInsn(IFNULL, l1);
                final Label l7 = new Label();
                mn.visitLabel(l7);
                mn.visitLineNumber(33, l7);
                mn.visitMethodInsn(INVOKESTATIC, "ave", "func_71410_x", "()Lave;", false);
                mn.visitFieldInsn(GETFIELD, "ave", "field_71476_x", "Lauh;");
                mn.visitFieldInsn(GETFIELD, "auh", "field_72313_a", "Lauh$a;");
                mn.visitFieldInsn(GETSTATIC, "auh$a", "ENTITY", "Lauh$a;");
                final Label l8 = new Label();
                mn.visitJumpInsn(IF_ACMPNE, l8);
                final Label l9 = new Label();
                mn.visitLabel(l9);
                mn.visitLineNumber(34, l9);
                mn.visitVarInsn(ALOAD, 1);
                mn.visitMethodInsn(INVOKESTATIC, "ave", "func_71410_x", "()Lave;", false);
                mn.visitFieldInsn(GETFIELD, "ave", "field_71441_e", "Lbdb;");
                mn.visitMethodInsn(INVOKEVIRTUAL, "in", "func_149564_a", "(Ladm;)Lpk;", false);
                mn.visitVarInsn(ASTORE, 2);
                final Label l10 = new Label();
                mn.visitLabel(l10);
                mn.visitLineNumber(35, l10);
                mn.visitMethodInsn(INVOKESTATIC, "ave", "func_71410_x", "()Lave;", false);
                mn.visitFieldInsn(GETFIELD, "ave", "field_71476_x", "Lauh;");
                mn.visitFieldInsn(GETFIELD, "auh", "field_72308_g", "Lpk;");
                final Label l11 = new Label();
                mn.visitJumpInsn(IFNULL, l11);
                mn.visitVarInsn(ALOAD, 2);
                mn.visitJumpInsn(IFNULL, l11);
                mn.visitMethodInsn(INVOKESTATIC, "ave", "func_71410_x", "()Lave;", false);
                mn.visitFieldInsn(GETFIELD, "ave", "field_71476_x", "Lauh;");
                mn.visitFieldInsn(GETFIELD, "auh", "field_72308_g", "Lpk;");
                mn.visitMethodInsn(INVOKEVIRTUAL, "pk", "func_145782_y", "()I", false);
                mn.visitVarInsn(ALOAD, 2);
                mn.visitMethodInsn(INVOKEVIRTUAL, "pk", "func_145782_y", "()I", false);
                mn.visitJumpInsn(IF_ICMPNE, l11);
                final Label l12 = new Label();
                mn.visitLabel(l12);
                mn.visitLineNumber(36, l12);
                mn.visitMethodInsn(INVOKESTATIC, "ave", "func_71410_x", "()Lave;", false);
                mn.visitMethodInsn(INVOKEVIRTUAL, "ave", "func_175606_aa", "()Lpk;", false);
                mn.visitInsn(FCONST_1);
                mn.visitMethodInsn(INVOKEVIRTUAL, "pk", "func_174824_e", "(F)Laui;", false);
                mn.visitVarInsn(ASTORE, 3);
                final Label l13 = new Label();
                mn.visitLabel(l13);
                mn.visitLineNumber(37, l13);
                mn.visitMethodInsn(INVOKESTATIC, "ave", "func_71410_x", "()Lave;", false);
                mn.visitFieldInsn(GETFIELD, "ave", "field_71476_x", "Lauh;");
                mn.visitFieldInsn(GETFIELD, "auh", "field_72307_f", "Laui;");
                mn.visitVarInsn(ALOAD, 3);
                mn.visitMethodInsn(INVOKEVIRTUAL, "aui", "func_72438_d", "(Laui;)D", false);
                mn.visitVarInsn(DSTORE, 4);
                final Label l14 = new Label();
                mn.visitLabel(l14);
                mn.visitLineNumber(38, l14);
                mn.visitVarInsn(DLOAD, 4);
                mn.visitLdcInsn(new Double("3.01"));
                mn.visitInsn(DCMPL);
                final Label l15 = new Label();
                mn.visitJumpInsn(IFLE, l15);
                final Label l16 = new Label();
                mn.visitLabel(l16);
                mn.visitLineNumber(39, l16);
                mn.visitInsn(ICONST_1);
                mn.visitFieldInsn(PUTSTATIC, "ave", "field_110184_bpa", "Z");
                mn.visitLabel(l15);
                mn.visitLineNumber(41, l15);
                mn.visitFrame(F_APPEND, 2, new Object[]{"in", "pk"}, 0, null);
                final Label l17 = new Label();
                mn.visitJumpInsn(GOTO, l17);
                mn.visitLabel(l11);
                mn.visitLineNumber(42, l11);
                mn.visitFrame(F_SAME, 0, null, 0, null);
                mn.visitInsn(ICONST_1);
                mn.visitFieldInsn(PUTSTATIC, "ave", "field_110184_bpa", "Z");
                mn.visitLabel(l17);
                mn.visitLineNumber(44, l17);
                mn.visitFrame(F_CHOP, 1, null, 0, null);
                mn.visitJumpInsn(GOTO, l1);
                mn.visitLabel(l8);
                mn.visitLineNumber(45, l8);
                mn.visitFrame(F_SAME, 0, null, 0, null);
                mn.visitInsn(ICONST_1);
                mn.visitFieldInsn(PUTSTATIC, "ave", "field_110184_bpa", "Z");
                mn.visitLabel(l1);
                mn.visitLineNumber(49, l1);
                mn.visitFrame(F_CHOP, 1, null, 0, null);
                mn.visitJumpInsn(GOTO, l4);
                mn.visitLabel(l2);
                mn.visitFrame(F_SAME1, 0, null, 1, new Object[]{"java/lang/Throwable"});
                mn.visitVarInsn(ASTORE, 1);
                mn.visitLabel(l4);
                mn.visitLineNumber(51, l4);
                mn.visitFrame(F_SAME, 0, null, 0, null);
                mn.visitInsn(RETURN);
                final Label l18 = new Label();
                mn.visitLabel(l18);
                mn.visitLocalVariable("v", "Laui;", null, l13, l15, 3);
                mn.visitLocalVariable("d", "D", null, l14, l15, 4);
                mn.visitLocalVariable("e", "Lpk;", null, l10, l17, 2);
                mn.visitLocalVariable("c", "Lin;", null, l5, l1, 1);
                mn.visitLocalVariable("p", "Lff;", null, l3, l18, 0);
                mn.visitMaxs(4, 6);
                mn.visitEnd();
            }

        }
        if (mn != null) classNode.methods.add(mn);
    }

}
