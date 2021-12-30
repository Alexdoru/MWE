//package fr.alexdoru.megawallsenhancementsmod.utils;
//
//import net.minecraft.util.EnumChatFormatting;
//
//import java.awt.*;
//
//public class ColorUtil {
//
//    private static final Color DARK_GREEN = new Color(43520);
//    private static final int r_dark_green = DARK_GREEN.getRed();
//    private static final int g_dark_green = DARK_GREEN.getGreen();
//    private static final int b_dark_green = DARK_GREEN.getBlue();
//    private static final float[] hsb_dark_green = Color.RGBtoHSB(r_dark_green, g_dark_green, b_dark_green, null);
//    private static final Color GREEN = new Color(5635925);
//    private static final int r_green = GREEN.getRed();
//    private static final int g_green = GREEN.getGreen();
//    private static final int b_green = GREEN.getBlue();
//    private static final float[] hsb_green = Color.RGBtoHSB(r_green, g_green, b_green, null);
//    private static final Color YELLOW = new Color(16777045);
//    private static final int r_yellow = YELLOW.getRed();
//    private static final int g_yellow = YELLOW.getGreen();
//    private static final int b_yellow = YELLOW.getBlue();
//    private static final float[] hsb_yellow = Color.RGBtoHSB(r_yellow, g_yellow, b_yellow, null);
//    private static final Color DARK_RED = new Color(11141120);
//    private static final int r_dark_red = DARK_RED.getRed();
//    private static final int g_dark_red = DARK_RED.getGreen();
//    private static final int b_dark_red = DARK_RED.getBlue();
//    private static final float[] hsb_dark_red = Color.RGBtoHSB(r_dark_red, g_dark_red, b_dark_red, null);
//
//    /**
//     * Makes a color gradient using linear interpolation on RGB coefficients
//     */
//    public static Color getColorRGBGradient(float otherHP, float maxHP) {
//
//        if (otherHP >= maxHP) {
//
//            if (otherHP >= maxHP * 1.3f) {
//                return DARK_GREEN;
//            }
//
//            float ratio = (otherHP - maxHP) / (maxHP * 0.3f);
//            int r = (int) (r_green * (1f - ratio) + r_dark_green * ratio);
//            int g = (int) (g_green * (1f - ratio) + g_dark_green * ratio);
//            int b = (int) (b_green * (1f - ratio) + b_dark_green * ratio);
//            return new Color(r, g, b);
//
//        } else if (0 < otherHP) {
//
//            float ratio = otherHP / maxHP;
//            int r = (int) (r_green * ratio + r_dark_red * (1f - ratio));
//            int g = (int) (g_green * ratio + g_dark_red * (1f - ratio));
//            int b = (int) (b_green * ratio + b_dark_red * (1f - ratio));
//            return new Color(r, g, b);
//
//        }
//
//        return DARK_RED;
//
//    }
//
//    /**
//     * Makes a color gradient using linear interpolation on HSB coefficients
//     */
//    public static Color getColorHSBGradient(float otherHP, float maxHP) {
//
//        if (otherHP >= maxHP) {
//
//            if (otherHP >= maxHP * 1.3f) {
//                return DARK_GREEN;
//            }
//
//            float ratio = (otherHP - maxHP) / (maxHP * 0.3f);
//            float hue = hsb_green[0] * (1f - ratio) + hsb_dark_green[0] * ratio;
//            float saturation = hsb_green[1] * (1f - ratio) + hsb_dark_green[1] * ratio;
//            float brightness = hsb_green[2] * (1f - ratio) + hsb_dark_green[2] * ratio;
//            return Color.getHSBColor(hue, saturation, brightness);
//
//        } else if (otherHP > maxHP / 2f) {
//
//            float ratio = (otherHP - maxHP / 2) / (maxHP / 2);
//            float hue = hsb_yellow[0] * (1f - ratio) + hsb_green[0] * ratio;
//            float saturation = hsb_yellow[1] * (1f - ratio) + hsb_green[1] * ratio;
//            float brightness = hsb_yellow[2] * (1f - ratio) + hsb_green[2] * ratio;
//            return Color.getHSBColor(hue, saturation, brightness);
//
//        } else if (0 < otherHP) {
//
//            float ratio = otherHP / maxHP;
//            float hue = hsb_yellow[0] * ratio + hsb_dark_red[0] * (1f - ratio);
//            float saturation = hsb_yellow[1] * ratio + hsb_dark_red[1] * (1f - ratio);
//            float brightness = hsb_yellow[2] * ratio + hsb_dark_red[2] * (1f - ratio);
//            return Color.getHSBColor(hue, saturation, brightness);
//
//        }
//
//        return DARK_RED;
//
//    }
//
//    public static EnumChatFormatting getColoredHP(float otherHP, float maxHP) {
//        if (otherHP > maxHP) {
//            return EnumChatFormatting.DARK_GREEN;
//        } else if (otherHP > maxHP * 3 / 4) {
//            return EnumChatFormatting.GREEN;
//        } else if (otherHP > maxHP / 2) {
//            return EnumChatFormatting.YELLOW;
//        } else if (otherHP > maxHP / 4) {
//            return EnumChatFormatting.RED;
//        } else {
//            return EnumChatFormatting.DARK_RED;
//        }
//    }
//
//}
