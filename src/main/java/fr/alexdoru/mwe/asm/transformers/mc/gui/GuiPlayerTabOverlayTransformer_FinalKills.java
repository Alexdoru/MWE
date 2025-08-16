package fr.alexdoru.mwe.asm.transformers.mc.gui;

import fr.alexdoru.mwe.asm.loader.InjectionCallback;
import fr.alexdoru.mwe.asm.loader.MWETransformer;
import fr.alexdoru.mwe.asm.mappings.FieldMapping;
import fr.alexdoru.mwe.asm.mappings.MethodMapping;
import org.objectweb.asm.tree.*;

public class GuiPlayerTabOverlayTransformer_FinalKills implements MWETransformer {

    @Override
    public String[] getTargetClassName() {
        return new String[]{"net.minecraft.client.gui.GuiPlayerTabOverlay"};
    }

    @Override
    public void transform(ClassNode classNode, InjectionCallback status) {
        status.setInjectionPoints(5);
        for (final MethodNode methodNode : classNode.methods) {
            if (checkMethodNode(methodNode, MethodMapping.GUIPLAYERTABOVERLAY$RENDERPLAYERLIST)) {
                methodNode.instructions.insert(new MethodInsnNode(INVOKESTATIC, getHookClass("GuiPlayerTabOverlayHook_FinalKills"), "resetFinalsScoreWidth", "()V", false));
                status.addInjection();
                boolean injectedMax = false;
                boolean lookFork5k2 = false;
                int ordinal_getGameType = 0;
                int index_k2 = -1;
                int index_j2 = -1;
                int index_i = -1;
                int index_networkplayerinfo = -1;
                AbstractInsnNode latestAload0 = null;
                for (final AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
                    if (!injectedMax && checkMethodInsnNode(insnNode, MethodMapping.MATH$MAX)) {
                        final AbstractInsnNode nextNode = insnNode.getNext();
                        if (checkVarInsnNode(nextNode, ISTORE, 6)) {
                            final InsnList list = new InsnList();
                            list.add(new VarInsnNode(ALOAD, 9));
                            list.add(getNewFieldInsnNode(GETFIELD, FieldMapping.NETWORKPLAYERINFO$MWE$FINALKILLS));
                            list.add(new MethodInsnNode(INVOKESTATIC, getHookClass("GuiPlayerTabOverlayHook_FinalKills"), "computeFKScoreWidth", "(I)V", false));
                            methodNode.instructions.insert(nextNode, list);
                            status.addInjection();
                            injectedMax = true;
                        }
                    } else if (checkVarInsnNode(insnNode, ILOAD, 7) && checkVarInsnNode(insnNode.getNext(), ISTORE, 12) ||
                            checkInsnNode(insnNode, ICONST_0) && checkVarInsnNode(insnNode.getNext(), ISTORE, 12)) {
                        final InsnList list = new InsnList();
                        list.add(new MethodInsnNode(INVOKESTATIC, getHookClass("GuiPlayerTabOverlayHook_FinalKills"), "getRenderScoreWidth", "()I", false));
                        list.add(new InsnNode(IADD));
                        methodNode.instructions.insert(insnNode, list);
                        status.addInjection();
                    } else if (lookFork5k2 && checkVarInsnNode(insnNode, ILOAD)) {
                        final AbstractInsnNode secondNode = insnNode.getNext();
                        if (checkVarInsnNode(secondNode, ILOAD)) {
                            final AbstractInsnNode thirdNode = secondNode.getNext();
                            if (checkInsnNode(thirdNode, IADD)) {
                                final AbstractInsnNode fourthNode = thirdNode.getNext();
                                if (checkInsnNode(fourthNode, ICONST_1)) {
                                    final AbstractInsnNode fifthNode = fourthNode.getNext();
                                    if (checkInsnNode(fifthNode, IADD)) {
                                        final AbstractInsnNode sixthNode = fifthNode.getNext();
                                        if (checkVarInsnNode(sixthNode, ISTORE)) {
                                            index_j2 = ((VarInsnNode) insnNode).var;
                                            index_i = ((VarInsnNode) secondNode).var;
                                        }
                                    }
                                }
                            }
                        }
                    } else if (lookFork5k2 && checkVarInsnNode(insnNode, ALOAD, 3)) {
                        final AbstractInsnNode nextNode = insnNode.getNext();
                        if (checkVarInsnNode(nextNode, ILOAD)) {
                            index_k2 = ((VarInsnNode) nextNode).var;
                        }
                    } else if (ordinal_getGameType < 2 && checkMethodInsnNode(insnNode, MethodMapping.NETWORKPLAYERINFO$GETGAMETYPE)) {
                        ordinal_getGameType++;
                        lookFork5k2 = ordinal_getGameType == 2;
                    } else if (checkMethodInsnNode(insnNode, MethodMapping.GUIPLAYERTABOVERLAY$DRAWSCOREBOARDVALUES)) {
                        lookFork5k2 = false;
                        final AbstractInsnNode previousNode = insnNode.getPrevious();
                        if (checkVarInsnNode(previousNode, ALOAD)) {
                            index_networkplayerinfo = ((VarInsnNode) previousNode).var;
                        }
                    } else if (latestAload0 != null && index_j2 != -1 && index_i != -1 && index_k2 != -1 && index_networkplayerinfo != -1 && checkMethodInsnNode(insnNode, MethodMapping.GUIPLAYERTABOVERLAY$DRAWPING)) {
                        final InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, index_networkplayerinfo));
                        list.add(getNewFieldInsnNode(GETFIELD, FieldMapping.NETWORKPLAYERINFO$MWE$FINALKILLS));
                        list.add(new VarInsnNode(ILOAD, index_j2));
                        list.add(new VarInsnNode(ILOAD, index_i));
                        list.add(new VarInsnNode(ILOAD, index_k2));
                        list.add(new MethodInsnNode(INVOKESTATIC, getHookClass("GuiPlayerTabOverlayHook_FinalKills"), "renderFinals", "(IIII)V", false));
                        methodNode.instructions.insertBefore(latestAload0, list);
                        status.addInjection();
                    } else if (checkVarInsnNode(insnNode, ALOAD, 0)) {
                        latestAload0 = insnNode;
                    }
                }
            }
        }
    }

}
