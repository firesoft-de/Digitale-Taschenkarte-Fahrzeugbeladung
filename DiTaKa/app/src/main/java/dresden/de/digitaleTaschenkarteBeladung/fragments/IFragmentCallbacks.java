package dresden.de.digitaleTaschenkarteBeladung.fragments;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

/**
 * Enthält die Definition für Methoden, welche aus den Fragments aufgerufen werden
 */
public interface IFragmentCallbacks {

    /**
     * Wechselt das angezeigte Fragment
     * @param id Kennung des Views in dem der Wechsel stattfindet
     * @param fragment Fragment das angezeigt werden soll (kann null sein)
     * @param tag Tag des anzuzeigenden Fragments
     */
    void switchFragment(int id, Fragment fragment, String tag);

    /**
     * Ermittelt die Versionsnummer der Online-Datenbank
     * @param callForUser Soll dem Nutzer eine Nachricht ausgegeben werden?
     */
    void getNetDBState(boolean callForUser);

    void manageActionBar(String tag);

    void invalOptionsMenu();

}
