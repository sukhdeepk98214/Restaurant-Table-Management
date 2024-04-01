package com.sukhdeep.androidtask.ui.tables

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sukhdeep.androidtask.R
import com.sukhdeep.androidtask.databinding.TableCellBinding
import com.sukhdeep.androidtask.model.TableData
import com.squareup.picasso.Picasso

class TableAdapter(private val click: (TableData, Int) -> Unit) :
    RecyclerView.Adapter<TableAdapter.ViewHolder>() {
    private var tableList: List<TableData> = ArrayList<TableData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(tableList[position], click)
    }

    override fun getItemCount(): Int = tableList.size

    class ViewHolder private constructor(private val binding: TableCellBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TableData, click: (TableData, Int) -> Unit) {
            with(binding) {

                tableId.text = item.tableId.toString()

                if (item.customerId != -1) {
                    reservingCustomerName.text = item.customerName
                    reservingCustomerName.setTextColor(Color.RED)

                    //load reserved user image
                    item.imgUrl?.let {
                        Picasso.get().load(it).into(avatarImageView)
                    }
                    avatarImageView.visibility = View.VISIBLE
                } else {
                    reservingCustomerName.text = binding.root.context.getString(R.string.free)
                    reservingCustomerName.setTextColor(Color.GREEN)
                    avatarImageView.visibility = View.INVISIBLE
                }

                tableImageView.setImageResource(getTableShapeImageResourceId(item.shape))
                binding.listItem.setOnClickListener {
                    // deprecated remove it
                    click.invoke(item, bindingAdapterPosition)
                }
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = TableCellBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

        private fun getTableShapeImageResourceId(tableShape: String?): Int {
            return when (tableShape) {
                binding.root.context.getString(R.string.circle) -> R.drawable.ic_circle
                binding.root.context.getString(R.string.square) -> R.drawable.ic_square
                else -> R.drawable.ic_rectangle
            }
        }

    }

    fun setTableList(tableDataList: List<TableData>) {
        tableList = tableDataList
        notifyDataSetChanged()
    }

}