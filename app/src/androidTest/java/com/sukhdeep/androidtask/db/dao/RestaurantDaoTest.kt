package com.sukhdeep.androidtask.db.dao

import androidx.test.filters.SmallTest
import com.sukhdeep.androidtask.db.RestaurantDatabase
import com.sukhdeep.androidtask.db.entity.Customer
import com.sukhdeep.androidtask.db.entity.TableInfo
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import javax.inject.Named

@SmallTest
@ExperimentalCoroutinesApi
@HiltAndroidTest
class RestaurantDaoTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    @Named("test_db")
    lateinit var database: RestaurantDatabase

    private lateinit var restaurantDao: RestaurantDao

    @Before
    fun setUpDb() {
        hiltRule.inject()

        restaurantDao = database.restaurantDao()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun check_if_insertAllTableData_return_data() = runTest {
        val restaurantTableList = listOf(
            TableInfo(
                tableId = 101,
                tableShape = "circle",
                customerId = 2
            )
        )
        restaurantDao.insertAllTableList(restaurantTableList)
        val list: List<TableInfo> = restaurantDao.getTableList()
        Assert.assertEquals(restaurantTableList[0].tableId, list[0].tableId)
    }


    @Test
    fun check_if_insertAllCustomerInfo_return_data() = runTest {
        val customerInfoList = listOf(
            Customer(
                customerId = 2,
                customerFirstName = "Test",
                customerLastName = "Test",
                imgUrl = "test"
            )
        )
        restaurantDao.insertAllCustomerInfo(customerInfoList)
        val list: List<Customer> = restaurantDao.getCustomerList()
        Assert.assertEquals(customerInfoList[0].customerId, list[0].customerId)
    }

    @Test
    fun updateReservationList_should_update_tableData_data() = runTest {
        val tableReservedList = listOf(
            TableInfo(
                tableId = 101,
                tableShape = "circle",
                customerId = -1
            )
        )

        restaurantDao.insertAllTableList(tableReservedList)
        restaurantDao.updateReservationList(101, 3)

        val tableList: List<TableInfo> = listOf(restaurantDao.getTableList().first())

        Assert.assertTrue(tableList.isNotEmpty())
        Assert.assertEquals(3, tableList.first().customerId)
    }

}