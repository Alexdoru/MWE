package fr.alexdoru.configlib.lib.gui;

import fr.alexdoru.configlib.api.ColorPalette;
import fr.alexdoru.configlib.api.IConfigTitleRenderer;
import fr.alexdoru.configlib.lib.ConfigCategoryContainer;
import fr.alexdoru.configlib.lib.ConfigFieldContainer;
import fr.alexdoru.configlib.lib.ConfigHandler;
import fr.alexdoru.configlib.lib.RendererManager;
import fr.alexdoru.configlib.lib.gui.elements.*;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.*;

public class ConfigGuiScreen extends GuiScreen {

    private static final ResourceLocation BLUR = new ResourceLocation("configlib", "blur.json");

    private final ConfigHandler configHandler;
    private final String configName;
    private final ColorPalette colorPalette;
    private final IConfigTitleRenderer titleRenderer;
    private final Map<String, ConfigCategoryContainer> categoryContainerMap;
    private final List<CategoryGuiButton> categoryElements = new ArrayList<>();
    private final List<ConfigUIElement> configElements = new ArrayList<>();
    private final List<ConfigUIElement> renderedConfigElements = new ArrayList<>();
    private GuiTextField searchField;
    private String selectedCategory = "";
    private SliderGuiButton lastInteractedSlider;

    private final Scrollbar configScrollbar = new Scrollbar();
    private final Scrollbar categoryScrollbar = new Scrollbar();

    private int GUI_BORDER_LEFT, GUI_BORDER_TOP, GUI_BORDER_RIGHT, GUI_BORDER_BOTTOM;
    private int GUI_INSIDE_LEFT, GUI_INSIDE_TOP, GUI_INSIDE_RIGHT, GUI_INSIDE_BOTTOM;
    private int CATEGORY_BOX_LEFT, CATEGORY_BOX_TOP, CATEGORY_BOX_RIGHT, CATEGORY_BOX_BOTTOM;
    private int CONFIG_BOX_LEFT, CONFIG_BOX_TOP, CONFIG_BOX_RIGHT, CONFIG_BOX_BOTTOM;
    private int SEARCH_BOX_LEFT, SEARCH_BOX_TOP, SEARCH_BOX_RIGHT, SEARCH_BOX_BOTTOM;

    public ConfigGuiScreen(
            ConfigHandler configHandler,
            String configName,
            List<ConfigCategoryContainer> categories,
            List<ConfigFieldContainer> configFields,
            LinkedHashMap<String, LinkedHashMap<String, List<String>>> configStructure,
            @NotNull ColorPalette colorPalette,
            @Nullable IConfigTitleRenderer titleRenderer,
            RendererManager rendererManager) throws IllegalAccessException {
        this.configHandler = configHandler;
        this.configName = configName;
        this.colorPalette = colorPalette;
        this.titleRenderer = titleRenderer;
        this.categoryContainerMap = new HashMap<>();
        for (final ConfigCategoryContainer container : categories) {
            this.categoryContainerMap.put(container.getCategoryName(), container);
        }
        final Map<String, ConfigUIElement> configButtonsMap = new HashMap<>();
        for (final ConfigFieldContainer configField : configFields) {
            final ConfigUIElement guiButton = configField.getConfigButton(this, rendererManager);
            if (guiButton != null) {
                configButtonsMap.put(configField.getAnnotation().name(), guiButton);
            }
        }
        // sort the category entries in the order they are declared in the config
        // sort the config entries in the order they are declared in the config
        for (final Map.Entry<String, LinkedHashMap<String, List<String>>> entry : configStructure.entrySet()) {
            boolean addedCategory = false;
            final String categoryName = entry.getKey();
            for (final Map.Entry<String, List<String>> subCategoryEntry : entry.getValue().entrySet()) {
                boolean addedSubCategoryTitle = false;
                final String subCategoryName = subCategoryEntry.getKey();
                final List<String> subCatConfigs = subCategoryEntry.getValue();
                for (final String propertyName : subCatConfigs) {
                    final ConfigUIElement guiButton = configButtonsMap.get(propertyName);
                    if (guiButton != null) {
                        if (!addedCategory && !categoryName.isEmpty()) {
                            final ConfigCategoryContainer categoryContainer = this.categoryContainerMap.get(entry.getKey());
                            if (categoryContainer != null) {
                                this.categoryElements.add(categoryContainer.getCategoryButton(this));
                                this.configElements.add(new CategoryHeader(categoryContainer));
                            } else {
                                this.categoryElements.add(new CategoryGuiButton(this, entry.getKey()));
                                this.configElements.add(new CategoryHeader(entry.getKey()));
                            }
                            addedCategory = true;
                        }
                        if (!addedSubCategoryTitle && !subCategoryName.isEmpty()) {
                            this.configElements.add(new SubCategoryHeader(categoryName, subCategoryName));
                            addedSubCategoryTitle = true;
                        }
                        this.configElements.add(guiButton);
                    }
                }
            }
        }
        if (!this.configElements.isEmpty()) {
            this.setFocusedCategory(this.configElements.get(0).getCategory());
        }
    }

    @Override
    public void initGui() {
        final int GUI_WIDTH = Math.min(600, width - 20);
        final int GUI_HEIGHT = Math.min(400, height - 20);

        GUI_BORDER_LEFT = (width - GUI_WIDTH) / 2;
        GUI_BORDER_TOP = (height - GUI_HEIGHT) / 2;
        GUI_BORDER_RIGHT = GUI_BORDER_LEFT + GUI_WIDTH;
        GUI_BORDER_BOTTOM = GUI_BORDER_TOP + GUI_HEIGHT;

        GUI_INSIDE_LEFT = GUI_BORDER_LEFT + 6;
        GUI_INSIDE_TOP = GUI_BORDER_TOP + 6;
        GUI_INSIDE_RIGHT = GUI_BORDER_RIGHT - 6;
        GUI_INSIDE_BOTTOM = GUI_BORDER_BOTTOM - 6;

        CATEGORY_BOX_LEFT = GUI_INSIDE_LEFT;
        CATEGORY_BOX_TOP = GUI_INSIDE_TOP + fontRendererObj.FONT_HEIGHT + 1 + 6;
        CATEGORY_BOX_RIGHT = GUI_INSIDE_LEFT + GUI_WIDTH / 5;
        final int categoryContentHeight = getCategoryContentHeight();
        final int categoryMaxY = CATEGORY_BOX_TOP + 6 + categoryContentHeight + 6;
        CATEGORY_BOX_BOTTOM = Math.min(categoryMaxY, GUI_INSIDE_BOTTOM);

        CONFIG_BOX_LEFT = CATEGORY_BOX_RIGHT + 6;
        CONFIG_BOX_TOP = GUI_INSIDE_TOP + fontRendererObj.FONT_HEIGHT + 1 + 6;
        CONFIG_BOX_RIGHT = GUI_INSIDE_RIGHT;
        CONFIG_BOX_BOTTOM = GUI_INSIDE_BOTTOM;

        final int configBoxWidth = getConfigBoxWidth();
        for (final ConfigUIElement element : this.configElements) {
            element.setBoxWidth(configBoxWidth);
        }

        final int lastCategoryScroll = this.categoryScrollbar.getScroll();
        final int lastConfigScroll = this.configScrollbar.getScroll();

        final boolean isFirstOpening = searchField == null;
        final String prevSearch = isFirstOpening ? null : searchField.getText();
        final boolean prevFocus = !isFirstOpening && searchField.isFocused();
        searchField = new GuiTextField(0, mc.fontRendererObj, GUI_INSIDE_RIGHT - 55 - 6, GUI_INSIDE_TOP, 55, 20);
        searchField.setMaxStringLength(128);
        searchField.setEnableBackgroundDrawing(false);
        searchField.setFocused(prevFocus);
        if (prevSearch != null && !prevSearch.isEmpty()) {
            searchField.setText(prevSearch);
            updateSearch(prevSearch);
        }

        SEARCH_BOX_LEFT = GUI_INSIDE_RIGHT - searchField.width - 8 - 1;
        SEARCH_BOX_TOP = GUI_INSIDE_TOP - 2;
        SEARCH_BOX_RIGHT = GUI_INSIDE_RIGHT;
        SEARCH_BOX_BOTTOM = GUI_INSIDE_TOP + mc.fontRendererObj.FONT_HEIGHT;

        this.categoryScrollbar.init(lastCategoryScroll, categoryContentHeight, CATEGORY_BOX_BOTTOM - CATEGORY_BOX_TOP);
        this.configScrollbar.init(lastConfigScroll, this.getConfigContentHeight(), CONFIG_BOX_BOTTOM - CONFIG_BOX_TOP);

        this.lastInteractedSlider = null;

        this.mc.entityRenderer.loadShader(BLUR);
        super.initGui();
    }

    private int getConfigBoxWidth() {
        return CONFIG_BOX_RIGHT - 6 - (CONFIG_BOX_LEFT + 6);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        final int categoryContentHeight = this.getCategoryContentHeight();
        final int configContentHeight = this.getConfigContentHeight();

        this.categoryScrollbar.updateScrollPos(categoryContentHeight, CATEGORY_BOX_BOTTOM - CATEGORY_BOX_TOP);
        this.configScrollbar.updateScrollPos(configContentHeight, CONFIG_BOX_BOTTOM - CONFIG_BOX_TOP);

        GuiUtil.drawBoxWithOutline(GUI_BORDER_LEFT, GUI_BORDER_TOP, GUI_BORDER_RIGHT, GUI_BORDER_BOTTOM, colorPalette.BACK_GROUND, colorPalette.BACK_GROUND_BORDER);

        if (this.titleRenderer != null) {
            this.titleRenderer.renderTitle(fontRendererObj, colorPalette, GUI_INSIDE_LEFT, GUI_INSIDE_TOP);
        } else {
            fontRendererObj.drawStringWithShadow(configName, GUI_INSIDE_LEFT, GUI_INSIDE_TOP, colorPalette.TITLE_TEXT);
        }

        searchField.xPosition = GUI_INSIDE_RIGHT - Math.min(searchField.width, mc.fontRendererObj.getStringWidth(searchField.getText())) - 8;
        Gui.drawRect(SEARCH_BOX_LEFT, SEARCH_BOX_TOP, SEARCH_BOX_RIGHT, SEARCH_BOX_BOTTOM, colorPalette.SEARCH_BOX);
        if (!searchField.isFocused()) {
            final int xSearchText = GUI_INSIDE_RIGHT - mc.fontRendererObj.getStringWidth("Search...") - 8;
            fontRendererObj.drawStringWithShadow("Search...", xSearchText, searchField.yPosition, colorPalette.SEARCH_BOX_TEXT);
        } else {
            searchField.drawTextBox();
        }

        GuiUtil.drawHorizontalLine(GUI_INSIDE_LEFT, GUI_INSIDE_RIGHT - 1, GUI_INSIDE_TOP + fontRendererObj.FONT_HEIGHT + 1, colorPalette.BELOW_TITLE_LINE);
        GuiUtil.drawBoxWithOutline(CATEGORY_BOX_LEFT, CATEGORY_BOX_TOP, CATEGORY_BOX_RIGHT, CATEGORY_BOX_BOTTOM, colorPalette.INNER_BACKGROUND, colorPalette.INNER_BACKGROUND_BORDER);
        GuiUtil.drawBoxWithOutline(CONFIG_BOX_LEFT, CONFIG_BOX_TOP, CONFIG_BOX_RIGHT, CONFIG_BOX_BOTTOM, colorPalette.INNER_BACKGROUND, colorPalette.INNER_BACKGROUND_BORDER);

        final ScaledResolution res = new ScaledResolution(mc);
        final double scaleW = mc.displayWidth / res.getScaledWidth_double();
        final double scaleH = mc.displayHeight / res.getScaledHeight_double();

        GL11.glEnable(GL11.GL_SCISSOR_TEST);

        GL11.glScissor(
                (int) ((CATEGORY_BOX_LEFT + 1) * scaleW),
                (int) (mc.displayHeight - (CATEGORY_BOX_BOTTOM * scaleH)),
                (int) ((CATEGORY_BOX_RIGHT - 1 - (CATEGORY_BOX_LEFT + 1)) * scaleW),
                (int) ((CATEGORY_BOX_BOTTOM - CATEGORY_BOX_TOP) * scaleH));

        final int categoryDrawX = CATEGORY_BOX_LEFT + 6;
        forEachVisible(this.categoryElements, CATEGORY_BOX_TOP + 6 - this.categoryScrollbar.getScroll(), CATEGORY_BOX_TOP, CATEGORY_BOX_BOTTOM, (element, drawY) -> {
            element.draw(colorPalette, categoryDrawX, drawY);
            return false;
        });

        this.categoryScrollbar.drawScrollbar(
                colorPalette,
                mouseY,
                CATEGORY_BOX_TOP,
                CATEGORY_BOX_RIGHT,
                CATEGORY_BOX_BOTTOM,
                categoryContentHeight
        );

        GL11.glScissor(
                (int) (CONFIG_BOX_LEFT * scaleW),
                (int) (mc.displayHeight - ((CONFIG_BOX_BOTTOM - 1) * scaleH)),
                (int) ((CONFIG_BOX_RIGHT - CONFIG_BOX_LEFT) * scaleW),
                (int) ((CONFIG_BOX_BOTTOM - 1 - (CONFIG_BOX_TOP + 1)) * scaleH));

        final int configDrawX = CONFIG_BOX_LEFT + 6;
        forEachVisible(this.renderedConfigElements, CONFIG_BOX_TOP + 6 - this.configScrollbar.getScroll(), CONFIG_BOX_TOP, CONFIG_BOX_BOTTOM, (element, drawY) -> {
            element.draw(colorPalette, configDrawX, drawY, mouseX, mouseY);
            return false;
        });

        this.configScrollbar.drawScrollbar(
                colorPalette,
                mouseY,
                CONFIG_BOX_TOP,
                CONFIG_BOX_RIGHT,
                CONFIG_BOX_BOTTOM,
                configContentHeight
        );

        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (isMouseInBox(mouseX, mouseY, CATEGORY_BOX_LEFT, CATEGORY_BOX_RIGHT, CATEGORY_BOX_TOP, CATEGORY_BOX_BOTTOM)) {
            forEachVisible(this.categoryElements, CATEGORY_BOX_TOP + 6 - this.categoryScrollbar.getScroll(), CATEGORY_BOX_TOP, CATEGORY_BOX_BOTTOM, (element, drawY) -> element.mouseClicked(mouseX, mouseY, mouseButton));
            if (this.categoryScrollbar.mouseClicked(mouseX, mouseY, mouseButton)) {
                return;
            }
        } else if (isMouseInBox(mouseX, mouseY, CONFIG_BOX_LEFT, CONFIG_BOX_RIGHT, CONFIG_BOX_TOP, CONFIG_BOX_BOTTOM)) {
            forEachVisible(this.renderedConfigElements, CONFIG_BOX_TOP + 6 - this.configScrollbar.getScroll(), CONFIG_BOX_TOP, CONFIG_BOX_BOTTOM, (element, drawY) -> {
                try {
                    if (element.mouseClicked(mouseX, mouseY, mouseButton)) {
                        if (element instanceof SliderGuiButton) {
                            lastInteractedSlider = ((SliderGuiButton) element);
                        }
                        return true;
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Caught exception running mouse click events!", e);
                }
                return false;
            });
            if (this.configScrollbar.mouseClicked(mouseX, mouseY, mouseButton)) {
                return;
            }
        } else if (mouseButton == 0 && isMouseInBox(mouseX, mouseY, SEARCH_BOX_LEFT, SEARCH_BOX_RIGHT, SEARCH_BOX_TOP, SEARCH_BOX_BOTTOM)) {
            searchField.setFocused(true);
            return;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    private boolean isMouseInBox(int mouseX, int mouseY, int boxLeft, int boxRight, int boxTop, int boxBottom) {
        return mouseX >= boxLeft && mouseX < boxRight && mouseY >= boxTop && mouseY < boxBottom;
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        for (final ConfigUIElement element : this.renderedConfigElements) {
            if (element.mouseReleased(mouseX, mouseY, mouseButton)) {
                if (element instanceof SliderGuiButton) {
                    lastInteractedSlider = ((SliderGuiButton) element);
                }
                return;
            }
        }
        this.configScrollbar.mouseReleased(mouseButton);
        this.categoryScrollbar.mouseReleased(mouseButton);
        super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (lastInteractedSlider != null && (keyCode == Keyboard.KEY_LEFT || keyCode == Keyboard.KEY_RIGHT)) {
            lastInteractedSlider.updateSliderFromIncrement(keyCode == Keyboard.KEY_LEFT ? -1 : 1);
            this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
            return;
        }
        if (searchField != null) {
            if (keyCode == 1 && searchField.isFocused()) {
                searchField.setText("");
                updateSearch("");
                searchField.setFocused(false);
                return;
            }
            final String search = searchField.getText();
            final boolean wasUnused = !searchField.isFocused() && searchField.getText().isEmpty();
            searchField.setFocused(true);
            if (searchField.textboxKeyTyped(typedChar, keyCode)) {
                if (!search.equals(searchField.getText())) {
                    updateSearch(searchField.getText());
                }
                return;
            }
            if (wasUnused && searchField.getText().isEmpty()) {
                searchField.setFocused(false);
            }
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        final int wheel = Mouse.getEventDWheel();
        if (wheel != 0) {
            final int mouseX = Mouse.getEventX() * width / mc.displayWidth;
            final int mouseY = height - Mouse.getEventY() * height / mc.displayHeight - 1;
            final int direction = wheel > 0 ? 1 : -1;
            final int amount = Math.min(Math.abs(wheel) * 2, 240);
            if (isMouseInBox(mouseX, mouseY, CATEGORY_BOX_LEFT, CATEGORY_BOX_RIGHT, CATEGORY_BOX_TOP, CATEGORY_BOX_BOTTOM)) {
                this.categoryScrollbar.scheduleScroll(direction, amount);
            } else if (isMouseInBox(mouseX, mouseY, CONFIG_BOX_LEFT, CONFIG_BOX_RIGHT, CONFIG_BOX_TOP, CONFIG_BOX_BOTTOM)) {
                this.configScrollbar.scheduleScroll(direction, amount);
            }
        }
    }

    @Override
    public void onGuiClosed() {
        this.configHandler.saveConfig();
        this.mc.entityRenderer.stopUseShader();
        super.onGuiClosed();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private int getCategoryContentHeight() {
        int height = 0;
        for (final CategoryGuiButton category : this.categoryElements) {
            height += category.getHeight() + 4;
        }
        if (!this.categoryElements.isEmpty()) height -= 4;
        return height;
    }

    private int getConfigContentHeight() {
        int height = 0;
        for (final ConfigUIElement element : this.renderedConfigElements) {
            height += element.getHeight() + 4;
        }
        if (!this.renderedConfigElements.isEmpty()) height -= 4;
        return height;
    }

    public void setFocusedCategory(String categoryName) {
        if (categoryName == null) return;
        this.selectedCategory = categoryName;
        if (searchField != null) {
            this.searchField.setText("");
            this.searchField.setFocused(false);
        }
        this.configScrollbar.resetScroll();
        this.lastInteractedSlider = null;
        this.renderedConfigElements.clear();
        for (final ConfigUIElement element : this.configElements) {
            if (categoryName.equals(element.getCategory())) {
                this.renderedConfigElements.add(element);
            }
        }
    }

    private void updateSearch(String search) {
        if (search.isEmpty()) {
            this.setFocusedCategory(selectedCategory);
            return;
        }
        search = search.toLowerCase();
        this.configScrollbar.resetScroll();
        this.lastInteractedSlider = null;
        this.renderedConfigElements.clear();
        String lastKey = null;
        final int configBoxWidth = getConfigBoxWidth();
        for (final ConfigUIElement element : this.configElements) {
            if (!(element instanceof SubCategoryHeader) && element.matchSearch(search)) {
                final String subCategoryKey = element.getCategory() + "$" + element.getSubCategory();
                if (!Objects.equals(lastKey, subCategoryKey)) {
                    lastKey = subCategoryKey;
                    final ConfigCategoryContainer categoryContainer = this.categoryContainerMap.get(element.getCategory());
                    final String displayText;
                    if (categoryContainer != null) {
                        displayText = categoryContainer.getAnnotation().displayname() + EnumChatFormatting.RESET + (element.getSubCategory().isEmpty() ? "" : " - " + element.getSubCategory());
                    } else {
                        displayText = element.getCategory() + (element.getSubCategory().isEmpty() ? "" : " - " + element.getSubCategory());
                    }
                    final TextLabel textLabel = new TextLabel(displayText);
                    textLabel.setBoxWidth(configBoxWidth);
                    this.renderedConfigElements.add(textLabel);
                }
                this.renderedConfigElements.add(element);
            }
        }
        if (this.renderedConfigElements.isEmpty()) {
            final TextLabel textLabel = new TextLabel("Nothing was found! :(");
            textLabel.setBoxWidth(configBoxWidth);
            this.renderedConfigElements.add(textLabel);
        }
    }

    private static <T extends SizedElement> void forEachVisible(List<T> elements, int startY, int boxTop, int boxBottom, ElementVisitor<T> visitor) {
        int drawY = startY;
        for (final T element : elements) {
            final int elementHeight = element.getHeight();
            if (drawY + elementHeight >= boxTop && drawY <= boxBottom) {
                if (visitor.visit(element, drawY)) return;
            }
            drawY += elementHeight + 4;
        }
    }

    @FunctionalInterface
    interface ElementVisitor<T extends SizedElement> {

        boolean visit(T element, int drawY);

    }

}
