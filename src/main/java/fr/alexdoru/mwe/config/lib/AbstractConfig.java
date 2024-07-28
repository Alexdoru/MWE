package fr.alexdoru.mwe.config.lib;

import fr.alexdoru.mwe.chat.ChatUtil;
import fr.alexdoru.mwe.gui.guiapi.GuiPosition;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.*;

public abstract class AbstractConfig {

    private final Class<?> configClass;
    private final Configuration config;
    private final List<ConfigField> configFields = new ArrayList<>();

    protected AbstractConfig(Class<?> clazz, File file) {
        configClass = clazz;
        config = new Configuration(file);
        config.load();
        try {
            final Map<String, Property> propertyMap = new HashMap<>();
            for (final Field field : configClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(ConfigProperty.class)) {
                    configFields.add(new ConfigField(field, propertyMap, config));
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        try {
            setConfigPropertyOrder();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (config.hasChanged()) {
            config.save();
        }
    }

    public void save() {
        try {
            for (final ConfigField configField : configFields) {
                configField.saveFieldValueToConfig();
            }
        } catch (IllegalAccessException e) {
            ChatUtil.addChatMessage(ChatUtil.getTagMW() + EnumChatFormatting.DARK_RED + "Failed to save the config!");
            e.printStackTrace();
        }
        if (config.hasChanged()) {
            config.save();
        }
    }

    private void setConfigPropertyOrder() throws IOException {
        // We need to parse the class bytes with ASM because
        // the reflection method class.getDeclaredFields()
        // returns the fields in no specific order.
        final InputStream is = configClass.getResourceAsStream('/' + configClass.getName().replace('.', '/') + ".class");
        if (is == null) return;
        final ClassReader cr = new ClassReader(is);
        final ClassNode cn = new ClassNode();
        cr.accept(cn, ClassReader.SKIP_CODE);
        final Set<String> configNames = new HashSet<>();
        final Map<String, List<String>> categoryOrderMap = new LinkedHashMap<>();
        final String annotationDesc = Type.getDescriptor(ConfigProperty.class);
        final String guiPositionDesc = Type.getDescriptor(GuiPosition.class);
        for (final FieldNode field : cn.fields) {
            if (field.visibleAnnotations == null) continue;
            for (final AnnotationNode annotation : field.visibleAnnotations) {
                if (!annotation.desc.equals(annotationDesc)) continue;
                String configCategory = null;
                String configName = null;
                for (int i = 0; i < annotation.values.size(); i += 2) {
                    final Object name = annotation.values.get(i);
                    final Object value = annotation.values.get(i + 1);
                    if ("category".equals(name) && value instanceof String) {
                        configCategory = ((String) value);
                    } else if ("name".equals(name) && value instanceof String) {
                        configName = (String) value;
                    }
                }
                if (configCategory != null && configName != null) {
                    if (!configNames.add(configName)) {
                        throw new IllegalStateException("Duplicate key names in config properties");
                    }
                    final List<String> list = categoryOrderMap.computeIfAbsent(configCategory, k -> new ArrayList<>());
                    final boolean isGuiPosition = field.desc.equals(guiPositionDesc);
                    if (isGuiPosition) {
                        list.add("Xpos " + configName);
                        list.add("Ypos " + configName);
                    } else {
                        list.add(configName);
                    }
                }
            }
        }
        for (final Map.Entry<String, List<String>> entry : categoryOrderMap.entrySet()) {
            config.setCategoryPropertyOrder(entry.getKey(), entry.getValue());
        }
    }

}
