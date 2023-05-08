package fr.alexdoru.megawallsenhancementsmod.gui.elements;

import fr.alexdoru.megawallsenhancementsmod.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class FancyGuiButton extends SimpleGuiButton {

    private final Supplier<String> textSupplier;
    private final List<String> tooltip;

    public FancyGuiButton(int x, int y, Supplier<String> buttonTextSupplier, Runnable action, List<String> tooltip) {
        this(x, y, 200, 20, buttonTextSupplier, action, tooltip);
    }

    public FancyGuiButton(int x, int y, int widthIn, int heightIn, Supplier<String> buttonTextSupplier, Runnable action, List<String> tooltip) {
        super(x, y, widthIn, heightIn, buttonTextSupplier.get(), action);
        this.textSupplier = buttonTextSupplier;
        this.tooltip = wrapTooltipLines(tooltip);
    }

    public FancyGuiButton(int x, int y, Supplier<String> buttonTextSupplier, Runnable action, String... tooltipLines) {
        this(x, y, 200, 20, buttonTextSupplier, action, tooltipLines);
    }

    public FancyGuiButton(int x, int y, int widthIn, int heightIn, Supplier<String> buttonTextSupplier, Runnable action, String... tooltipLines) {
        super(x, y, widthIn, heightIn, buttonTextSupplier.get(), action);
        this.textSupplier = buttonTextSupplier;
        if (tooltipLines == null) {
            this.tooltip = null;
            return;
        }
        this.tooltip = wrapTooltipLines(makeTooltipList(tooltipLines));
    }

    @Override
    public void onButtonPressed() {
        super.onButtonPressed();
        this.displayString = textSupplier.get();
    }

    public List<String> getTooltip() {
        return this.tooltip;
    }

    private static List<String> makeTooltipList(String... tooltipLines) {
        final List<String> list = new ArrayList<>();
        for (int i = 0; i < tooltipLines.length; i++) {
            final String line = tooltipLines[i];
            if (i != 0) {
                list.add("");
            }
            list.add(line);
        }
        return list;
    }

    private static List<String> wrapTooltipLines(List<String> tooltip) {
        if (tooltip == null) {
            return null;
        }
        final int firstWrappedLine = 1;
        final int wrapLength = 40;
        final List<String> list = new ArrayList<>();
        for (int i = 0; i < tooltip.size(); i++) {
            final String line = tooltip.get(i);
            if (line == null) {
                continue;
            }
            if (i >= firstWrappedLine) {
                list.addAll(StringUtil.wrap(line, wrapLength));
            } else {
                list.add(line);
            }
        }
        return list;
    }

}
