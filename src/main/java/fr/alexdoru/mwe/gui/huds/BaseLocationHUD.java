package fr.alexdoru.mwe.gui.huds;

import fr.alexdoru.mwe.api.enums.MWMap;
import fr.alexdoru.mwe.api.events.MapEvent;
import fr.alexdoru.mwe.config.MWEConfig;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BaseLocationHUD extends SimpleTextRenderer {

    private MWMap currentMap;

    public BaseLocationHUD() {
        super(MWEConfig.baseLocationHUDPosition);
        this.setDummyText(EnumChatFormatting.GREEN + "GREEN");
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onMapEvent(MapEvent event) {
        currentMap = MWMap.fromName(event.mapName);
    }

    @Override
    protected void updateText() {
        this.setText(currentMap.getPlayerBaseLocation(mc.thePlayer));
    }

    @Override
    public boolean isEnabled(long currentTimeMillis) {
        return this.rendererPosition.isEnabled() && (this.parser.isInMwGame() || this.parser.isMWReplay()) && this.currentMap != null;
    }

}
