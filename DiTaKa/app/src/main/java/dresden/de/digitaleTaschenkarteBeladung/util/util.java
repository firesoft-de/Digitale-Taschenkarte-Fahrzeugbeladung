package dresden.de.digitaleTaschenkarteBeladung.util;


import android.util.Log;

import dresden.de.digitaleTaschenkarteBeladung.MainActivity;

public class util {

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
