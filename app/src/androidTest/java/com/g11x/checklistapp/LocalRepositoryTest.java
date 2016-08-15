package com.g11x.checklistapp;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ProviderTestCase2;

import org.junit.Test;
import org.junit.runner.RunWith;

/** Tests for the app's ContentProvider.*/
@RunWith(AndroidJUnit4.class)
public class LocalRepositoryTest extends ProviderTestCase2<LocalRepository> {
    public LocalRepositoryTest() {
        super(LocalRepository.class, Constants.LOCAL_REPOSITORY_AUTHORITY);
    }

    @Override
    protected void setUp() throws Exception {
        setContext(InstrumentationRegistry.getTargetContext());
        super.setUp();
    }

    @Test
    public void shouldFetchData() {}
}
