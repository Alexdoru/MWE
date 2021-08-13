package fr.alexdoru.megawallsenhancementsmod.mixin;

import java.util.Map;

import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.MCVersion("1.8.9")
public class MixinLoader implements IFMLLoadingPlugin {
	
	public MixinLoader() {
		MixinBootstrap.init();
		Mixins.addConfiguration("mixins.MegaWallsEnhancements.json");
		//MixinEnvironment.getDefaultEnvironment().setSide(MixinEnvironment.Side.CLIENT);
			
	}

	@Override
	public String[] getASMTransformerClass() {
		return new String [0];
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
		
	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}

}
