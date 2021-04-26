package com.webdevelopment.shoppinglist.adapters

import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.webdevelopment.shoppinglist.R
import com.webdevelopment.shoppinglist.data.Product
import kotlinx.android.synthetic.main.list_product.view.*

class ProductAdapter(var products: MutableList<Product>) :
    RecyclerView.Adapter<ProductAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_product,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ProductAdapter.ViewHolder, position: Int) {
        val curProduct = products[position]
        holder.itemView.apply {
            ShopProductTitle.text = curProduct.title
            ShopProductCheckBox.isChecked = curProduct.isChecked
            productStrikeTrough(ShopProductTitle, curProduct.isChecked)
            ShopProductCheckBox.setOnCheckedChangeListener { _, isChecked ->
                productStrikeTrough(ShopProductTitle, isChecked)
                curProduct.isChecked = !curProduct.isChecked
            }
        }
    }

    override fun getItemCount(): Int {
        return products.size
    }

    private fun productStrikeTrough(ShopProductTitle: TextView, isChecked: Boolean) {
        if(isChecked) {
            ShopProductTitle.paintFlags = ShopProductTitle.paintFlags or STRIKE_THRU_TEXT_FLAG
        } else {
            ShopProductTitle.paintFlags = ShopProductTitle.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //here you need to to do stuff also - to back to the exercises
        //about recyclerviews and you can use approach that were used
        //in the exercise about recyclerviews from the book (lesson 3)
        //if you did not do that exercise - then first do that exercise in
        //a seperate project

    }
}