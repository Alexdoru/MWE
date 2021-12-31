package fr.alexdoru.megawallsenhancementsmod.api.cache;

import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import fr.alexdoru.megawallsenhancementsmod.api.requests.MojangPlayernameToUUID;

public class CachedMojangUUID {

    private static String Playername;
    private static String uuid;

    public CachedMojangUUID(String playernameIn) throws ApiException {

        if (Playername != null && Playername.equalsIgnoreCase(playernameIn)) // don't send a request again if it is the same player as before
            return;

        MojangPlayernameToUUID mojangPlayernameToUUID = new MojangPlayernameToUUID(playernameIn);

        Playername = mojangPlayernameToUUID.getName();
        uuid = mojangPlayernameToUUID.getUuid();

    }

    public String getName() {
        return Playername;
    }

    public String getUuid() {
        return uuid;
    }

}
