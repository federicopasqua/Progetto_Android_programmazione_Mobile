package com.example.progetto


import androidx.core.os.bundleOf
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.hamcrest.CoreMatchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class CheckFabDisappear {

    @get:Rule
    var activityRule: ActivityScenarioRule<MainActivity>
            = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun changeText_sameActivity() {
        val fragmentArgs = bundleOf("ticker" to "btc")
        val scenario = launchFragmentInContainer<coin_display_fragment>(fragmentArgs, R.style.Theme_AppCompat)

        onView(withId(R.id.display_transactions))
            .perform(click())

        onView(withId(R.id.floatingActionButton))
            .check(matches(isDisplayed()))

        onView(withId(R.id.display_details)).perform(click())

        onView(withId(R.id.floatingActionButton))
            .check(matches(not(isDisplayed())))


    }


}