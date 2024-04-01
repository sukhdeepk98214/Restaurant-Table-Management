package com.sukhdeep.androidtask.ui.tables

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
import com.sukhdeep.androidtask.databinding.FragmentTableBinding
import com.sukhdeep.androidtask.model.TableData
import com.sukhdeep.androidtask.ui.MainActivity
import com.sukhdeep.androidtask.ui.RestaurantViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TableFragment : Fragment() {

    private var binding: FragmentTableBinding? = null
    private val tableViewModel: RestaurantViewModel by activityViewModels()
    private var adapter: TableAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTableBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        initObserver()
    }

    private fun initUI() {

        val actionBar = (activity as MainActivity).supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(false)

        val layoutManager = LinearLayoutManager(context)
        binding?.rvTable?.layoutManager = layoutManager
        adapter = TableAdapter { item, pos ->
            tableViewModel.selectedTableId = item.tableId

            if (item.customerId != -1) {
                showFreeTableAlert(item)
            } else {
                findNavController().navigate(R.id.action_tableFragment_to_customerFragment)
            }

        }
        binding?.rvTable?.adapter = adapter

    }

    private fun initObserver() {
        with(tableViewModel) {
            tableMutableList.observe(viewLifecycleOwner) { tableList ->
                adapter?.setTableList(tableList)
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
                    requireActivity(), error,
                    Toast.LENGTH_LONG
                ).show()
            }

            networkError().observe(viewLifecycleOwner) {
                if (it) checkInternet()
            }
        }
    }

    private fun showFreeTableAlert(table: TableData) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder
            .setTitle(getString(R.string.want_to_free_table))
            .setPositiveButton(getString(R.string.yes)) { dialog, which ->
                // Free table
                table.customerId = -1
                tableViewModel.updateTableReservation(-1)
                adapter?.notifyDataSetChanged()
            }
            .setNegativeButton(getString(R.string.no)) { dialog, which ->
                dialog.cancel()
            }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun checkInternet() {
        //close the app when no internet
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder
            .setTitle(getString(R.string.no_internet))
            .setPositiveButton(getString(R.string.close_app)) { dialog, which ->
                (activity as MainActivity).finish()
            }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter = null
        binding = null
    }
}