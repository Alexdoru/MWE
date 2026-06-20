package fr.alexdoru.mwe.gui;

import fr.alexdoru.configlib.api.IRendererManager;
import fr.alexdoru.mwe.gui.huds.*;

public final class MWERenderers {

    private MWERenderers() {}

    public static ArrowHitHUD arrowHitHUD;
    public static BaseLocationHUD baseLocationHUD;
    public static CreeperPrimedTntHUD creeperPrimedTntHUD;
    public static FKCounterHUD fkCounterHUD;
    public static StrengthHUD strengthHUD;
    public static KillCooldownHUD killCooldownHUD;
    public static LastWitherHPHUD lastWitherHPHUD;
    public static PhoenixBondHUD phoenixBondHUD;
    public static WarcryHUD warcryHUD;

    public static void loadRenderers(IRendererManager rendererManager) {
        rendererManager.registerHUDRenderer(new ArmorHUD());
        rendererManager.registerHUDRenderer(arrowHitHUD = new ArrowHitHUD());
        rendererManager.registerHUDRenderer(baseLocationHUD = new BaseLocationHUD());
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
