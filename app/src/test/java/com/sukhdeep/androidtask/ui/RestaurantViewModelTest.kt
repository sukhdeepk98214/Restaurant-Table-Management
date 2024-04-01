package com.sukhdeep.androidtask.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sukhdeep.androidtask.CoroutinesDispatcherTestRule
import com.sukhdeep.androidtask.api.ApiResult
import com.sukhdeep.androidtask.db.entity.Customer
import com.sukhdeep.androidtask.db.entity.TableInfo
import com.sukhdeep.androidtask.repository.RestaurantServiceRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class RestaurantViewModelTest {

    @get:Rule
    val coroutinesRule = CoroutinesDispatcherTestRule()

    @get:Rule
    val rule = InstantTaskExecutorRule()
    private val restaurantServiceRepository: RestaurantServiceRepository = mockk(relaxed = true)
    private lateinit var classToTest: RestaurantViewModel

    @Test
    fun `restaurantServiceRepository should return network error`() = runTest {
        coEvery { restaurantServiceRepository.getCustomersList() } returns ApiResult.NetworkError
        coEvery { restaurantServiceRepository.getReservationDataList() } returns ApiResult.NetworkError

        classToTest = RestaurantViewModel(restaurantServiceRepository)

        Assert.assertNotNull(classToTest.networkError())
        Assert.assertEquals(true, classToTest.networkError().value)
    }

    @Test
    fun `fetchTableReservationData and getCustomersList should return success`() = runTest {
        val fakeCustomerList = listOf(Customer(1, "Test", "Test", "Test"))
        coEvery { restaurantServiceRepository.getCustomersList() } returns ApiResult.OnSuccess(
            fakeCustomerList
        )
        coEvery { restaurantServiceRepository.getReservationDataList() } returns ApiResult.OnSuccess(
            fakeTableList
        )

        classToTest = RestaurantViewModel(restaurantServiceRepository)

        Assert.assertEquals(fakeCustomerList, classToTest.customerMutableList.value)
        coVerify { restaurantServiceRepository.getCustomerData(any()) }
        val customerId = classToTest.tableMutableList.value?.get(0)?.customerId
        Assert.assertEquals(1, customerId)
    }

    @Test
    fun `fetchTableReservationData should return failure if failed when getCustomersList was success`() =
        runTest {
            val failedError = "error"
            coEvery { restaurantServiceRepository.getCustomersList() } returns ApiResult.OnSuccess(
                emptyList()
            )
            coEvery { restaurantServiceRepository.getReservationDataList() } returns ApiResult.OnFailure(
                failedError
            )

            classToTest = RestaurantViewModel(restaurantServiceRepository)

            Assert.assertNotNull(classToTest.customerMutableList.value)

            val error = classToTest.getDisplayErrorLiveData().value
            Assert.assertEquals(failedError, error)
        }

    @Test
    fun `getCustomersList should return failure if failed when fetchTableReservationData was success`() =
        runTest {
            val failedError = "error"
            coEvery { restaurantServiceRepository.getCustomersList() } returns ApiResult.OnFailure(
                failedError
            )
            coEvery { restaurantServiceRepository.getReservationDataList() } returns ApiResult.OnSuccess(
                fakeTableList
            )

            classToTest = RestaurantViewModel(restaurantServiceRepository)

            val error = classToTest.getDisplayErrorLiveData().value
            Assert.assertEquals(failedError, error)

            Assert.assertNotNull(classToTest.tableMutableList.value)
        }

    @Test
    fun `getTableData should not call getCustomerData when customer has not reserved the table`() =
        runTest {
            val tableList = listOf(TableInfo(1, "Test", -1))
            coEvery { restaurantServiceRepository.getCustomersList() } returns ApiResult.OnSuccess(
                emptyList()
            )
            coEvery { restaurantServiceRepository.getReservationDataList() } returns ApiResult.OnSuccess(
                tableList
            )

            classToTest = RestaurantViewModel(restaurantServiceRepository)

            coVerify(exactly = 0) { restaurantServiceRepository.getCustomerData(any()) }
        }

    @Test
    fun `updateTableReservation should call updateReservationData when user is selected`() =
        runTest {
            coEvery { restaurantServiceRepository.updateReservationData(any(), any()) } returns Unit

            classToTest = RestaurantViewModel(restaurantServiceRepository)
            classToTest.selectedTableId = 1
            classToTest.updateTableReservation(1)

            coVerify { restaurantServiceRepository.updateReservationData(any(), any()) }
        }

    @Test
    fun `hasUserReservedTable should return user if that user has reserved the table `() = runTest {
        coEvery { restaurantServiceRepository.getReservationDataList() } returns ApiResult.OnSuccess(
            fakeTableList
        )

        classToTest = RestaurantViewModel(restaurantServiceRepository)

        Assert.assertNotNull(classToTest.tableMutableList.value)

        Assert.assertNotNull(classToTest.hasUserReservedTable(1))
    }

    private val fakeTableList = listOf(TableInfo(1, "Test", 1))

}