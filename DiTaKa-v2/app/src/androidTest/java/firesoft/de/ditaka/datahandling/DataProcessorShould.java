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

package firesoft.de.ditaka.datahandling;

import android.content.Context;
import android.provider.ContactsContract;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.MediumTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertNotNull;
import android.arch.core.executor.testing.InstantTaskExecutorRule;

@MediumTest
@RunWith(JUnit4.class)
public class DataProcessorShould {

    private String validJsonString;
    private String invalidJsonString;
    private Context context;

    private DataProcessor processor;

    // A JUnit Test Rule that swaps the background executor used by the Architecture Components with a different one which executes each task synchronously. (Livedata)
    // https://medium.com/pxhouse/unit-testing-with-mutablelivedata-22b3283a7819
    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    @Before
    public void setUp() throws Exception {


        context = InstrumentationRegistry.getContext();

        // Teststring einrichten
        validJsonString = "{\n" +
                "\"Trays\": \n" +
                "\t[\n" +
                "\t\t{\n" +
                "\t\t\t\"id\":1,\n" +
                "\t\t\t\"name\":\"Fach 1\",\n" +
                "\t\t\t\"groupId\":1\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"id\":2,\n" +
                "\t\t\t\"name\":\"Fach 2\",\n" +
                "\t\t\t\"groupId\":1\n" +
                "\t\t}\n" +
                "\t]\n" +
                "}";

        invalidJsonString = "{\n" +
                "\"Trays\": \n" +
                "\t[\n" +
                "\t\t{\n" +
                "\t\t\t\"id\":1,\n" +
                "\t\t\t\"name\":\"Fach 1\",\n" +
                "\t\t\t\"groupId\":1\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"id\":2,\n" +
                "\t\t\t\"name\":\"Fach 2\",\n" +
                "\t\t\t\"groupId:1\n" + // Hier fehlt ein " hinter groupId
                "\t\t}\n" +
                "\t]\n" +
                "}";

    }

    @Test
    public void instantiateWithValidJSON() {

        processor = new DataProcessor(context,validJsonString);

    }

    // GSON kann nicht zur Erkennung von fehlerhaften JSON's verwendet werden.

//    @Test(expected = Exception.class)
//    public void failWithInvalidJson() {
//
//        processor = new DataProcessor(context,invalidJsonString);
//
//    }

}