package com.sukhdeep.androidtask

import android.content.Intent
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.sukhdeep.androidtask.db.RestaurantDatabase
import com.sukhdeep.androidtask.db.dao.RestaurantDao
import com.sukhdeep.androidtask.db.entity.Customer
import com.sukhdeep.androidtask.db.entity.TableInfo
import com.sukhdeep.androidtask.ui.MainActivity
import com.sukhdeep.androidtask.ui.tables.TableAdapter
import com.sukhdeep.androidtask.utils.EspressoCustomMarchers.Companion.first
import com.sukhdeep.androidtask.utils.EspressoCustomMarchers.Companion.withHolderTablesView
import com.sukhdeep.androidtask.utils.EspressoCustomMarchers.Companion.withRecyclerView
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit
import javax.inject.Inject


/**
 * Instrumented test, which will execute on an Android device.
 * See [testing documentation](http://d.android.com/tools/testing).
 */

@RunWith(AndroidJUnit4::class)
@LargeTest
@HiltAndroidTest
@ExperimentalCoroutinesApi
class AcceptanceTest {

    @Rule
    @JvmField
    var mActivityTestRule: ActivityTestRule<MainActivity> =
        ActivityTestRule(
            MainActivity::class.java, true,
            false
        )

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Inject
    lateinit var database: RestaurantDatabase

    private lateinit var restaurantDao: RestaurantDao

    @Before
    fun setup() {
        hiltRule.inject()

        restaurantDao = database.restaurantDao()
        //launch activity using empty intent (no arguments needed for now ...)
        mActivityTestRule.launchActivity(Intent())
    }

    @After
    fun tearDown() {
        database.clearAllTables()
        mActivityTestRule.finishActivity()
    }

    @Test
    fun checkNoInternetAvailableAlertWasShown() {
        disableInternet()

        TimeUnit.SECONDS.sleep(2)

        onView(withText(R.string.no_internet)).check(matches(isDisplayed()))

        enableInternet()

        TimeUnit.SECONDS.sleep(2)
    }

    @Test
    fun reserveTableTest() {
        waitForLiveData()

        onView(withText(R.string.tables)).check(matches(isDisplayed()))
        onView(withId(R.id.rv_table)).check(matches(isDisplayed()))

        onView(withId(R.id.rv_table))
            .perform(RecyclerViewActions.scrollToHolder(first(withHolderTablesView("Free"))))

        onView(withId(R.id.rv_table))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    1,
                    click()
                )
            )

        //THEN :

        //Screen with customers appear
        onView(withText(R.string.customers)).check(matches(isDisplayed()))

        //WHEN :

        //User clicks on any user
        onView(withId(R.id.rv_customer))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    1,
                    click()
                )
            )

        //THEN :

        //Screen with users tables appear
        onView(withText(R.string.tables)).check(matches(isDisplayed()))

        //Previously selected table is marked as reserved by a user name
        onView(withRecyclerView(R.id.rv_table).atPosition(1))
            .check(matches(not(hasDescendant(withText(R.string.free)))))
    }

    @Test
    fun removeReservationTest() {

        waitForLiveData()

        //App is open
        onView(withText(R.string.tables)).check(matches(isDisplayed()))

        // List of tables visible
        onView(withId(R.id.rv_table)).check(matches(isDisplayed()))

        //There is at least one reserved table
        onView(withId(R.id.rv_table))
            .perform(RecyclerViewActions.scrollToHolder(first(not(withHolderTablesView("Free")))))

        //WHEN :

        //User clicks on a reserved table
        onView(withId(R.id.rv_table))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    2,
                    click()
                )
            )

        //THEN :

        //Confirmation dialog appears
        onView(withText(R.string.want_to_free_table)).check(matches(isDisplayed()))

        //WHEN :

        //User clicks on a accept button
        onView(withText("Yes")).check(matches(isDisplayed())).perform(click())


        //Previously reserved table is marked as free
        onView(withRecyclerView(R.id.rv_table).atPosition(2))
            .check(matches(hasDescendant(withText(R.string.free))))
    }

    @Test
    fun tableListWasShownFromStorageInOfflineMode() {

        insertData()

        TimeUnit.SECONDS.sleep(2)

        disableInternet()

        TimeUnit.SECONDS.sleep(2)

        onView(withText(R.string.tables)).check(matches(isDisplayed()))
        onView(withId(R.id.rv_table)).check(matches(isDisplayed()))

        onView(withId(R.id.rv_table))
            .perform(RecyclerViewActions.scrollToLastPosition<TableAdapter.ViewHolder>())

        onView(withId(R.id.rv_table))
            .perform(RecyclerViewActions.scrollToHolder(first(withHolderTablesView("Free"))))

        enableInternet()

        TimeUnit.SECONDS.sleep(2)
    }

    @Test
    fun checkIfReservedTableHasCorrectUserData() {

        waitForLiveData()

        onView(withText(R.string.tables)).check(matches(isDisplayed()))
        onView(withId(R.id.rv_table)).check(matches(isDisplayed()))

        onView(withId(R.id.rv_table))
            .perform(RecyclerViewActions.scrollToHolder(first(withHolderTablesView("Free"))))

        onView(withId(R.id.rv_table))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    5,
                    click()
                )
            )

        //THEN :

        //Screen with customers appear
        onView(withText(R.string.customers)).check(matches(isDisplayed()))

        //WHEN :

        //User clicks on any user
        onView(withId(R.id.rv_customer))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    7,
                    click()
                )
            )

        //THEN :

        //Screen with users tables appear
        onView(withText(R.string.tables)).check(matches(isDisplayed()))

        onView(withRecyclerView(R.id.rv_table).atPosition(5))
            .check(matches(hasDescendant(withText("Bill Gates"))))
    }

    private fun disableInternet() {
        InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand("svc wifi disable")
        InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand("svc data disable")
    }

    private fun enableInternet() {
        InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand("svc wifi enable")
        InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand("svc data enable")
    }

    private fun waitForLiveData() {
        TimeUnit.SECONDS.sleep(3)
    }

    private fun insertData() = runTest {
        val fakeRestaurantTableList = listOf(
            TableInfo(
                tableId = 101,
                tableShape = "circle",
                customerId = -1
            ),
            TableInfo(
                tableId = 102,
                tableShape = "square",
                customerId = 3
            )
        )
        val fakeCustomerInfoList = listOf(
            Customer(
                customerId = 2,
                customerFirstName = "Test",
                customerLastName = "Test",
                imgUrl = "test"
            ), Customer(
                customerId = 3,
                customerFirstName = "First",
                customerLastName = "Last",
                imgUrl = "Test"
            )
        )
        restaurantDao.insertAllTableList(fakeRestaurantTableList)
        restaurantDao.insertAllCustomerInfo(fakeCustomerInfoList)
    }
}
