package dresden.de.digitaleTaschenkarteBeladung.util;


import android.util.Log;

import dresden.de.digitaleTaschenkarteBeladung.MainActivity;

public class Util {

    //Konstanten für die Fragmenterkennung
//    public static final String FRAGMENT_MAIN = "100";
    public static final String FRAGMENT_DATA  = "101";
    public static final String FRAGMENT_LIST_TRAY = "102";
    public static final String FRAGMENT_LIST_ITEM = "103";
    public static final String FRAGMENT_DETAIL = "104";
    public static final String FRAGMENT_DEBUG = "105";
    public static final String FRAGMENT_SETTINGS = "106";
    public static final String FRAGMENT_ABOUT = "107";

    public static final String ARGS_URL = "ARGS_URL";
    public static final String ARGS_VERSION = "ARGS_VERSION";
    public static final String ARGS_DBSTATE = "ARGS_DBSTATE";
    public static final String ARGS_CALLFORUSER = "ARGS_CALLFORUSER";

    public static final String PREFS_NAME="dresden.de.digitaleTaschenkarteBeladung";
    public static final String PREFS_URL="dresden.de.digitaleTaschenkarteBeladung.url";
    public static final String PREFS_DBVERSION="dresden.de.digitaleTaschenkarteBeladung.dbversion";

    public static void LogDebug(String tag, String message) {
        if (MainActivity.DEBUG_ENABLED) {
            Log.d(tag, message);
        }
    }

    public static void LogError(String tag, String message) {
        if (MainActivity.DEBUG_ENABLED) {
            Log.e(tag,message);
        }
    }
}
