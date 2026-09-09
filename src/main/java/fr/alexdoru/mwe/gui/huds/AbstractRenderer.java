package fr.alexdoru.mwe.gui.huds;

import fr.alexdoru.configlib.api.IRenderer;
import fr.alexdoru.configlib.api.RendererPosition;
import fr.alexdoru.mwe.api.IScoreboardParser;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

public abstract class AbstractRenderer extends Gui implements IRenderer {

    protected static final ResourceLocation inventoryBackground = new ResourceLocation("textures/gui/container/inventory.png");
    protected final RendererPosition rendererPosition;
    protected final IScoreboardParser parser = ScoreboardTracker.getParser();

    public AbstractRenderer(RendererPosition rendererPosition) {
        this.rendererPosition = rendererPosition;
    }

    @Override
    public RendererPosition getPosition() {
        return this.rendererPosition;
    }

}
