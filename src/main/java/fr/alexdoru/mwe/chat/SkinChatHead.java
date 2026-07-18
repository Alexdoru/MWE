package fr.alexdoru.mwe.chat;

import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.util.ResourceLocation;

public class SkinChatHead {

    private ResourceLocation skin;

    public SkinChatHead() {
        this.skin = DefaultPlayerSkin.getDefaultSkinLegacy();
    }

    public SkinChatHead(ResourceLocation skin) {
        this.skin = skin;
    }

    public ResourceLocation getSkin() {
        return skin;
    }

    public void setSkin(ResourceLocation skin) {
        this.skin = skin;
    }

}
