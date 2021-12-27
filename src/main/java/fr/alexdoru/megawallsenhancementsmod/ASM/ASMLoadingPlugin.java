//package fr.alexdoru.megawallsenhancementsmod.ASM;
//
//import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
//
//import java.util.Map;
//
//@IFMLLoadingPlugin.MCVersion("1.8.9")
//@IFMLLoadingPlugin.TransformerExclusions({"fr/alexdoru/"})
//public class ASMLoadingPlugin implements IFMLLoadingPlugin {
//
//    public static Boolean isObf;
//
//    @Override
//    public String[] getASMTransformerClass() {
//        return new String[]{ClassTransformer.class.getName()};
//    }
//
//    @Override
//    public String getModContainerClass() {
//        return null;
//    }
//
//    @Override
//    public String getSetupClass() {
//        return null;
//    }
//
//    @Override
//    public void injectData(Map<String, Object> data) {
//        isObf = (Boolean) data.get("runtimeDeobfuscationEnabled");
//    }
//
//    @Override
//    public String getAccessTransformerClass() {
//        return null;
//    }
//
//}
