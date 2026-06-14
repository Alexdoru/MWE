package fr.alexdoru.mwe.asm;

import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.*;

@IFMLLoadingPlugin.Name("MWE ASM")
@IFMLLoadingPlugin.MCVersion("1.8.9")
@IFMLLoadingPlugin.TransformerExclusions({
        "fr.alexdoru.mwe.api.asm",
        "fr.alexdoru.mwe.asm.mappings",
        "fr.alexdoru.mwe.asm.transformers",
})
public class MWELoadingPlugin implements IFMLLoadingPlugin {

    public static final Logger logger = LogManager.getLogger("MWE ASM");
    private static Boolean isObf;

    @Override
    public String[] getASMTransformerClass() {
        return new String[]{"fr.alexdoru.mwe.asm.transformers.MWEClassTransformer"};
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

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T> List<T> loadClasses(String blackboardkey, Class<T> itf) {
        final List<T> instances = new ArrayList<>();
        final Object o = Launch.blackboard.get(blackboardkey);
        if (o instanceof ArrayList) {
            Map<String, Class<?>> cachedClasses = null;
            try {
                final Field field = LaunchClassLoader.class.getDeclaredField("cachedClasses");
                field.setAccessible(true);
                cachedClasses = (Map<String, Class<?>>) field.get(Launch.classLoader);
            } catch (Exception ex) {
                logger.error("Could not reflect classloader's cached classes", ex);
            }
            final ArrayList classnames = ((ArrayList) o);
            final Set<String> loadedClasses = new HashSet<>();
            for (final Object name : classnames) {
                if (name instanceof String) {
                    final String classname = ((String) name);
                    if (cachedClasses != null && cachedClasses.containsKey(name)) {
                        throw new IllegalStateException("Class " + classname + " was loaded too early");
                    }
                    try {
                        final Class<?> objectClass = Class.forName(classname);
                        if (!itf.isAssignableFrom(objectClass)) {
                            throw new IllegalStateException(classname + " does not implement " + itf.getName() + " interface");
                        }
                        final T object = (T) objectClass.newInstance();
                        if (loadedClasses.contains(classname)) {
                            throw new IllegalStateException("Duplicate instances of " + itf.getName() + " loaded");
                        }
                        instances.add(object);
                        loadedClasses.add(classname);
                    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return instances;
    }

}
