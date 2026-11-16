package fr.alexdoru.configlib.lib.gui;

import fr.alexdoru.configlib.api.IRendererV2;
import fr.alexdoru.configlib.api.RendererAlignment;
import fr.alexdoru.configlib.api.RendererPositionV2;
import fr.alexdoru.configlib.lib.RendererManager;
import fr.alexdoru.configlib.lib.gui.elements.ClickGuiButton;
import fr.alexdoru.configlib.lib.utils.EnumUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Locale;

public class RendererEditGuiScreenV2 extends AbstractRendererEditGuiScreen<RendererPositionV2> {

    private static final double SCALE_STEP = 0.1d;
    private static final int ANCHORED_SIDE_COLOR = 0xFFFF0000;
    private static final int SIDE_COLOR = 0xFF555555;
    private static final int ALIGNMENT_DOT_COLOR = 0xFF8A8A8F;

    private final IRendererV2 rendererV2;
    private final double originalScale;
    private final RendererAlignment originalAlignment;
    private final ClickGuiButton alignmentButton;
    private final ClickGuiButton increaseButton;
    private final ClickGuiButton decreaseButton;

    public RendererEditGuiScreenV2(RendererManager rendererManager, RendererPositionV2 rendererPosition, ConfigGuiScreen parent, Field field) {
        super(rendererManager, rendererPosition, parent, field);
        if (!(renderer instanceof IRendererV2)) {
            throw new IllegalStateException("The renderer associated to " + field.getName() + " uses a RendererPositionV2 but doesn't implement IRendererV2");
        }
        this.rendererV2 = (IRendererV2) renderer;
        this.originalScale = rendererPosition.getScale();
        this.originalAlignment = rendererPosition.getAlignment();
        this.alignmentButton = new ClickGuiButton(BUTTON_SIZE, BUTTON_SIZE);
        this.increaseButton = new ClickGuiButton(BUTTON_SIZE, BUTTON_SIZE, "+");
        this.increaseButton.setHoveringText("Increase Size", EnumChatFormatting.DARK_GRAY + "You can also use the mouse wheel");
        this.decreaseButton = new ClickGuiButton(BUTTON_SIZE, BUTTON_SIZE, "-");
        this.decreaseButton.setHoveringText("Decrease Size", EnumChatFormatting.DARK_GRAY + "You can also use the mouse wheel");
        this.buttons.add(alignmentButton);
        this.buttons.add(increaseButton);
        this.buttons.add(decreaseButton);
    }

    @Override
    public void initGui() {
        if (this.resolution != null) {
            // the screen got resized, keep the position that was being edited
            this.moveAnchorToBox();
        }
        super.initGui();
    }

    @Override
    protected void drawEditedHud(int mouseX, int mouseY) {
        if (this.dragging) {
            this.rendererPosition.setRelativePosition(
                    this.rendererPosition.getRelativeX() + (double) (mouseX - this.prevX) / this.resolution.getScaledWidth(),
                    this.rendererPosition.getRelativeY() + (double) (mouseY - this.prevY) / this.resolution.getScaledHeight()
            );
        }
        GlStateManager.pushMatrix();
        this.rendererV2.renderDummy(this.resolution);
        GlStateManager.popMatrix();
        if (this.dragging) {
            this.moveAnchorToBox();
        }
        this.drawBoxOutline();
    }

    /**
     * Puts the anchor back on the last rendered box, so that it matches
     * what is on screen after the box got clamped inside the screen
     */
    private void moveAnchorToBox() {
        final RendererAlignment alignment = this.rendererPosition.getAlignment();
        final double anchorX = this.rendererPosition.getBoxX() + alignment.xFactor * this.rendererPosition.getBoxWidth();
        final double anchorY = this.rendererPosition.getBoxY() + alignment.yFactor * this.rendererPosition.getBoxHeight();
        this.rendererPosition.setRelativePosition(anchorX / this.resolution.getScaledWidth(), anchorY / this.resolution.getScaledHeight());
    }

    private void drawBoxOutline() {
        final int left = MathHelper.floor_float(rendererPosition.getBoxX());
        final int top = MathHelper.floor_float(rendererPosition.getBoxY());
        final int right = MathHelper.ceiling_float_int(rendererPosition.getBoxX() + rendererPosition.getBoxWidth());
        final int bottom = MathHelper.ceiling_float_int(rendererPosition.getBoxY() + rendererPosition.getBoxHeight());
        final RendererAlignment alignment = rendererPosition.getAlignment();
        final boolean leftAnchored = alignment.xFactor == 0f;
        final boolean rightAnchored = alignment.xFactor == 1f;
        final boolean topAnchored = alignment.yFactor == 0f;
        final boolean bottomAnchored = alignment.yFactor == 1f;
        // red sides are drawn last so they cover the corner pixels shared with the gray sides
        for (final boolean anchoredPass : new boolean[]{false, true}) {
            final int color = anchoredPass ? ANCHORED_SIDE_COLOR : SIDE_COLOR;
            if (topAnchored == anchoredPass) {
                GuiUtil.drawHorizontalLine(left, right - 1, top, color);
            }
            if (bottomAnchored == anchoredPass) {
                GuiUtil.drawHorizontalLine(left, right - 1, bottom - 1, color);
            }
            if (leftAnchored == anchoredPass) {
                GuiUtil.drawVerticalLine(left, top - 1, bottom - 1, color);
            }
            if (rightAnchored == anchoredPass) {
                GuiUtil.drawVerticalLine(right - 1, top - 1, bottom - 1, color);
            }
        }
    }

    @Override
    protected void drawButtonsExtras(int buttonX, int nextButtonY) {
        final RendererAlignment alignment = rendererPosition.getAlignment();
        alignmentButton.setHoveringText("Alignment: " + EnumUtil.formatEnumName(alignment), EnumChatFormatting.DARK_GRAY + "Left/right click to change");
        this.drawAlignmentGrid(alignment);
        final String scaleText = String.format(Locale.ROOT, "%.1fx", rendererPosition.getScale());
        fontRendererObj.drawStringWithShadow(scaleText, buttonX + BUTTON_SIZE - fontRendererObj.getStringWidth(scaleText), nextButtonY, 0xFFFFFFFF);
    }

    private void drawAlignmentGrid(RendererAlignment alignment) {
        final int DOT_SIZE = 2;
        final int DOT_SPACING = 4;
        final int INSET = 3;
        final int selectedColumn = (int) (alignment.xFactor * 2);
        final int selectedRow = (int) (alignment.yFactor * 2);
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                final int x = alignmentButton.xPosition + INSET + column * DOT_SPACING;
                final int y = alignmentButton.yPosition + INSET + row * DOT_SPACING;
                final int color = column == selectedColumn && row == selectedRow ? 0xFFFFFFFF : ALIGNMENT_DOT_COLOR;
                drawRect(x, y, x + DOT_SIZE, y + DOT_SIZE, color);
            }
        }
    }

    @Override
    protected boolean handleExtraButtonClick(MouseButton button, int mouseX, int mouseY) {
        if ((button.isLeft() || button.isRight()) && alignmentButton.mousePressed(mouseX, mouseY)) {
            rendererPosition.setAlignment(rendererPosition.getAlignment().cycle(button.isLeft() ? 1 : -1));
            this.moveAnchorToBox();
            return true;
        }
        if (button.isLeft()) {
            if (increaseButton.mousePressed(mouseX, mouseY)) {
                this.changeScale(1);
                return true;
            }
            if (decreaseButton.mousePressed(mouseX, mouseY)) {
                this.changeScale(-1);
                return true;
            }
        }
        return false;
    }

    @Override
    protected void undoChanges() {
        rendererPosition.setScale(originalScale);
        rendererPosition.setAlignment(originalAlignment);
        super.undoChanges();
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
        if (MouseButton.from(mouseButton).isLeft()) {
            this.moveAnchorToBox();
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        final int wheel = Mouse.getEventDWheel();
        if (wheel != 0) {
            this.changeScale(Integer.signum(wheel));
        }
    }

    private void changeScale(int direction) {
        final double newScale = Math.round((rendererPosition.getScale() + direction * SCALE_STEP) * 10d) / 10d;
        rendererPosition.setScale(newScale);
    }

    @Override
    public void onGuiClosed() {
        this.moveAnchorToBox();
    }

}
