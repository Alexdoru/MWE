package fr.alexdoru.mwe.api;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * The interface for the main class of your addon
 */
public interface IMWEAddon {

    /**
     * The name of the addon
     */
    String name();

    /**
     * The minimum version of MWE required for your addon to work
     */
    String targetVersion();

    void preInit(FMLPreInitializationEvent event);

    void init(FMLInitializationEvent event);

    void postInit(FMLPostInitializationEvent event);

}
