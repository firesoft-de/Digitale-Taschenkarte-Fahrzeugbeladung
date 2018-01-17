package dresden.de.digitaleTaschenkarteBeladung.util;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import dresden.de.digitaleTaschenkarteBeladung.MainActivity;
import dresden.de.digitaleTaschenkarteBeladung.viewmodels.DataFragViewModel;

/**
 * Mit dieser Hilfsklasse werden die Gruppen verwaltet
 */
public class GroupManager {

    private ArrayList<String>  subscribedGroups;
    private String activeGroup;
    private ArrayList<String> newGroups;

    private Activity parentActivity;


    public static final String PREFS_GROUPS="groups";
    public static final String PREFS_ACTIVE_GROUP="activegroup";
    public static final String NO_SUBSCRIBED_GROUPS="no-subscribed-groups";

    //=======================================================
    //=====================Konstruktoren=====================
    //=======================================================

    /**
     * Klasse initalisieren
     * @param activity Die übergeordnete Activity
     */
    public GroupManager(Activity activity) {
        subscribedGroups = new ArrayList<>();
        newGroups = new ArrayList<>();
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

        if (subscribedGroups.size() > 0) {
            SharedPreferences.Editor editor = parentActivity.getSharedPreferences(Util.PREFS_NAME, Context.MODE_PRIVATE).edit();
            StringBuilder saveString = new StringBuilder();

            for (String group : subscribedGroups
                    ) {
                saveString.append(group);
                saveString.append(";");
            }

            //Abschließendes ; entfernen
            saveString.deleteCharAt(saveString.length() - 1);
            //Speichern
            editor.putString(PREFS_GROUPS, saveString.toString());
            editor.apply();

            //Aktive Gruppe speichern
            editor = parentActivity.getSharedPreferences(Util.PREFS_NAME, Context.MODE_PRIVATE).edit();
            editor.putString(PREFS_ACTIVE_GROUP, activeGroup);

        }
    }

    //=======================================================
    //=====================GETTER/SETTER=====================
    //=======================================================

    public ArrayList<String> getSubscribedGroups() {
        return subscribedGroups;
    }

    public void setSubscribedGroups(ArrayList<String> groups) {
        subscribedGroups = new ArrayList<>();
        subscribedGroups.addAll(groups);
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


    //=======================================================
    //====================Arbeitsmethoden====================
    //=======================================================

    /**
     * Identifiziert Gruppen, welche in der neu ausgewählten Gruppenliste erstmalig auftauchen
     * @param selectedGroups Die neue Gruppenauswahl des Benutzers
     * @return Liste der neuen Gruppen
     */
    public void identifyNewGroups(ArrayList<String> selectedGroups) {
        ArrayList<String> list = new ArrayList<>();
        list.addAll(selectedGroups);
        for (String group: selectedGroups
             ) {
            if(subscribedGroups.contains(group)) {
                list.remove(group);
            }
        }
        newGroups = list;
    }

    /**
     * Identifiziert Gruppen, welche in der neu ausgewählten Gruppenliste nicht mehr enthalten sind
     * @param selectedGroups Die neue Gruppenauswahl des Benutzers
     * @return Liste der entfernten Gruppen
     */
    public void deleteRemovedGroups(ArrayList<String> selectedGroups, DataFragViewModel viewModel) {
        ArrayList<String> list = new ArrayList<>();
        for (String group: subscribedGroups
                ) {
            if(!selectedGroups.contains(group)) {
                list.add(group);
            }
        }
        //TODO: Items, Trays, Positionimage und die Bilder löschen

        if (list.size() > 0) {
            for (String group: list
                 ) {
                viewModel.deleteByGroup(group);
            }
        }
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

    /**
     * Die Methode erzeugt den Query für die gruppengestützte HTTP-Abfrage
     * @return
     */
    public String subscribedToQuery() {

        if (subscribedGroups.size() > 0) {

            StringBuilder stringBuilder = new StringBuilder();

            for (String group : subscribedGroups
                    ) {
                stringBuilder.append(group);
                stringBuilder.append("_");
            }

            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            return stringBuilder.toString();
        }
        else {
            return NO_SUBSCRIBED_GROUPS;
        }
    }

    /**
     * Die Methode erzeugt den Query für die gruppengestützte HTTP-Abfrage der neu hinzugekommenen Gruppen
     * @return
     */
    public String newToQuery() {

        if (newGroups.size() > 0) {

            StringBuilder stringBuilder = new StringBuilder();

            for (String group : newGroups
                    ) {
                stringBuilder.append(group);
                stringBuilder.append("_");
            }

            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            return stringBuilder.toString();
        }
        else {
            return NO_SUBSCRIBED_GROUPS;
        }

    }

    public void delete(Context context) {

        try {
            subscribedGroups.clear();
            newGroups.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Util.deletePref(context);

    }

    //=======================================================
    //===================Interne Methoden====================
    //=======================================================


}
