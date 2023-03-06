package fr.alexdoru.megawallsenhancementsmod.asm;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.8.9")
@IFMLLoadingPlugin.TransformerExclusions({"fr.alexdoru.megawallsenhancementsmod.asm"})
public class ASMLoadingPlugin implements IFMLLoadingPlugin {

    public static final Logger logger = LogManager.getLogger("ASM MegaWallsEnhancements");
    public static boolean isObf;

    @Override
    public String[] getASMTransformerClass() {
        return new String[]{ClassTransformer.class.getName()};
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
        isObf = (boolean) data.get("runtimeDeobfuscationEnabled");
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

    private static Boolean isPatcherLoaded;
    private static Boolean isFeatherLoaded;

    public static boolean isPatcherLoaded() {
        if (isPatcherLoaded == null) {
            try {
                final String PATCHER_CLASS = "club/sk1er/patcher/Patcher.class";
                isPatcherLoaded = ClassLoader.getSystemClassLoader().getResource(PATCHER_CLASS) != null;
            } catch (Exception e) {
                isPatcherLoaded = Boolean.FALSE;
            }
        }
        return isPatcherLoaded;
    }

    public static boolean isFeatherLoaded() {
        if (isFeatherLoaded == null) {
            try {
                final String FEATHER_CLASS = "net/digitalingot/featheropt/FeatherOptMixinPlugin.class";
                isFeatherLoaded = ClassLoader.getSystemClassLoader().getResource(FEATHER_CLASS) != null;
            } catch (Exception e) {
                isFeatherLoaded = Boolean.FALSE;
            }
        }
        return isFeatherLoaded;
    }

}
