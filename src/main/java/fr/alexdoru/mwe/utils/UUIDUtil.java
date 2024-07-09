package fr.alexdoru.mwe.utils;

import java.util.UUID;
import java.util.regex.Pattern;

public class UUIDUtil {

    private static final Pattern UUID_PATTERN = Pattern.compile("[0-9a-z]{8}-?[0-9a-z]{4}-?[0-9a-z]{4}-?[0-9a-z]{4}-?[0-9a-z]{12}");

    public static UUID fromString(String uuid) {
        if (uuid == null) return null;
        if (!UUID_PATTERN.matcher(uuid).matches()) return null;
        if (uuid.length() == 32) {
            final StringBuilder sb = new StringBuilder(uuid);
            sb.insert(8 + 4 + 4 + 4, '-');
            sb.insert(8 + 4 + 4, '-');
            sb.insert(8 + 4, '-');
            sb.insert(8, '-');
            return UUID.fromString(sb.toString());
        }
        if (uuid.length() == 36) {
            return UUID.fromString(uuid);
        }
        return null;
    }

}
