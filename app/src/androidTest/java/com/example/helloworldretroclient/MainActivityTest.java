package com.example.helloworldretroclient;

import android.view.View;

import androidx.test.espresso.IdlingRegistry;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;

/**
 * End-to-end tests for the FootBallApiClient app
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {

    private static final String PLAYER_NAME = "Messi";

    // Custom IdlingResource to wait for network calls
    private EspressoTestingIdlingResource idlingResource = new EspressoTestingIdlingResource();

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void registerIdlingResource() {
        IdlingRegistry.getInstance().register(idlingResource.getIdlingResource());
    }

    @After
    public void unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(idlingResource.getIdlingResource());
    }

    /**
     * HAPPY PATH TEST: Search by entering a player name
     */
    @Test
    public void enterPlayerName_AndClickSearch() {
        // Enter player name in search field
        onView(withId(R.id.inputName))
                .perform(typeText(PLAYER_NAME), closeSoftKeyboard());

        // Click search button
        idlingResource.increment();
        onView(withId(R.id.searchButton))
                .perform(click());

        // Verify search results header is displayed
        onView(withId(R.id.searchResultsHeader))
                .check(matches(isDisplayed()));
        
        idlingResource.decrement();
    }

    /**
     * HAPPY PATH TEST: View teams list
     */
    @Test
    public void clickViewTeams_DisplaysTeamsList() {
        // Click View Teams button
        idlingResource.increment();
        onView(withId(R.id.getTeamsButton))
                .perform(click());

        // Check if teams are displayed in recycler view
        onView(withId(R.id.recyclerView))
                .check(matches(isDisplayed()));
                
        idlingResource.decrement();
    }

    /**
     * HAPPY PATH TEST: Open create team form and cancel
     */
    @Test
    public void openCreateTeamForm_ThenCancel() {
        // Click Create Team button
        onView(withId(R.id.createTeamButton))
                .perform(click());

        // Verify create team form is displayed
        onView(withId(R.id.teamNameInput))
                .check(matches(isDisplayed()));

        // Click cancel button
        onView(withId(R.id.cancelTeamButton))
                .perform(click());

        // Verify we're back to the main view
        onView(withId(R.id.createTeamButton))
                .check(matches(isDisplayed()));
    }

    /**
     * Integration TEST: Back button behavior
     * Tests the back button navigation flow in the app
     */
    @Test
    public void testBackButtonBehavior() {
        // First, open create team form
        onView(withId(R.id.createTeamButton))
                .perform(click());

        // Press back
        pressBack();

        // Verify we're back to the main view
        onView(withId(R.id.createTeamButton))
                .check(matches(isDisplayed()));
    }

    /**
     * Custom matcher that checks if a RecyclerView has items
     */
    private static Matcher<View> hasItems() {
        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("RecyclerView with at least one item");
            }

            @Override
            protected boolean matchesSafely(View view) {
                if (!(view instanceof androidx.recyclerview.widget.RecyclerView)) {
                    return false;
                }
                androidx.recyclerview.widget.RecyclerView recyclerView = 
                        (androidx.recyclerview.widget.RecyclerView) view;
                return recyclerView.getAdapter() != null && 
                       recyclerView.getAdapter().getItemCount() > 0;
            }
        };
    }
    
    /**
     * Custom matcher that checks if a RecyclerView has a specific number of items
     */
    private static Matcher<View> hasChildCount(final int count) {
        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("RecyclerView with child count: " + count);
            }

            @Override
            protected boolean matchesSafely(View view) {
                if (!(view instanceof androidx.recyclerview.widget.RecyclerView)) {
                    return false;
                }
                androidx.recyclerview.widget.RecyclerView recyclerView = 
                        (androidx.recyclerview.widget.RecyclerView) view;
                return recyclerView.getAdapter() != null && 
                       recyclerView.getAdapter().getItemCount() == count;
            }
        };
    }
} 