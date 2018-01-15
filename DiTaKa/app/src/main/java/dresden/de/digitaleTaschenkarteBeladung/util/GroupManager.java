package dresden.de.digitaleTaschenkarteBeladung.util;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Mit dieser Hilfsklasse werden die Gruppen verwaltet
 */
public class GroupManager {

    private ArrayList<String>  subscribedGroups;
    private String activeGroup;

    private Activity parentActivity;


    public static final String PREFS_GROUPS="dresden.de.digitaleTaschenkarteBeladung.groups";
    public static final String PREFS_ACTIVE_GROUP="dresden.de.digitaleTaschenkarteBeladung.activegroup";

    //=======================================================
    //=====================Konstruktoren=====================
    //=======================================================

    /**
     * Klasse initalisieren
     * @param activity Die übergeordnete Activity
     */
    public GroupManager(Activity activity) {
        this.subscribedGroups = new ArrayList<>();
        activeGroup = "";
        parentActivity = activity;
    }

    //=======================================================
    //====================Daten-Methoden=====================
    //=======================================================

    /**
     * Lädt die abonnierten Gruppen aus den PREFS.
     */
    public void loadGroupsFromPref() {

        //Abonnierte Gruppen laden
        String saveString = parentActivity.getSharedPreferences(Util.PREFS_NAME,Context.MODE_PRIVATE).getString(PREFS_GROUPS,"");
        subscribedGroups = new ArrayList<>();

        if (!saveString.equals("")) {
            String[] array = saveString.split(";");
            subscribedGroups.addAll(Arrays.asList(array));
        }

        //Aktive Gruppe laden
        activeGroup = parentActivity.getSharedPreferences(Util.PREFS_NAME,Context.MODE_PRIVATE).getString(PREFS_ACTIVE_GROUP,"");

    }

    /**
     * Speichert die abonnierten Gruppen in den PREFS
     */
    public void saveGroupsToPref() {
        //Abonnierte Gruppe speichern
        SharedPreferences.Editor editor = parentActivity.getSharedPreferences(Util.PREFS_NAME, Context.MODE_PRIVATE).edit();
        StringBuilder saveString = new StringBuilder();

        for (String group: subscribedGroups
                ) {
            saveString.append(group);
            saveString.append(";");
        }

        //Abschließendes ; entfernen
        saveString.deleteCharAt(saveString.length() - 1);
        //Speichern
        editor.putString(PREFS_GROUPS,saveString.toString());
        editor.apply();

        //Aktive Gruppe speichern
        editor = parentActivity.getSharedPreferences(Util.PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(PREFS_ACTIVE_GROUP, activeGroup);
        editor.apply();
    }

    //=======================================================
    //====================Arbeitsmethoden====================
    //=======================================================

    /**
     * Identifiziert Gruppen, welche in der neu ausgewählten Gruppenliste erstmalig auftauchen
     * @param selectedGroups Die neue Gruppenauswahl des Benutzers
     * @return Liste der neuen Gruppen
     */
    public ArrayList<String> identifyNewGroups(ArrayList<String> selectedGroups) {
        for (String group: selectedGroups
             ) {
            if(subscribedGroups.contains(group)) {
                selectedGroups.remove(group);
            }
        }
        return selectedGroups;
    }

    /**
     * Identifiziert Gruppen, welche in der neu ausgewählten Gruppenliste nicht mehr enthalten sind
     * @param selectedGroups Die neue Gruppenauswahl des Benutzers
     * @return Liste der entfernten Gruppen
     */
    public ArrayList<String> identifyRemovedGroups(ArrayList<String> selectedGroups) {
        ArrayList<String> list = subscribedGroups;
        for (String group: selectedGroups
                ) {
            if(!subscribedGroups.contains(group)) {
                list.add(group);
            }
        }
        return list;
    }

    /**
     * Prüft ob ein String in der Liste der abonnierten Gruppen enthalten ist
     * @param groupCandidate Der zu prüfende Kandidate
     * @return True = ist enthalten, False = ist nicht enthalten
     */
    public boolean contains(String groupCandidate) {
        return subscribedGroups.contains(groupCandidate);
    }

    public int getActiveGroupIndex() {

        if (subscribedGroups.size() > 0 && subscribedGroups.contains(activeGroup)) {
            return subscribedGroups.indexOf(activeGroup);
        }
        else {
            return 0;
        }
    }

    //=======================================================
    //=====================GETTER/SETTER=====================
    //=======================================================

    public ArrayList<String> getSubscribedGroups() {
        return subscribedGroups;
    }

    public void setSubscribedGroups(ArrayList<String> groups) {
        subscribedGroups = groups;
    }

    /**
     * Gibt die aktive Gruppe aus
     * @return
     */
    public String getActiveGroup() {
        if (activeGroup.equals("")) {
            if (subscribedGroups.size() > 0) {
                return subscribedGroups.get(0);
            }
            else {
                return "";
            }
        }
        else {
            return activeGroup;
        }
    }

    /**
     * Setzt die angezeigte (aktive) Gruppe
     * @param activeGroup Zu setzende Gruppe, falls der Parameter null ist, wird das erste Element der Liste genommen
     */
    public void setActiveGroup(@Nullable String activeGroup) {
        if (activeGroup == null) {
            if (subscribedGroups.size() > 0) {
                this.activeGroup = subscribedGroups.get(0);
            }
        }
        else {
            this.activeGroup = activeGroup;
        }
    }

    /**
     * Setzt die angezeigte (aktive) Gruppe
     * @param index Index der zu setzende Gruppe, falls der Parameter null ist, wird das erste Element der Liste genommen
     */
    public void setActiveGroup(@Nullable int index) {
        if (activeGroup == null) {
            if (subscribedGroups.size() > 0) {
                this.activeGroup = subscribedGroups.get(0);
            }
        }
        else {
            if (subscribedGroups.size() > 0) {
                this.activeGroup = subscribedGroups.get(index);
            }
        }
    }

    /**
     * Liefert die Anzahl der abonnierten Gruppen
     * @return
     */
    public int getSubscribedGroupsCount() {
        return subscribedGroups.size();
    }

}
