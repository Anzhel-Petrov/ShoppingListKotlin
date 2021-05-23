package com.webdevelopment.shoppinglist.adapters


import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.webdevelopment.shoppinglist.MainActivity
import com.webdevelopment.shoppinglist.R
import com.webdevelopment.shoppinglist.data.Product
import kotlinx.android.synthetic.main.list_product.view.*

class ProductAdapter(private var products: MutableList<Product>, private val listener: OnLongItemClickListener) :
    RecyclerView.Adapter<ProductAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_product, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ProductAdapter.ViewHolder, position: Int) {
        // Reference of the corresponding current view item that is displayed to the item from our array
        // We use that to populate the text and graphics of the corresponding current view
        val curProduct = products[position]
        // Create a main activity instance (context) so I can access the the Main Activity counter variable and update the ActionBar menu
        val activity: MainActivity = holder.itemView.context as MainActivity
        // We access our itemViews from our item xml layout
        holder.itemView.apply {
            // Set the values of the TextView items for product name and quantity to the name and quantity of the products in the array accordingly
            ShopProductTitle.text = curProduct.title
            ShopProductQuantity.text = curProduct.quantity.toString()
            // If the delete mode is active
            if (activity.checked) {
                // Show the checkbox
                holder.itemView.ShopProductCheckBox.visibility = View.VISIBLE
                // Set a on check listener on our checkbox
                ShopProductCheckBox.setOnCheckedChangeListener { _, isChecked ->
                    // Call the strikethrough function if the checkbox is checked
                    productStrikeTrough(ShopProductTitle, isChecked)
                    productStrikeTrough(ShopProductQuantity, isChecked)
                    // If the product was checked invert is and vice versa
                    // If isChecked was false make it true and if it was true make it false
                    curProduct.isChecked = !curProduct.isChecked
                    // Update the selected products text counter if the product is checked or unchecked
                    if(curProduct.isChecked)
                    {
                        activity.counter ++
                        activity.invalidateOptionsMenu()
                    } else if (!curProduct.isChecked){
                        activity.counter --
                        activity.invalidateOptionsMenu()
                    }
                }
                // If the deletion activity is false (the back button is pressed) uncheck all products, remove the stricketrough
                // and hide the checkbox
            } else if (!activity.checked){
                holder.itemView.ShopProductCheckBox.visibility = View.GONE
                for(product in products) {
                    product.isChecked = false
                }
                productStrikeTrough(ShopProductTitle, curProduct.isChecked)
            }
        }
    }

    override fun getItemCount(): Int {
        return products.size
    }

    private fun productStrikeTrough(TextView: TextView, isChecked: Boolean) {
        if(isChecked) {
            TextView.paintFlags = TextView.paintFlags or STRIKE_THRU_TEXT_FLAG
        } else {
            TextView.paintFlags = TextView.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnLongClickListener {

        // Set the on long click listener to the ViewHolder (this class)
        init {
            itemView.setOnLongClickListener(this)
        }

        // Override the ViewHolder onLongClick function and call the onLongItemClick interface method passing the position of the clicked item
        override fun onLongClick(v: View?): Boolean {
            val position: Int = adapterPosition
            listener.onLongItemClick(position)
            return true
        }
    }

    // A callback interface - it is implemented in the Main activity and the onLongItemClick is overridden
    interface OnLongItemClickListener {
        fun onLongItemClick(position: Int)
    }
}