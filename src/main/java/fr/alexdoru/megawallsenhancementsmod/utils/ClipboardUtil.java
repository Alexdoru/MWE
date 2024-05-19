package fr.alexdoru.megawallsenhancementsmod.utils;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

public class ClipboardUtil {

    public static void copyString(String msg) {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(msg), null);
    }

}
