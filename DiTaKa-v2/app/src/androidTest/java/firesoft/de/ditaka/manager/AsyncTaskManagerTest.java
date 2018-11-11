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

package firesoft.de.ditaka.manager;

import android.content.Context;
import android.os.Bundle;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.MediumTest;
import android.support.v4.content.Loader;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import firesoft.de.libfirenet.http.HttpLoader;
import firesoft.de.libfirenet.method.GET;
import firesoft.de.libfirenet.util.ResultWrapper;

import static firesoft.de.ditaka.util.Definitions.HTTP_LOADER;
import static org.junit.Assert.*;


@MediumTest
@RunWith(JUnit4.class)
public class AsyncTaskManagerTest {

    private AsyncTaskManager manager;
    private Context context;

    @Before
    public void setUp() throws Exception {

        context = InstrumentationRegistry.getContext();
        manager = new AsyncTaskManager(context);

    }

    @Test
    public void createAHTTPLoader() {

        Bundle bundle = new Bundle();
        bundle.putString("url","https://test.de");

        Loader<ResultWrapper> loader = manager.onCreateLoader(HTTP_LOADER,bundle);

        assertNotNull(loader);

    }

    @Test(expected = IllegalArgumentException.class)
    public void throwAnErrorWithoutMatchingLoaderID() {

        Bundle bundle = new Bundle();
        bundle.putString("url","https://test.de");

        Loader<ResultWrapper> loader = manager.onCreateLoader(-1,bundle);

    }

    @Test(expected = IllegalArgumentException.class)
    public void thowAnErrorWithMissingURL() {

        Bundle bundle = new Bundle();
        bundle.putString("wrongKey","https://test.de");

        Loader<ResultWrapper> loader = manager.onCreateLoader(HTTP_LOADER,bundle);

    }

    @Test(expected = IllegalArgumentException.class)
    public void thowAnErrorWithArgumentsEqualsNull() {

        Loader<ResultWrapper> loader = manager.onCreateLoader(HTTP_LOADER,null);

    }

    @Test(expected = IllegalArgumentException.class)
    public void thowAnErrorWhenArgumentIsNumber() {

        Bundle bundle = new Bundle();
        bundle.putByte("url", (byte) 100);

        Loader<ResultWrapper> loader = manager.onCreateLoader(HTTP_LOADER,null);

    }

}