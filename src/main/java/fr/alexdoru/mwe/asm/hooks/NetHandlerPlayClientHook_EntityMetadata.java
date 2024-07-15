package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.asm.interfaces.IWitherColor;
import fr.alexdoru.mwe.utils.ColorUtil;
import fr.alexdoru.mwe.utils.StringUtil;
import net.minecraft.entity.Entity;

@SuppressWarnings("unused")
public class NetHandlerPlayClientHook_EntityMetadata {

    public static void onEntityMetadata(Entity entity) {
        if (entity instanceof IWitherColor) {
            final char color = StringUtil.getLastColorCharBefore(entity.getName(), "Wither");
            if (color != '\0') {
                ((IWitherColor) entity).setmwe$Color(ColorUtil.getColorInt(color));
            }
        }
    }

}
