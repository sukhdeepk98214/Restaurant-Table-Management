package com.sukhdeep.androidtask.ui.customers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sukhdeep.androidtask.R
import com.sukhdeep.androidtask.databinding.FragmentCustomerBinding
import com.sukhdeep.androidtask.ui.RestaurantViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CustomerFragment : Fragment() {

    private var binding: FragmentCustomerBinding? = null
    private val restaurantViewModel: RestaurantViewModel by activityViewModels()
    private var adapter: CustomerAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCustomerBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        initObserver()
    }

    private fun initUI() {
        val linearLayoutManager = LinearLayoutManager(context)
        binding?.rvCustomer?.layoutManager = linearLayoutManager
        adapter = CustomerAdapter { item ->
            with(restaurantViewModel) {
                if (hasUserReservedTable(item.customerId) == null) {
                    updateTableReservation(item.customerId)
                    onPopBackPress()
                } else {
                    showTableAlreadyBooked()
                }
            }
        }
        binding?.rvCustomer?.adapter = adapter
    }

    private fun initObserver() {
        with(restaurantViewModel) {

            customerMutableList.observe(viewLifecycleOwner) { customerList ->
                customerList?.let { adapter?.setCustomerList(it) }
            }
            getIsLoadingLiveData().observe(viewLifecycleOwner) { loader ->
                if (loader) {
                    binding?.pb?.visibility = View.VISIBLE
                } else {
                    binding?.pb?.visibility = View.GONE
                }

            }

            getDisplayErrorLiveData().observe(viewLifecycleOwner) { error ->
                Toast.makeText(
                    context, error,
                    Toast.LENGTH_LONG
                ).show()
            }

            networkError().observe(viewLifecycleOwner) {
                if (it) Toast.makeText(
                    context, getString(R.string.no_internet),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun showTableAlreadyBooked() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder
            .setTitle(getString(R.string.table_alread_booked))
            .setPositiveButton(getString(R.string.ok)) { dialog, which ->
                onPopBackPress()
            }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun onPopBackPress() {
        findNavController().popBackStack()
    }

}