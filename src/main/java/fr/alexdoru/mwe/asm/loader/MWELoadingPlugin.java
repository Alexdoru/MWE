package fr.alexdoru.mwe.asm.loader;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

@IFMLLoadingPlugin.Name("MWE ASM")
@IFMLLoadingPlugin.MCVersion("1.8.9")
@IFMLLoadingPlugin.TransformerExclusions({
        "fr.alexdoru.mwe.asm.loader",
        "fr.alexdoru.mwe.asm.mappings",
        "fr.alexdoru.mwe.asm.transformers",
})
public class MWELoadingPlugin implements IFMLLoadingPlugin {

    public static final Logger logger = LogManager.getLogger("ASM MWE");
    private static final boolean MORE_CLASS_DUMP = Boolean.getBoolean("mwe.moreclassdump");
    private static final boolean CLASS_DUMP = MORE_CLASS_DUMP || Boolean.getBoolean("mwe.classdump");
    private static Boolean isObf;

    @Override
    public String[] getASMTransformerClass() {
        return new String[]{"fr.alexdoru.mwe.asm.loader.MWEClassTransformer"};
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
        isObf = (Boolean) data.get("runtimeDeobfuscationEnabled");
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

    private static Boolean isPatcherLoaded;
    private static Boolean isFeatherLoaded;

    public static boolean isPatcherPresent() {
        if (isPatcherLoaded == null) {
            try {
                final String PATCHER_CLASS = "club/sk1er/patcher/Patcher.class";
                isPatcherLoaded = MWELoadingPlugin.class.getClassLoader().getResource(PATCHER_CLASS) != null;
            } catch (Exception e) {
                isPatcherLoaded = Boolean.FALSE;
            }
        }
        return isPatcherLoaded;
    }

    public static boolean isFeatherPresent() {
        if (isFeatherLoaded == null) {
            try {
                final String FEATHER_CLASS = "net/digitalingot/featheropt/FeatherOptMixinPlugin.class";
                isFeatherLoaded = MWELoadingPlugin.class.getClassLoader().getResource(FEATHER_CLASS) != null;
            } catch (Exception e) {
                isFeatherLoaded = Boolean.FALSE;
            }
        }
        return isFeatherLoaded;
    }

    public static boolean isObf() {
        if (isObf == null) {
            throw new IllegalStateException("Obfuscation state has been accessed too early!");
        }
        return isObf;
    }

    public static boolean classDump() {
        return CLASS_DUMP;
    }

    public static boolean moreClassDump() {
        return MORE_CLASS_DUMP;
    }

}
