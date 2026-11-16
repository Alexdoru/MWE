package fr.alexdoru.mwe.gui.huds;

import fr.alexdoru.configlib.api.IRendererV2;
import fr.alexdoru.configlib.api.RendererPositionV2;
import fr.alexdoru.mwe.api.IScoreboardParser;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public abstract class AbstractRenderer extends Gui implements IRendererV2 {

    protected final Minecraft mc = Minecraft.getMinecraft();
    protected final RendererPositionV2 rendererPosition;
    protected final IScoreboardParser parser = ScoreboardTracker.getParser();

    public AbstractRenderer(RendererPositionV2 rendererPosition) {
        this.rendererPosition = rendererPosition;
    }

    @Override
    public final RendererPositionV2 getPosition() {
        return this.rendererPosition;
    }

}
