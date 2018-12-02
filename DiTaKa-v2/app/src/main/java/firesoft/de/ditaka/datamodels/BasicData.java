/*
 *     Diese App stellt die Beladung von BOS Fahrzeugen in digitaler Form dar.
 *     Copyright (C) 2018  David Schlossarczyk
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     For the full license visit https://www.gnu.org/licenses/gpl-3.0.
 */

package firesoft.de.ditaka.datamodels;

import android.graphics.Bitmap;
import android.support.annotation.VisibleForTesting;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import static android.support.annotation.VisibleForTesting.PROTECTED;

/**
 * Definiert grundlegende Datenstrukturen die in allen Datenklassen vorhanden sind.
 */
public class BasicData {

    // region Variablen

    protected int id;
    protected String name;
    protected String description;
    protected Bitmap image;
    protected ArrayList<String> tags;

    // endregion

    // region Funktionen und Methoden

    /**
     * Wandelt einen String (bspw. "tag1;tag2;tag3;tag4") anhand eines Trennzeichens in eine String-ArrayList. Eventuell vorhandene Daten werden überschrieben!
     * @param content Inhalt der umgewandelt werden soll (bspw. "tag1;tag2;tag3;tag4")
     * @param delimiter Trennzeichen (bspw. ";"). Wenn null gesetzt wird davon ausgegangen, dass nur ein Tag vorhanden ist. -> Es findet keine Trennung statt
     */
    @VisibleForTesting(otherwise = PROTECTED)
    void setTags(String content, String delimiter) {

        // Leere Tag-Liste erstellen
        tags = new ArrayList<>();

        // Prüfen, ob ein Trennzeichen übergeben wurde
        if (delimiter == null || delimiter.equals("")) {

            // ggf. ein Single-Tag aus dem übergebenenen content erstellen
            if (content != null || !content.equals("")) {
                tags.add(content);
            }
            return;
        }

        // Prüfen, ob content übergebene wurde
        if (content == null) {
            // Fehler erzeugen
            throw new IllegalArgumentException("String representing the objects search tags was null.");
        }
        else if (content.equals("")) {
            // Wenn der content leer ist, ist das nicht schön, wird aber akzeptiert.
        }
        else {

            if (!content.contains(delimiter)) {
                throw new IllegalArgumentException("Couldn't generate Tags from given String and Delimiter.");
            }

            tags = new ArrayList<>(Arrays.asList(content.split(delimiter)));
        }
    }

    /**
     * Durchsucht die zum Objekt zugehörigen Tags sowie den Namen des Objekts nach einem Stichwort
     * @param candidate zu suchendes Stichwort
     * @return True = Objekt besitzt ein Tag mit passendem Tag oder Namen
     */
    @VisibleForTesting(otherwise = PROTECTED)
    boolean search(String candidate) {
        // TODO: Eventuell bietet es sich für die Tags an eine sortierte Liste zu verwenden und dann mittels Bordmitteln eine Binary Search durchzuführen. Das könnte u.U. schneller und resourcenschonender sein.
        // siehe https://stackoverflow.com/a/559113

        // Namen durchsuchen
        if (name.equals(candidate) || name.contains(candidate) || candidate.contains(name)) {
            return true;
        }

        // Beschreibung durchsuchen
        if (description.equals(candidate) || description.contains(candidate) || candidate.contains(description)) {
            return true;
        }

        // Tags durchsuchen
        for (String tag: tags
             ) {

            if (tag.equals(candidate) || tag.contains(candidate) || candidate.contains(tag)) {
                return true;
            }
        }

        return false;
    }


    // endregion

    // region Getter / Setter für Variablen

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }


    // endregion
}
