package fr.alexdoru.mwe.gui;

import fr.alexdoru.configlib.api.IRendererManager;
import fr.alexdoru.mwe.gui.huds.*;

public final class MWERenderers {

    public final ArrowHitHUD arrowHitHUD;
    public final CreeperPrimedTntHUD creeperPrimedTntHUD;
    public final FKCounterHUD fkCounterHUD;
    public final StrengthHUD strengthHUD;
    public final KillCooldownHUD killCooldownHUD;
    public final LastWitherHPHUD lastWitherHPHUD;
    public final PhoenixBondHUD phoenixBondHUD;
    public final WarcryHUD warcryHUD;

    public MWERenderers(IRendererManager rendererManager) {
        rendererManager.registerHUDRenderer(new ArmorHUD());
        rendererManager.registerHUDRenderer(arrowHitHUD = new ArrowHitHUD());
        rendererManager.registerHUDRenderer(new BaseLocationHUD());
        rendererManager.registerHUDRenderer(new ClassInLobbyHUD());
        rendererManager.registerHUDRenderer(creeperPrimedTntHUD = new CreeperPrimedTntHUD());
        rendererManager.registerHUDRenderer(new EnergyDisplayHUD());
        rendererManager.registerHUDRenderer(fkCounterHUD = new FKCounterHUD());
        rendererManager.registerHUDRenderer(strengthHUD = new StrengthHUD());
        rendererManager.registerHUDRenderer(killCooldownHUD = new KillCooldownHUD());
        rendererManager.registerHUDRenderer(lastWitherHPHUD = new LastWitherHPHUD());
        rendererManager.registerHUDRenderer(new MiniPotionHUD());
        rendererManager.registerHUDRenderer(new PendingReportHUD());
        rendererManager.registerHUDRenderer(phoenixBondHUD = new PhoenixBondHUD());
        rendererManager.registerHUDRenderer(new PotionHUD());
        rendererManager.registerHUDRenderer(new SpeedHUD());
        rendererManager.registerHUDRenderer(new SquadHealthHUD());
        rendererManager.registerHUDRenderer(warcryHUD = new WarcryHUD());
    }

}
