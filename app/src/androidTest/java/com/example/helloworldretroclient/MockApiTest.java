package com.example.helloworldretroclient;

import androidx.test.espresso.IdlingRegistry;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

/**
 * Basic UI testing without relying on mocks
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MockApiTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    private EspressoTestingIdlingResource idlingResource = new EspressoTestingIdlingResource();

    @Before
    public void setup() {
        IdlingRegistry.getInstance().register(idlingResource.getIdlingResource());
    }

    @After
    public void tearDown() throws Exception {
        IdlingRegistry.getInstance().unregister(idlingResource.getIdlingResource());
    }

    /**
     * Basic UI test - verify main UI components are displayed
     */
    @Test
    public void searchButton_isDisplayed() {
        // Verify that the search button is displayed
        onView(withId(R.id.searchButton))
                .check(matches(isDisplayed()));
    }
    
    /**
     * Test the recyclerView is displayed after clicking Get Teams button
     */
    @Test
    public void teamsButton_displaysRecyclerView() {
        // Click the Get Teams button
        idlingResource.increment();
        onView(withId(R.id.getTeamsButton))
                .perform(click());
                
        // Check if recyclerView is displayed
        onView(withId(R.id.recyclerView))
                .check(matches(isDisplayed()));
        idlingResource.decrement();
    }
} 