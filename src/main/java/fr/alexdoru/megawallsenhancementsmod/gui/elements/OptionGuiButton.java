package fr.alexdoru.megawallsenhancementsmod.gui.elements;

import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class OptionGuiButton extends FancyGuiButton {

    public OptionGuiButton(int x, int y, String optionName, Consumer<Boolean> setter, Supplier<Boolean> getter, List<String> tooltip) {
        this(x, y, 200, 20, optionName, setter, getter, tooltip);
    }

    public OptionGuiButton(int x, int y, int widthIn, int heightIn, String optionName, Consumer<Boolean> setter, Supplier<Boolean> getter, List<String> tooltip) {
        super(x, y,
                widthIn, heightIn,
                () -> optionName + " : " + (getter.get() ? EnumChatFormatting.GREEN + "Enabled" : EnumChatFormatting.RED + "Disabled"),
                () -> setter.accept(!getter.get()),
                tooltip);
    }

    public OptionGuiButton(int x, int y, String optionName, Consumer<Boolean> setter, Supplier<Boolean> getter, String... tooltipLines) {
        this(x, y, 200, 20, optionName, setter, getter, tooltipLines);
    }

    public OptionGuiButton(int x, int y, int widthIn, int heightIn, String optionName, Consumer<Boolean> setter, Supplier<Boolean> getter, String... tooltipLines) {
        super(x, y,
                widthIn, heightIn,
                () -> optionName + " : " + (getter.get() ? EnumChatFormatting.GREEN + "Enabled" : EnumChatFormatting.RED + "Disabled"),
                () -> setter.accept(!getter.get()),
                makeTooltipList(optionName, tooltipLines));
    }

    private static List<String> makeTooltipList(String optionName, String... tooltipLines) {
        final List<String> list = new ArrayList<>();
        list.add(EnumChatFormatting.GREEN + optionName);
        for (final String line : tooltipLines) {
            list.add("");
            list.add(line);
        }
        return list;
    }

}
