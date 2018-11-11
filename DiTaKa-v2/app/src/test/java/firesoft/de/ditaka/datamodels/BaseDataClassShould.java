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

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class BaseDataClassShould {

    private BaseDataClass baseDataClass;

    private String tags;
    private String delimiter = ";";
    private ArrayList<String> tagResult;

    @Before
    public void setUp() throws Exception {

        baseDataClass = new BaseDataClass();

        // Beispieltags erstellen
        tags = "Test;BaseData;Desktop;Computer;Beispiel";

        tagResult = new ArrayList<>();
        tagResult.add("Test");
        tagResult.add("BaseData");
        tagResult.add("Desktop");
        tagResult.add("Computer");
        tagResult.add("Beispiel");

    }

    @Test
    public void setName() {

        baseDataClass.setName("Test");
        assertEquals("Test",baseDataClass.getName());

    }

    @Test
    public void setTagsFromString() {

        baseDataClass.setTags(tags,delimiter);
        assertEquals(tagResult,baseDataClass.tags);

    }

    @Test(expected = IllegalArgumentException.class)
    public void throwErrorWhenNoTagsFound() {
        // Wenn aus String und Trennzeichen keine Tags erzeugt wurden, sollte eine Fehlermeldung erscheinen
        baseDataClass.setTags(tags,",");

    }

    @Test
    public void searchAndFindValidCandidatesInName() {

        baseDataClass.setTags(tags,delimiter);
        baseDataClass.setName("Testname");
        baseDataClass.setDescription("Das ist eine Beispielbeschreibung");

        boolean searchResult = baseDataClass.search("Testname");
        assertTrue(searchResult);

    }

    @Test
    public void searchAndFindValidPartialCandidatesInName() {

        baseDataClass.setTags(tags,delimiter);
        baseDataClass.setName("Testname");
        baseDataClass.setDescription("Das ist eine Beispielbeschreibung");

        boolean searchResult = baseDataClass.search("ame");
        assertTrue(searchResult);

    }

    @Test
    public void searchAndFindValidCandidatesInDescription() {

        baseDataClass.setTags(tags,delimiter);
        baseDataClass.setName("Testname");
        baseDataClass.setDescription("Das ist eine Beispielbeschreibung");

        boolean searchResult = baseDataClass.search("Beispielbeschreibung");
        assertTrue(searchResult);

    }

    @Test
    public void searchAndFindValidPartialCandidatesInDescription() {

        baseDataClass.setTags(tags,delimiter);
        baseDataClass.setName("Testname");
        baseDataClass.setDescription("Das ist eine Beispielbeschreibung");

        boolean searchResult = baseDataClass.search("ist eine Beispie");
        assertTrue(searchResult);

    }

    @Test
    public void searchAndFindValidCandidatesInTags() {

        baseDataClass.setTags(tags,delimiter);
        baseDataClass.setName("Testname");
        baseDataClass.setDescription("Das ist eine Beispielbeschreibung");

        boolean searchResult = baseDataClass.search("Computer");
        assertTrue(searchResult);
    }

    @Test
    public void searchAndFindValidPartialCandidatesInTags() {
        // Testet die Suchfunktion für Fragmente in Tags

        baseDataClass.setTags(tags,delimiter);
        baseDataClass.setName("Testname");
        baseDataClass.setDescription("Das ist eine Beispielbeschreibung");

        boolean searchResult = baseDataClass.search("puter");
        assertTrue(searchResult);
    }

    @Test
    public void searchAndNotFindInvalidCandidatesInTags() {

        baseDataClass.setTags(tags,delimiter);
        baseDataClass.setName("Testname");
        baseDataClass.setDescription("Das ist eine Beispielbeschreibung");

        boolean searchResult = baseDataClass.search("Kartoffel");
        assertFalse(searchResult);

    }


}