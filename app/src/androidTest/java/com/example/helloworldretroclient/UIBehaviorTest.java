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
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.Visibility;
import static org.hamcrest.CoreMatchers.not;

/**
 * Tests focused on UI behavior and visual elements
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class UIBehaviorTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    private EspressoTestingIdlingResource idlingResource = new EspressoTestingIdlingResource();

    @Before
    public void registerIdlingResource() {
        IdlingRegistry.getInstance().register(idlingResource.getIdlingResource());
    }

    @After
    public void unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(idlingResource.getIdlingResource());
    }

    /**
     * Test that the form container is properly displayed and hidden
     * This verifies that the form overlay functionality works correctly
     */
    @Test
    public void formContainer_ShowsAndHidesCorrectly() {
        // Check that form container is initially hidden
        onView(withId(R.id.formContainer))
                .check(matches(withEffectiveVisibility(Visibility.GONE)));

        // Open create team form
        onView(withId(R.id.createTeamButton))
                .perform(click());

        // Check that form container is now visible
        onView(withId(R.id.formContainer))
                .check(matches(isDisplayed()));

        // Verify team form content is visible
        onView(withId(R.id.teamNameInput))
                .check(matches(isDisplayed()));

        // Cancel form
        onView(withId(R.id.cancelTeamButton))
                .perform(click());

        // Verify form container is hidden again
        onView(withId(R.id.formContainer))
                .check(matches(withEffectiveVisibility(Visibility.GONE)));
    }

    /**
     * Test that main content is not visible/interactive when form is displayed
     * This verifies that the form properly overlays the main content
     */
    @Test
    public void mainContent_NotInteractiveWhenFormDisplayed() {
        // Show create team form
        onView(withId(R.id.createTeamButton))
                .perform(click());

        // Attempt to interact with main content elements (should not be possible)
        try {
            // These should fail because elements should not be clickable/visible
            onView(withId(R.id.searchButton))
                    .perform(click());
            
            // If we get here, the test fails
            throw new AssertionError("Main content is still interactive when form is displayed");
        } catch (androidx.test.espresso.AmbiguousViewMatcherException | 
                 androidx.test.espresso.NoMatchingViewException e) {
            // This is expected - the view should not be visible/clickable
        }

        // Cancel form
        onView(withId(R.id.cancelTeamButton))
                .perform(click());

        // Now main content should be interactive again
        onView(withId(R.id.searchButton))
                .check(matches(isDisplayed()));
    }

    /**
     * Test that back button properly dismisses forms
     */
    @Test
    public void backButton_DismissesForms() {
        // Show create team form
        onView(withId(R.id.createTeamButton))
                .perform(click());

        // Form should be visible
        onView(withId(R.id.formContainer))
                .check(matches(isDisplayed()));

        // Press back button
        pressBack();

        // Form should be dismissed, form container should be gone
        onView(withId(R.id.formContainer))
                .check(matches(withEffectiveVisibility(Visibility.GONE)));

        // Main content should be visible
        onView(withId(R.id.searchButton))
                .check(matches(isDisplayed()));
    }

    /**
     * Test searching and viewing results
     */
    @Test
    public void searchResultsHeader_AppearsWhenSearching() {
        // Initially, search results header should be hidden
        onView(withId(R.id.searchResultsHeader))
                .check(matches(withEffectiveVisibility(Visibility.GONE)));

        // Perform search
        idlingResource.increment();
        onView(withId(R.id.searchButton))
                .perform(click());

        // Now search results header should be visible
        onView(withId(R.id.searchResultsHeader))
                .check(matches(isDisplayed()));
        
        idlingResource.decrement();
    }
} 