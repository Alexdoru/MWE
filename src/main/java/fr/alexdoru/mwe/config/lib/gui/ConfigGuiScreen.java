package fr.alexdoru.mwe.config.lib.gui;

import fr.alexdoru.mwe.MWE;
import fr.alexdoru.mwe.config.lib.AbstractConfig;
import fr.alexdoru.mwe.config.lib.ConfigCategoryContainer;
import fr.alexdoru.mwe.config.lib.ConfigFieldContainer;
import fr.alexdoru.mwe.config.lib.gui.elements.*;
import fr.alexdoru.mwe.gui.GuiUtil;
import fr.alexdoru.mwe.utils.SoundUtil;
import fr.alexdoru.mwe.utils.StringUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeVersion;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.*;

public class ConfigGuiScreen extends GuiScreen {

    private static final ResourceLocation BLUR = new ResourceLocation("mwe", "blur.json");
    private static final ResourceLocation DORU_SKIN = new ResourceLocation("mwe", "doru_skin.png");
    private final AbstractConfig config;
    private final Map<String, ConfigCategoryContainer> categoryContainerMap;
    private final List<CategoryGuiButton> categoryElements = new ArrayList<>();
    private final List<ConfigUIElement> configElements = new ArrayList<>();
    private final List<ConfigUIElement> renderedConfigElements = new ArrayList<>();
    private GuiTextField searchField;
    private String selectedCategory = "";
    private SliderGuiButton lastInteractedSlider;

    private int scroll;
    private int scrollDirection;
    private int amountToScroll;
    private boolean draggingScrollBar;
    private int scrollGrabbedAtY;
    private long lastScrollTime;

    private int GUI_BORDER_LEFT, GUI_BORDER_TOP, GUI_BORDER_RIGHT, GUI_BORDER_BOTTOM;
    private int GUI_INSIDE_LEFT, GUI_INSIDE_TOP, GUI_INSIDE_RIGHT, GUI_INSIDE_BOTTOM;
    private int CATEGORY_BOX_LEFT, CATEGORY_BOX_TOP, CATEGORY_BOX_RIGHT, CATEGORY_BOX_BOTTOM;
    private int CONFIG_BOX_LEFT, CONFIG_BOX_TOP, CONFIG_BOX_RIGHT, CONFIG_BOX_BOTTOM;
    private int SEARCH_BOX_LEFT, SEARCH_BOX_TOP, SEARCH_BOX_RIGHT, SEARCH_BOX_BOTTOM;
    private int SCROLL_BAR_LEFT, SCROLL_BAR_TOP, SCROLL_BAR_RIGHT, SCROLL_BAR_BOTTOM;

    public ConfigGuiScreen(AbstractConfig config, List<ConfigCategoryContainer> categories, List<ConfigFieldContainer> configFields, LinkedHashMap<String, LinkedHashMap<String, List<String>>> configStructure) throws IllegalAccessException {
        this.config = config;
        this.categoryContainerMap = new HashMap<>();
        for (final ConfigCategoryContainer container : categories) {
            this.categoryContainerMap.put(container.getCategoryName(), container);
        }
        final Map<String, ConfigUIElement> configButtonsMap = new HashMap<>();
        for (final ConfigFieldContainer configField : configFields) {
            final ConfigUIElement guiButton = configField.getConfigButton(this);
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
                for (final String configName : subCatConfigs) {
                    final ConfigUIElement guiButton = configButtonsMap.get(configName);
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
        int categoryDrawY = CATEGORY_BOX_TOP + 6;
        for (final CategoryGuiButton category : this.categoryElements) {
            categoryDrawY += category.getHeight() + 4;
        }
        CATEGORY_BOX_BOTTOM = categoryDrawY - 4 + 6;

        CONFIG_BOX_LEFT = CATEGORY_BOX_RIGHT + 6;
        CONFIG_BOX_TOP = GUI_INSIDE_TOP + fontRendererObj.FONT_HEIGHT + 1 + 6;
        CONFIG_BOX_RIGHT = GUI_INSIDE_RIGHT;
        CONFIG_BOX_BOTTOM = GUI_INSIDE_BOTTOM;

        final int configBoxWidth = getConfigBoxWidth();
        for (final ConfigUIElement element : this.configElements) {
            element.setBoxWidth(configBoxWidth);
        }

        final boolean isFirstOpening = searchField == null;
        final String prevSearch = isFirstOpening ? null : searchField.getText();
        final boolean prevFocus = !isFirstOpening && searchField.isFocused();
        final int lastScroll = this.scroll;
        searchField = new GuiTextField(0, mc.fontRendererObj, GUI_INSIDE_RIGHT - 55 - 6, GUI_INSIDE_TOP, 55, 20);
        searchField.setMaxStringLength(128);
        searchField.setEnableBackgroundDrawing(false);
        searchField.setFocused(prevFocus);
        if (!StringUtil.isNullOrEmpty(prevSearch)) {
            searchField.setText(prevSearch);
            updateSearch(prevSearch);
        }

        SEARCH_BOX_LEFT = GUI_INSIDE_RIGHT - searchField.width - 8 - 1;
        SEARCH_BOX_TOP = GUI_INSIDE_TOP - 2;
        SEARCH_BOX_RIGHT = GUI_INSIDE_RIGHT;
        SEARCH_BOX_BOTTOM = GUI_INSIDE_TOP + mc.fontRendererObj.FONT_HEIGHT;

        this.scroll = 0;
        this.scroll(-lastScroll);
        this.lastInteractedSlider = null;

        if (ForgeVersion.getVersion().contains("2318")) {
            mc.entityRenderer.loadShader(BLUR);
        }
        super.initGui();
    }

    private int getConfigBoxWidth() {
        return CONFIG_BOX_RIGHT - 6 - (CONFIG_BOX_LEFT + 6);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        // smooth scrolling updated on every frame instead of once per tick
        if (amountToScroll > 0) {
            final long time = System.currentTimeMillis();
            if (time - lastScrollTime > 1) {
                this.scroll(scrollDirection * 3);
                amountToScroll -= 3;
                lastScrollTime = time;
            }
        }

        GuiUtil.drawBoxWithOutline(GUI_BORDER_LEFT, GUI_BORDER_TOP, GUI_BORDER_RIGHT, GUI_BORDER_BOTTOM, 0xFF323237, 0xFF4C4C51);
        final String modnameText = MWE.modName + " - " + MWE.version + EnumChatFormatting.GRAY + " created by ";
        fontRendererObj.drawStringWithShadow(modnameText, GUI_INSIDE_LEFT, GUI_INSIDE_TOP, 0xFFFFFFFF);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        mc.getTextureManager().bindTexture(DORU_SKIN);
        final int headPosX = GUI_INSIDE_LEFT + fontRendererObj.getStringWidth(modnameText);
        Gui.drawScaledCustomSizeModalRect(headPosX, GUI_INSIDE_TOP, 8, 8, 8, 8, 8, 8, 64.0F, 32.0F);
        Gui.drawScaledCustomSizeModalRect(headPosX, GUI_INSIDE_TOP, 40, 8, 8, 8, 8, 8, 64.0F, 32.0F);
        fontRendererObj.drawStringWithShadow(EnumChatFormatting.GOLD + "Alexdoru", headPosX + 10, GUI_INSIDE_TOP, 0xFFFFFFFF);

        searchField.xPosition = GUI_INSIDE_RIGHT - Math.min(searchField.width, mc.fontRendererObj.getStringWidth(searchField.getText())) - 8;
        drawRect(SEARCH_BOX_LEFT, SEARCH_BOX_TOP, SEARCH_BOX_RIGHT, SEARCH_BOX_BOTTOM, 0xFF515156);
        if (!searchField.isFocused()) {
            final int xSearchText = GUI_INSIDE_RIGHT - mc.fontRendererObj.getStringWidth("Search...") - 8;
            fontRendererObj.drawStringWithShadow("Search...", xSearchText, searchField.yPosition, 0xFFFFFFFF);
        } else {
            searchField.drawTextBox();
        }

        GuiUtil.drawHorizontalLine(GUI_INSIDE_LEFT, GUI_INSIDE_RIGHT - 1, GUI_INSIDE_TOP + fontRendererObj.FONT_HEIGHT + 1, 0xFF545459);
        GuiUtil.drawBoxWithOutline(CATEGORY_BOX_LEFT, CATEGORY_BOX_TOP, CATEGORY_BOX_RIGHT, CATEGORY_BOX_BOTTOM, 0xFF3C3C41, 0xFF171717);
        GuiUtil.drawBoxWithOutline(CONFIG_BOX_LEFT, CONFIG_BOX_TOP, CONFIG_BOX_RIGHT, CONFIG_BOX_BOTTOM, 0xFF3C3C41, 0xFF171717);

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
        int categoryDrawY = CATEGORY_BOX_TOP + 6;
        for (final CategoryGuiButton category : this.categoryElements) {
            category.draw(categoryDrawX, categoryDrawY);
            categoryDrawY += category.getHeight() + 4;
        }

        GL11.glScissor(
                (int) (CONFIG_BOX_LEFT * scaleW),
                (int) (mc.displayHeight - ((CONFIG_BOX_BOTTOM - 1) * scaleH)),
                (int) ((CONFIG_BOX_RIGHT - CONFIG_BOX_LEFT) * scaleW),
                (int) ((CONFIG_BOX_BOTTOM - 1 - (CONFIG_BOX_TOP + 1)) * scaleH));

        final int configDrawX = CONFIG_BOX_LEFT + 6;
        int configDrawY = CONFIG_BOX_TOP + 6 - scroll;
        int allElementsHeight = 0;
        for (final ConfigUIElement element : this.renderedConfigElements) {
            final int elementHeight = element.getHeight();
            if (configDrawY + elementHeight + 4 >= CONFIG_BOX_TOP && configDrawY <= CONFIG_BOX_BOTTOM) {
                element.draw(configDrawX, configDrawY, mouseX, mouseY);
            }
            configDrawY += elementHeight + 4;
            allElementsHeight += elementHeight + 4;
        }

        final int configBoxHeight = CONFIG_BOX_BOTTOM - CONFIG_BOX_TOP - 2;
        if (allElementsHeight > configBoxHeight) {
            final int scrollBarSize = configBoxHeight * configBoxHeight / allElementsHeight;
            final int minScrollBarY = CONFIG_BOX_TOP + 1 + 1;
            final int maxScrollBarY = CONFIG_BOX_BOTTOM - 1 - scrollBarSize - 1;
            if (draggingScrollBar) {
                final int relativeMouseY = mouseY - minScrollBarY - scrollGrabbedAtY;
                final int newScroll = relativeMouseY * (allElementsHeight - (CONFIG_BOX_BOTTOM - CONFIG_BOX_TOP)) / (maxScrollBarY - minScrollBarY);
                this.scroll(this.scroll - newScroll);
            }
            SCROLL_BAR_LEFT = CONFIG_BOX_RIGHT - 5;
            SCROLL_BAR_TOP = ((maxScrollBarY - minScrollBarY) * scroll) / (allElementsHeight - configBoxHeight) + minScrollBarY;
            SCROLL_BAR_RIGHT = CONFIG_BOX_RIGHT - 5 + 3;
            SCROLL_BAR_BOTTOM = SCROLL_BAR_TOP + scrollBarSize;
            GuiUtil.drawVerticalLine(CONFIG_BOX_RIGHT - 4, CONFIG_BOX_TOP + 4, CONFIG_BOX_BOTTOM - 4, 0xFF202020);
            Gui.drawRect(SCROLL_BAR_LEFT, SCROLL_BAR_TOP, SCROLL_BAR_RIGHT, SCROLL_BAR_BOTTOM, 0xFFC0C0C0);
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (isMouseInBox(mouseX, mouseY, CATEGORY_BOX_LEFT, CATEGORY_BOX_RIGHT, CATEGORY_BOX_TOP, CATEGORY_BOX_BOTTOM)) {
            for (final CategoryGuiButton category : this.categoryElements) {
                if (category.mouseClicked(mouseX, mouseY, mouseButton)) {
                    return;
                }
            }
        } else if (isMouseInBox(mouseX, mouseY, CONFIG_BOX_LEFT, CONFIG_BOX_RIGHT, CONFIG_BOX_TOP, CONFIG_BOX_BOTTOM)) {
            try {
                for (final ConfigUIElement element : this.renderedConfigElements) {
                    if (element.mouseClicked(mouseX, mouseY, mouseButton)) {
                        if (element instanceof SliderGuiButton) {
                            lastInteractedSlider = ((SliderGuiButton) element);
                        }
                        return;
                    }
                }
            } catch (IllegalAccessException ignored) {
                throw new RuntimeException("Caught  to generate the config menu!");
            }
            if (mouseButton == 0 && isMouseInBox(mouseX, mouseY, SCROLL_BAR_LEFT, SCROLL_BAR_RIGHT, SCROLL_BAR_TOP, SCROLL_BAR_BOTTOM)) {
                draggingScrollBar = true;
                scrollGrabbedAtY = mouseY - SCROLL_BAR_TOP;
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
        if (mouseButton == 0 && draggingScrollBar) {
            draggingScrollBar = false;
        }
        super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (lastInteractedSlider != null && (keyCode == Keyboard.KEY_LEFT || keyCode == Keyboard.KEY_RIGHT)) {
            lastInteractedSlider.updateSliderFromIncrement(keyCode == Keyboard.KEY_LEFT ? -1 : 1);
            SoundUtil.playButtonPress();
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
        final int i = Mouse.getEventDWheel();
        if (i != 0) {
            if (i > 1) {
                scrollDirection = 1;
                amountToScroll = 100;
            }
            if (i < -1) {
                scrollDirection = -1;
                amountToScroll = 100;
            }
        }
    }

    @Override
    public void onGuiClosed() {
        config.save();
        if (ForgeVersion.getVersion().contains("2318")) {
            mc.entityRenderer.stopUseShader();
        }
        super.onGuiClosed();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private void scroll(int amount) {
        this.scroll = this.scroll - amount;
        int maxDrawY = 6;
        for (final ConfigUIElement element : this.renderedConfigElements) {
            maxDrawY += element.getHeight() + 4;
        }
        if (this.scroll > maxDrawY - (CONFIG_BOX_BOTTOM - CONFIG_BOX_TOP)) {
            this.scroll = maxDrawY - (CONFIG_BOX_BOTTOM - CONFIG_BOX_TOP);
            this.amountToScroll = 0;
        }
        if (this.scroll <= 0) {
            this.scroll = 0;
            this.amountToScroll = 0;
        }
    }

    public void setFocusedCategory(String categoryName) {
        if (categoryName == null) return;
        this.selectedCategory = categoryName;
        if (searchField != null) {
            this.searchField.setText("");
            this.searchField.setFocused(false);
        }
        this.scroll = 0;
        this.amountToScroll = 0;
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
            if (selectedCategory != null) {
                this.setFocusedCategory(selectedCategory);
            }
            return;
        }
        search = search.toLowerCase();
        this.scroll = 0;
        this.amountToScroll = 0;
        this.lastInteractedSlider = null;
        this.renderedConfigElements.clear();
        String lastHeader = null;
        final int configBoxWidth = getConfigBoxWidth();
        for (final ConfigUIElement element : this.configElements) {
            if (!(element instanceof SubCategoryHeader) && element.matchSearch(search)) {
                final ConfigCategoryContainer categoryContainer = this.categoryContainerMap.get(element.getCategory());
                final String headerKey;
                if (categoryContainer != null) {
                    headerKey = categoryContainer.getAnnotation().displayname() + EnumChatFormatting.RESET + (element.getSubCategory().isEmpty() ? "" : " - " + element.getSubCategory());
                } else {
                    headerKey = element.getCategory() + (element.getSubCategory().isEmpty() ? "" : " - " + element.getSubCategory());
                }
                if (!Objects.equals(lastHeader, headerKey)) {
                    final SubCategoryHeader header = new SubCategoryHeader(null, headerKey);
                    header.setBoxWidth(configBoxWidth);
                    this.renderedConfigElements.add(header);
                    lastHeader = headerKey;
                }
                this.renderedConfigElements.add(element);
            }
        }
        if (this.renderedConfigElements.isEmpty()) {
            final SubCategoryHeader header = new SubCategoryHeader(null, "Nothing was found! :(");
            header.setBoxWidth(configBoxWidth);
            this.renderedConfigElements.add(header);
        }
    }

}
