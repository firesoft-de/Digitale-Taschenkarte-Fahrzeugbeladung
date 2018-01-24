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

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import dresden.de.digitaleTaschenkarteBeladung.MainActivity;
import dresden.de.digitaleTaschenkarteBeladung.data.Group;
import dresden.de.digitaleTaschenkarteBeladung.viewmodels.DataFragViewModel;

/**
 * Mit dieser Hilfsklasse werden die Gruppen verwaltet
 */
public class GroupManager {

    private ArrayList<Group> groups;
    private ArrayList<Group> newGroups;

    private ArrayList<Group> tmpList;
//    private ArrayList<String>  subscribedGroups;
//    private String activeGroup;
//    private ArrayList<String> newGroups;

    private Activity parentActivity;


    public static final String NO_SUBSCRIBED_GROUPS="no-subscribed-groups";

    //=======================================================
    //=====================Konstruktoren=====================
    //=======================================================

    /**
     * Klasse initalisieren
     * @param activity Die übergeordnete Activity
     */
    public GroupManager(Activity activity) {
        groups = new ArrayList<>();
        newGroups = new ArrayList<>();
        parentActivity = activity;

        tmpList = new ArrayList<>();

//        activeGroup = "";
//        subscribedGroups = new ArrayList<>();
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

    public void addToTmpList(ArrayList<Group> group) {
        tmpList = group;
    }

    public void setSubscribedGroups(ArrayList<Group> input, boolean includeTmpList) {

        for (Group group: groups)
        {
            if (input.contains(group)) {
                group.setSubscribed(true);
            }
        }

        if (includeTmpList) {
            for (Group group: tmpList)
            {
                if (input.contains(group)) {
                    group.setSubscribed(true);
                }
            }

        }

    }

    public void setSubscribedGroupsByName(ArrayList<String> input) {

        for (Group group: groups)
        {
            if (input.contains(group.getName())) {
                group.setSubscribed(true);
            }
        }
    }

    /**
     * Gibt die aktive Gruppe aus
     * @return
     */
    public Group getActiveGroup() {

        for (Group group: groups)
        {
            if (group.isActive()) {
                return group;
            }
        }
        return null;
    }

    /**
     * Setzt die angezeigte (aktive) Gruppe
     * @param activeGroup Zu setzende Gruppe, falls der Parameter null ist, wird das erste Element der Liste genommen
     */
    public void setActiveGroup(String activeGroup) {

        if (activeGroup == "") {
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

    public void setActiveGroup(Group group) {
        setActiveGroup(group.getName());
    }

    public void moveNewGroupsToMainList() {
        groups.addAll(newGroups);
    }

    /**
     * Liefert die Anzahl der abonnierten Gruppen
     * @return
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

    public ArrayList<Group> getGroupsByName(ArrayList<String> names, boolean includeTmpList) {
        ArrayList<Group> list = new ArrayList<>();

        for (Group group: groups)
        {
            if (names.contains(group.getName())) {
                list.add(group);
            }
        }

        if (includeTmpList) {
            for (Group group: tmpList)
            {
                if (names.contains(group.getName()) && !list.contains(group)) {
                    list.add(group);
                }
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
     * @return Liste der neuen Gruppen
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
     * @return Liste der entfernten Gruppen
     */
    public void deleteRemovedGroups(ArrayList<Group> selectedGroups, DataFragViewModel viewModel) {
        ArrayList<Group> list = new ArrayList<>();
        for (Group group: groups
                ) {
            if(!selectedGroups.contains(group)) {
                list.add(group);
            }
        }
        //TODO: Items, Trays, Positionimage und die Bilder löschen

        if (list.size() > 0) {
            for (Group group: list
                 ) {
                viewModel.deleteByGroup(group.getName());
                groups.remove(group);
            }
        }
    }

    /**
     * Prüft ob ein String in der Liste der abonnierten Gruppen enthalten ist
     * @param groupCandidate Der zu prüfende Kandidate
     * @return True = ist enthalten, False = ist nicht enthalten
     */
    public boolean contains(Group groupCandidate) {
        return groups.contains(groupCandidate);
    }

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
     * Die Methode erzeugt den Query für die gruppengestützte HTTP-Abfrage
     * @return
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
     * @return
     */
    public String newToQuery() {

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

    public void delete() {

        try {
            groups.clear();
            newGroups.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public ArrayList<Group> getGroups() {
        return groups;
    }
}
