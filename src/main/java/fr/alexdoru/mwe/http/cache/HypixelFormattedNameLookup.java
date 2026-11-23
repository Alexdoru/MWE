package fr.alexdoru.mwe.http.cache;

import fr.alexdoru.mwe.api.IPlayerUUID;
import fr.alexdoru.mwe.http.apikey.HypixelApiKeyUtil;
import fr.alexdoru.mwe.http.exceptions.ApiException;
import fr.alexdoru.mwe.http.parsers.hypixel.LoginData;

public final class HypixelFormattedNameLookup {

    private HypixelFormattedNameLookup() {}

    /**
     * Blocking network call, don't use on the main thread.
     *
     * @return the player's Hypixel formatted name, or null if it can't be fetched
     */
    public static String fetchFormattedName(IPlayerUUID playerID) {
        if (HypixelApiKeyUtil.apiKeyIsNotSetup()) {
            return null;
        }
        try {
            final LoginData loginData = new LoginData(CachedHypixelPlayerData.getPlayerData(playerID.getId()));
            if (!loginData.hasNeverJoinedHypixel() && playerID.getName().equals(loginData.getdisplayname())) {
                return loginData.getFormattedName();
            }
        } catch (ApiException ignored) {}
        return null;
    }

}
