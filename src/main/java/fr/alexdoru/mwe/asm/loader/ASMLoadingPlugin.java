package fr.alexdoru.mwe.asm.loader;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.8.9")
@IFMLLoadingPlugin.TransformerExclusions({
        "fr.alexdoru.mwe.asm.loader",
        "fr.alexdoru.mwe.asm.mappings",
        "fr.alexdoru.mwe.asm.transformers",
})
public class ASMLoadingPlugin implements IFMLLoadingPlugin {

    public static final Logger logger = LogManager.getLogger("ASM MWE");
    // JVM flags
    // -Dmwe.moreclassdump=true
    // -Dmwe.classdump=true
    private static final boolean MORE_CLASS_DUMP = Boolean.parseBoolean(System.getProperty("mwe.moreclassdump", "false"));
    private static final boolean CLASS_DUMP = MORE_CLASS_DUMP || Boolean.parseBoolean(System.getProperty("mwe.classdump", "false"));
    private static boolean isObf;

    @Override
    public String[] getASMTransformerClass() {
        return new String[]{MWEClassTransformer.class.getName()};
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

    public static boolean isObf() {
        return isObf;
    }

    public static boolean classDump() {
        return !isObf() || CLASS_DUMP;
    }

    public static boolean moreClassDump() {
        return MORE_CLASS_DUMP;
    }

}
