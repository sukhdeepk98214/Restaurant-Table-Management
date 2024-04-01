package com.sukhdeep.androidtask.ui.customers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sukhdeep.androidtask.databinding.CustomerCellBinding
import com.sukhdeep.androidtask.db.entity.Customer
import com.sukhdeep.androidtask.db.entity.getCustomerFullName
import com.squareup.picasso.Picasso

class CustomerAdapter(
    private val click: (Customer) -> Unit
) : RecyclerView.Adapter<CustomerAdapter.ViewHolder>() {

    private var customerInfoList: List<Customer> = ArrayList<Customer>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(customerInfoList[position], click)
    }

    override fun getItemCount(): Int = customerInfoList.size

    class ViewHolder private constructor(private val binding: CustomerCellBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Customer, click: (Customer) -> Unit) {
            with(binding) {
                customerNameTextView.text = item.getCustomerFullName()
                Picasso.get().load(item.imgUrl).into(customerAvatarImageView)
                customerItemView.setOnClickListener {
                    click.invoke(item)
                }
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = CustomerCellBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    fun setCustomerList(customerList: List<Customer>) {
        customerInfoList = customerList
        notifyDataSetChanged()
    }
}