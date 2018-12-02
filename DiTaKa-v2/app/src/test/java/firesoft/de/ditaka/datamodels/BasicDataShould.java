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

public class BasicDataShould {

    private BasicData basicData;

    private String tags;
    private String delimiter = ";";
    private ArrayList<String> tagResult;
    private ArrayList<String> singleTagResult;

    @Before
    public void setUp() throws Exception {

        basicData = new BasicData();

        // Beispieltags erstellen
        tags = "Test;BaseData;Desktop;Computer;Beispiel";

        tagResult = new ArrayList<>();
        tagResult.add("Test");
        tagResult.add("BaseData");
        tagResult.add("Desktop");
        tagResult.add("Computer");
        tagResult.add("Beispiel");

        singleTagResult = new ArrayList<>();
        singleTagResult.add("singletag");

    }

    @Test
    public void setName() {

        basicData.setName("Test");
        assertEquals("Test", basicData.getName());

    }

    @Test
    public void setTagsFromString() {

        basicData.setTags(tags,delimiter);
        assertEquals(tagResult, basicData.tags);

    }

    @Test(expected = IllegalArgumentException.class)
    public void failSettingTagsFromNull() {

        basicData.setTags(null,",");

    }

    @Test
    public void dontFailSettingTagsFromEmpty() {

        basicData.setTags("",",");
        assertEquals(basicData.tags.size(),0);

    }

    @Test(expected = IllegalArgumentException.class)
    public void failSettingTagsFromString() {

        basicData.setTags(tags,",");

    }

    @Test(expected = IllegalArgumentException.class)
    public void throwErrorWhenNoTagsFound() {
        // Wenn aus String und Trennzeichen keine Tags erzeugt wurden, sollte eine Fehlermeldung erscheinen
        basicData.setTags(tags,",");

    }

    @Test
    public void handleTageGenerationWithOnlyOneTag() {
        // Wenn aus String und Trennzeichen keine Tags erzeugt wurden, sollte eine Fehlermeldung erscheinen
        basicData.setTags("singletag",null);
        assertEquals(basicData.tags,singleTagResult);
    }

    @Test
    public void handleEmptyTagsWhileSearching() {

        basicData.setTags("",delimiter);
        basicData.setName("Testname");
        basicData.setDescription("Das ist eine Beispielbeschreibung");

        boolean searchResult = basicData.search("Testname");
        assertTrue(searchResult);

    }

    @Test
    public void searchAndFindValidCandidatesInName() {

        basicData.setTags(tags,delimiter);
        basicData.setName("Testname");
        basicData.setDescription("Das ist eine Beispielbeschreibung");

        boolean searchResult = basicData.search("Testname");
        assertTrue(searchResult);

    }

    @Test
    public void searchAndFindValidPartialCandidatesInName() {

        basicData.setTags(tags,delimiter);
        basicData.setName("Testname");
        basicData.setDescription("Das ist eine Beispielbeschreibung");

        boolean searchResult = basicData.search("ame");
        assertTrue(searchResult);

    }

    @Test
    public void searchAndFindValidCandidatesInDescription() {

        basicData.setTags(tags,delimiter);
        basicData.setName("Testname");
        basicData.setDescription("Das ist eine Beispielbeschreibung");

        boolean searchResult = basicData.search("Beispielbeschreibung");
        assertTrue(searchResult);

    }

    @Test
    public void searchAndFindValidPartialCandidatesInDescription() {

        basicData.setTags(tags,delimiter);
        basicData.setName("Testname");
        basicData.setDescription("Das ist eine Beispielbeschreibung");

        boolean searchResult = basicData.search("ist eine Beispie");
        assertTrue(searchResult);

    }

    @Test
    public void searchAndFindValidCandidatesInTags() {

        basicData.setTags(tags,delimiter);
        basicData.setName("Testname");
        basicData.setDescription("Das ist eine Beispielbeschreibung");

        boolean searchResult = basicData.search("Computer");
        assertTrue(searchResult);
    }

    @Test
    public void searchAndFindValidPartialCandidatesInTags() {
        // Testet die Suchfunktion für Fragmente in Tags

        basicData.setTags(tags,delimiter);
        basicData.setName("Testname");
        basicData.setDescription("Das ist eine Beispielbeschreibung");

        boolean searchResult = basicData.search("puter");
        assertTrue(searchResult);
    }

    @Test
    public void searchAndNotFindInvalidCandidatesInTags() {

        basicData.setTags(tags,delimiter);
        basicData.setName("Testname");
        basicData.setDescription("Das ist eine Beispielbeschreibung");

        boolean searchResult = basicData.search("Kartoffel");
        assertFalse(searchResult);

    }


}