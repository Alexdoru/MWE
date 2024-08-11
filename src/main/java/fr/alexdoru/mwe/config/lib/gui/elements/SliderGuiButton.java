package fr.alexdoru.mwe.config.lib.gui.elements;

import fr.alexdoru.mwe.config.lib.ConfigProperty;
import fr.alexdoru.mwe.gui.GuiUtil;
import fr.alexdoru.mwe.utils.SoundUtil;
import net.minecraft.util.MathHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class SliderGuiButton extends ConfigGuiButton {

    private static final int SLIDER_WIDTH = 80;
    private static final int SLIDER_BUTTON_SIZE = 12;
    private static final int PLUS_BUTTON_SIZE = 10;
    private int sliderBarX;
    private int sliderButtonX, sliderButtonY;
    private int minusButtonX, minusButtonY;
    private int plusButtonX, plusButtonY;
    private int sliderIncrement;
    private final boolean isIntValue;
    private boolean isPourcentage;
    private final int minValue, maxValue;
    private int sliderValueI;
    private double sliderValueD;
    private double incrementStepD;
    private boolean dragging = false;

    public SliderGuiButton(Field field, Method event, ConfigProperty annotation) throws IllegalAccessException {
        super(field, event, annotation);
        minValue = annotation.sliderMin();
        maxValue = annotation.sliderMax();
        if (field.getType() == int.class) {
            isIntValue = true;
            sliderValueI = (int) field.get(null);
            sliderIncrement = MathHelper.clamp_int((SLIDER_WIDTH - 1) * (sliderValueI - minValue) / (maxValue - minValue), 0, SLIDER_WIDTH - 1);
        } else if (field.getType() == double.class) {
            isIntValue = false;
            sliderValueD = (double) field.get(null);
            if (minValue == 0 && maxValue == 1) {
                isPourcentage = true;
                sliderValueI = (int) ((double) field.get(null) * 100d);
                sliderIncrement = MathHelper.clamp_int((SLIDER_WIDTH - 1) * (sliderValueI) / 100, 0, SLIDER_WIDTH - 1);
            } else {
                incrementStepD = Math.min(0.25D, (maxValue - minValue) / (double) 100);
                sliderIncrement = MathHelper.clamp_int((int) (((double) SLIDER_WIDTH - 1d) * (sliderValueD - (double) minValue) / (double) (maxValue - minValue)), 0, SLIDER_WIDTH - 1);
            }
        } else {
            throw new IllegalArgumentException("Field of type " + field.getType() + " not supported by SliderGuiButton.");
        }
    }

    @Override
    public void setBoxWidth(int boxWidth) {
        super.setBoxWidth(boxWidth - 60);
        this.boxWidth = boxWidth;
    }

    @Override
    public void draw(int drawX, int drawY, int mouseX, int mouseY) {
        super.draw(drawX, drawY, mouseX, mouseY);
        if (dragging) {
            updateSliderFromPosition(mouseX - sliderBarX);
        }
        final int SLIDER_HEIGHT = 6;
        sliderBarX = drawX + boxWidth - SLIDER_WIDTH - 20;
        final int sliderBarY = drawY + 8 + mc.fontRendererObj.FONT_HEIGHT;
        sliderButtonX = sliderBarX + sliderIncrement - SLIDER_BUTTON_SIZE / 2;
        sliderButtonY = sliderBarY + (SLIDER_HEIGHT - SLIDER_BUTTON_SIZE) / 2;
        minusButtonX = sliderBarX - SLIDER_BUTTON_SIZE / 2 - PLUS_BUTTON_SIZE - 1;
        minusButtonY = sliderBarY + (SLIDER_HEIGHT - PLUS_BUTTON_SIZE) / 2;
        plusButtonX = sliderBarX + SLIDER_WIDTH + SLIDER_BUTTON_SIZE / 2;
        plusButtonY = sliderBarY + (SLIDER_HEIGHT - PLUS_BUTTON_SIZE) / 2;
        GuiUtil.drawBoxWithOutline(sliderBarX, sliderBarY, sliderBarX + SLIDER_WIDTH, sliderBarY + SLIDER_HEIGHT, 0xFF595959, 0xFFBFBFBF);
        GuiUtil.drawBoxWithOutline(sliderButtonX, sliderButtonY, sliderButtonX + SLIDER_BUTTON_SIZE, sliderButtonY + SLIDER_BUTTON_SIZE, 0xFF5D83FF, 0xFFE6E6E6);
        GuiUtil.drawBoxWithOutline(minusButtonX, minusButtonY, minusButtonX + PLUS_BUTTON_SIZE, minusButtonY + PLUS_BUTTON_SIZE, 0xFF707070, 0xFF606060);
        mc.fontRendererObj.drawStringWithShadow("-", minusButtonX + 2, minusButtonY + 1, 0xFFFFFFFF);
        GuiUtil.drawBoxWithOutline(plusButtonX, plusButtonY, plusButtonX + PLUS_BUTTON_SIZE, plusButtonY + PLUS_BUTTON_SIZE, 0xFF707070, 0xFF606060);
        mc.fontRendererObj.drawStringWithShadow("+", plusButtonX + 2, plusButtonY + 1, 0xFFFFFFFF);
        final String valueText;
        if (isIntValue) {
            valueText = String.valueOf(sliderValueI);
        } else {
            if (isPourcentage) {
                valueText = MathHelper.floor_double(sliderValueI) + "%";
            } else {
                valueText = String.format("%.2f", sliderValueD);
            }
        }
        final int valueX = sliderButtonX + (SLIDER_BUTTON_SIZE - mc.fontRendererObj.getStringWidth(valueText)) / 2;
        final int valueY = sliderBarY - mc.fontRendererObj.FONT_HEIGHT - 4;
        mc.fontRendererObj.drawStringWithShadow(valueText, valueX, valueY, 0xFFFFFFFF);
    }

    @Override
    public int getHeight() {
        return Math.max(super.getHeight(), 8 + mc.fontRendererObj.FONT_HEIGHT + SLIDER_BUTTON_SIZE + 8);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) throws IllegalAccessException {
        if (mouseButton == 0) {
            if (isMouseOnButton(mouseX, mouseY, sliderButtonX, sliderButtonY, SLIDER_BUTTON_SIZE, SLIDER_BUTTON_SIZE)) {
                updateSliderFromPosition(mouseX - sliderBarX);
                dragging = true;
                SoundUtil.playButtonPress();
                return true;
            } else if (isMouseOnButton(mouseX, mouseY, minusButtonX, minusButtonY, PLUS_BUTTON_SIZE, PLUS_BUTTON_SIZE)) {
                updateSliderFromIncrement(-1);
                SoundUtil.playButtonPress();
                return true;
            } else if (isMouseOnButton(mouseX, mouseY, plusButtonX, plusButtonY, PLUS_BUTTON_SIZE, PLUS_BUTTON_SIZE)) {
                updateSliderFromIncrement(1);
                SoundUtil.playButtonPress();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && this.dragging) {
            this.dragging = false;
            return true;
        }
        return false;
    }

    private void updateSliderFromPosition(int sliderIncrementIn) {
        sliderIncrement = MathHelper.clamp_int(sliderIncrementIn, 0, SLIDER_WIDTH - 1);
        try {
            if (isIntValue) {
                final int prevValue = sliderValueI;
                sliderValueI = MathHelper.clamp_int(sliderIncrement * (maxValue - minValue) / (SLIDER_WIDTH - 1) + minValue, minValue, maxValue);
                field.setInt(null, sliderValueI);
                if (prevValue != sliderValueI) {
                    invokeConfigEvent();
                }
            } else if (isPourcentage) {
                final int prevValue = sliderValueI;
                sliderValueI = MathHelper.clamp_int(sliderIncrement * 100 / (SLIDER_WIDTH - 1), 0, 100);
                field.setDouble(null, MathHelper.clamp_double(sliderValueI / 100d, 0, 1));
                if (prevValue != sliderValueI) {
                    invokeConfigEvent();
                }
            } else {
                final double prevValue = sliderValueD;
                sliderValueD = MathHelper.clamp_double(sliderIncrement * (maxValue - minValue) / (double) (SLIDER_WIDTH - 1) + (double) minValue, minValue, maxValue);
                sliderValueD = ((int) ((sliderValueD - (double) minValue) / incrementStepD)) * incrementStepD + minValue;
                field.setDouble(null, sliderValueD);
                if (prevValue != sliderValueD) {
                    invokeConfigEvent();
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot set value to : " + field.getName());
        }
    }

    public void updateSliderFromIncrement(int valueIncrement) {
        try {
            if (isIntValue) {
                sliderValueI = MathHelper.clamp_int(sliderValueI + valueIncrement, minValue, maxValue);
                sliderIncrement = MathHelper.clamp_int((SLIDER_WIDTH - 1) * (sliderValueI - minValue) / (maxValue - minValue), 0, SLIDER_WIDTH - 1);
                field.setInt(null, sliderValueI);
            } else if (isPourcentage) {
                sliderValueI = MathHelper.clamp_int(sliderValueI + valueIncrement, 0, 100);
                sliderIncrement = MathHelper.clamp_int((SLIDER_WIDTH - 1) * sliderValueI / (100), 0, SLIDER_WIDTH - 1);
                field.setDouble(null, MathHelper.clamp_double(sliderValueI / 100d, 0, 1));
            } else {
                sliderValueD = MathHelper.clamp_double(MathHelper.floor_double((sliderValueD - (double) minValue) / incrementStepD + valueIncrement) * incrementStepD + minValue, minValue, maxValue);
                sliderIncrement = MathHelper.clamp_int((int) (((double) SLIDER_WIDTH - 1d) * (sliderValueD - (double) minValue) / (double) (maxValue - minValue)), 0, SLIDER_WIDTH - 1);
                field.setDouble(null, sliderValueD);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot set value to : " + field.getName());
        }
        invokeConfigEvent();
    }

}
