/*  Diese App stellt die Beladung von BOS Fahrzeugen in digitaler Form dar.
    Copyright (C) 2017  David Schlossarczyk

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    For the full license visit https://www.gnu.org/licenses/gpl-3.0.*/

package dresden.de.digitaleTaschenkarteBeladung.util;

import java.util.ArrayList;

import dresden.de.digitaleTaschenkarteBeladung.data.Group;
import dresden.de.digitaleTaschenkarteBeladung.viewmodels.DataFragViewModel;

/**
 * Mit dieser Hilfsklasse werden die Gruppen verwaltet
 */
public class GroupManager {

    // Die groups Liste wird zum App-Start mit den in den PREFS gespeicherten, abonnierten Gruppen gefüttert.
    // Sie dient dazu die bereits abonnierte Gruppen zu identifizieren
    private ArrayList<Group> groups;

    // In der newGroups Liste werden die in der identifyNewGroups-Methode erkannten neu hinzugekommenden Gruppen gespeichert.
    // Sie wird dann dazu verwendet den benötigten Query zum vollständigen Download zu erzeugen.
    private ArrayList<Group> newGroups;

    // Die availableGroups Liste enthält alle Gruppen welche dem Benutzer zur Auswahl gestellt werden. Die Liste wird benötigt,
    // da die newGroups Liste erst nach der Benutzerauswahl mit Einträgen gefüllt ist. Um die Gruppenverwaltung zu ermöglichen,
    // wird aber schon vor der Benutzerauswahl eine vollständige Liste der verfügbaren Gruppen benötigt (DataImportFragment - buttonAddClick()
    private ArrayList<Group> availableGroups;


    public static final String NO_SUBSCRIBED_GROUPS="no-subscribed-groups";

    //=======================================================
    //=====================Konstruktoren=====================
    //=======================================================

    /**
     * Klasse initalisieren
     */
    public GroupManager() {
        groups = new ArrayList<>();
        newGroups = new ArrayList<>();

        availableGroups = new ArrayList<>();
    }

    //=======================================================
    //=====================GETTER/SETTER=====================
    //=======================================================

    public ArrayList<Group> getSubscribedGroups() {
        ArrayList<Group> result = new ArrayList<>();

        for (Group group: groups)
        {
            if (group.isSubscribed()) {
                result.add(group);
            }
        }

        return result;
    }

    public void add(Group group) {
        groups.add(group);
    }

    public void addToTmpList(ArrayList<Group> group) {
        availableGroups = group;
    }


    public void setSubscribedGroups(ArrayList<Group> input) {

        for (Group group: groups)
        {
            if (input.contains(group)) {
                group.setSubscribed(true);
            }
        }

            for (Group group: availableGroups)
            {
                if (input.contains(group)) {
                    group.setSubscribed(true);
                }
            }
    }

/*    public void setSubscribedGroupsByName(ArrayList<String> input) {

        for (Group group: groups)
        {
            if (input.contains(group.getName())) {
                group.setSubscribed(true);
            }
        }
    }*/

    /**
     * Gibt die aktive Gruppe aus
     * @return aktive Gruppe als Group-Objekt
     */
    public Group getActiveGroup() {

        for (Group group: groups)
        {
            if (group.isActive()) {
                return group;
            }
        }
        Group group = new Group(-1,"",false);
        group.setTrayname("default");

        return group;
    }

    /**
     * Setzt die anzuzeigende (aktive) Gruppe
     * @param activeGroup Zu setzende Gruppe, falls der Parameter null ist, wird das erste Element der Liste genommen
     */
    public void setActiveGroup(String activeGroup) {

        if (activeGroup.equals("")) {
            if (groups.size() > 0) {
                groups.get(0).setActive(true);
            }
        }
        else {
            for (Group group: groups)
            {
                if (group.getName().equals(activeGroup)) {
                    group.setActive(true);
                }
                else {
                    group.setActive(false);
                }
            }
        }
    }

    /**
     * Setzt die aktive Gruppe durch das Gruppenobjekt
     * @param group die zu setztende Gruppe als Group-Objekt
     */
    public void setActiveGroup(Group group) {
        setActiveGroup(group.getName());
    }

    /**
     * Liefert die Anzahl der abonnierten Gruppen
     * @return die Anzahl der abonnierten Gruppen
     */
    public int getSubscribedGroupsCount() {
        int count = 0;

        for (Group group: groups)
        {
            if (group.isSubscribed()) {
                count += 1;
            }
        }
        return count;
    }

    /**
     * Findet Einträge aus der Gruppenliste anhand ihres Namens
     * @param names Namen der gesuchten Gruppen
     * @return Liste der gefundenen Gruppen
     */
    public ArrayList<Group> getGroupsByName(ArrayList<String> names) {
        ArrayList<Group> list = new ArrayList<>();

        for (Group group: groups)
        {
            if (names.contains(group.getName())) {
                list.add(group);
            }
        }

        for (Group group: availableGroups)
        {
            if (names.contains(group.getName()) && !list.contains(group)) {
                list.add(group);
            }
        }

        return list;
    }

    //=======================================================
    //====================Arbeitsmethoden====================
    //=======================================================

    /**
     * Identifiziert Gruppen, welche in der neu ausgewählten Gruppenliste erstmalig auftauchen
     * @param selectedGroups Die neue Gruppenauswahl des Benutzers
     */
    public void identifyNewGroups(ArrayList<Group> selectedGroups) {
        ArrayList<Group> list = new ArrayList<>();
        list.addAll(selectedGroups);
        for (Group group: selectedGroups
             ) {
            if(groups.contains(group)) {
                list.remove(group);
            }
        }
        newGroups = list;
    }

    /**
     * Identifiziert Gruppen, welche in der neu ausgewählten Gruppenliste nicht mehr enthalten sind
     * @param selectedGroups Die neue Gruppenauswahl des Benutzers
     */
    public void identifyRemovedGroups(ArrayList<Group> selectedGroups, DataFragViewModel viewModel) {
        ArrayList<Group> list = new ArrayList<>();
        for (Group group: groups
                ) {
            if(!selectedGroups.contains(group)) {
                list.add(group);
            }
        }

        if (list.size() > 0) {
            for (Group group: list
                 ) {
                viewModel.deleteByGroup(group.getName());
                groups.remove(group);
            }
        }
    }

    public void moveNewGroupsToMainList() {
        groups.addAll(newGroups);
    }

    /**
     * Kombiniert die abonnierten Gruppen mit den eingegebenen Gruppen
     * @param newg Neue Gruppen welche in die Liste aufgenommen werden sollen
     * @return Liste mit den Einträgen der abonnierten Gruppen Liste und der eingegebenen Liste. Jeder Eintrag kommt nur einmal vor.
     */
    public ArrayList<Group> mergeNewGroupList(ArrayList<Group> newg) {

        ArrayList<Group> result = new ArrayList<>();
        result.addAll(groups);

        for (int i = 0; i < newg.size(); i++) {
            Group newgroup = newg.get(i);
            boolean existing = false;

            for (int d = 0; d < groups.size(); d++) {
                Group group = groups.get(d);

                if (newgroup.getName().equals(group.getName())) {
                    existing = true;
                    d = groups.size();
                }
            }

            if (!existing) {
                result.add(newgroup);
            }
        }
        return result;
    }

//    /**
//     * Prüft ob ein String in der Liste der abonnierten Gruppen enthalten ist
//     * @param groupCandidate Der zu prüfende Kandidate
//     * @return True = ist enthalten, False = ist nicht enthalten
//     */
//    public boolean contains(Group groupCandidate) {
//        return groups.contains(groupCandidate);
//    }

    public int getActiveGroupIndex() {

        if (groups.size() > 0) {
            for (Group group: groups)
            {
                if (group.isActive()) {
                    return groups.indexOf(group);
                }
            }
        }
        return 0;
    }

    /**
     * Die Methode erzeugt den Query für die gruppengestützte HTTP-Abfrage der bereits abonnierten Gruppen
     * @return Der Query zum Abruf der bereits abonnierten Gruppen
     */
    public String subscribedToQuery() {

        if (groups.size() > 0) {

            StringBuilder stringBuilder = new StringBuilder();

            for (Group group : groups
                    ) {
                stringBuilder.append(group.getName());
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
     * @return der Query zum Abruf der neuen Gruppen
     */
    public String newGroupsQuery() {

        if (newGroups.size() > 0) {

            StringBuilder stringBuilder = new StringBuilder();

            for (Group group : newGroups
                    ) {
                stringBuilder.append(group.getName());
                stringBuilder.append("_");
            }

            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            return stringBuilder.toString();
        }
        else {
            return NO_SUBSCRIBED_GROUPS;
        }

    }

    public void clear() {

        try {
            groups.clear();
            newGroups.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
