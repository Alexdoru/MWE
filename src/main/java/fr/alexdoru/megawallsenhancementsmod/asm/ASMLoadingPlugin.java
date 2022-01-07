package fr.alexdoru.megawallsenhancementsmod.asm;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.8.9")
@IFMLLoadingPlugin.TransformerExclusions({"fr.alexdoru.megawallsenhancementsmod.asm"})
public class ASMLoadingPlugin implements IFMLLoadingPlugin {

    public static Boolean isObf;
    public static final Logger logger = LogManager.getLogger("MWEn ASM");

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
        isObf = (Boolean) data.get("runtimeDeobfuscationEnabled");
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

}
