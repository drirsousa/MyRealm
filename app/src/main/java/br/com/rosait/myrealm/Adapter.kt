package br.com.rosait.myrealm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import br.com.rosait.myrealm.databinding.ItemLayoutBinding

class Adapter(val items: MutableList<Item>, val clickEdit: (Item) -> Unit, val clickDelete: (Item) -> Unit) : RecyclerView.Adapter<Adapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], clickEdit, clickDelete)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding: ItemLayoutBinding? = DataBindingUtil.bind(itemView)

        fun bind(item: Item, clickEdit: (Item) -> Unit, clickDelete: (Item) -> Unit) {
            binding?.item = item

            binding?.imvEdit?.setOnClickListener { clickEdit(item) }
            binding?.imvDelete?.setOnClickListener { clickDelete(item) }
        }
    }
}