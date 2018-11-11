package dresden.de.digitaleTaschenkarteBeladung.util;

import android.arch.lifecycle.MutableLiveData;

/**
 * Der VariablenManager verwaltet Variablen die von verschiedenen Klassen benötigt werden.
 */
public class VariableManager {

    // Live-Variablen
    public MutableLiveData<Integer> liveNetDBVersion;
    public MutableLiveData<Util.Sort> liveSort;

    // Weitere Variablen
    public Util.DbState dbState;

    // Flags
    public boolean NetDBVersionCallForUser;
    public boolean FirstDownloadCompleted;
    public boolean CallFromNotification;

    public VariableManager() {
        reset();
    }

    public void reset() {
        // Live Variablen in den Ausganzustand bringen
        // Default Zustand -1 -> Keine Internetverbindung, noch keine Daten empfangen oder ein unbekannter Fehler ist aufgetreten!
        liveNetDBVersion = new MutableLiveData<>();
        liveNetDBVersion.setValue(-1);

        liveSort = new MutableLiveData<>();
        liveSort.setValue(Util.Sort.PRESET);

        dbState = Util.DbState.CLEAN;

        NetDBVersionCallForUser = false;
        FirstDownloadCompleted = false;
        CallFromNotification = false;
    }
}
