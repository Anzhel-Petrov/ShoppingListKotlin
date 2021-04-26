package com.webdevelopment.shoppinglist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.webdevelopment.shoppinglist.adapters.ProductAdapter
import com.webdevelopment.shoppinglist.data.Product
import com.webdevelopment.shoppinglist.data.Repository
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.list_product.*

class MainActivity : AppCompatActivity() {

    //you need to have an Adapter for the products
    lateinit var adapter: ProductAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Repository.getData().observe(this, Observer {
            Log.d("Products","Found ${it.size} products")
            updateUI()
        })

        ShopButtonAdd.setOnClickListener {
            val productTitle = ShopListTitle.text.toString()
            if(productTitle.isNotEmpty()) {
                val product = Product(productTitle)
                Repository.addProduct(product).observe(this, Observer {
                    Log.d("Product","${product.title} added to the list")
                    updateUI()
                })
                ShopListTitle.text.clear()
            }
        }

        ShopButtonDelete.setOnClickListener {
            Repository.deleteProduct().observe(this, Observer {
            Log.d("Product","Product deleted")
            updateUI()
        })
        }
    }

    fun updateUI() {
        val layoutManager = LinearLayoutManager(this)

        /*you need to have a defined a recylerView in your
        xml file - in this case the id of the recyclerview should
        be "recyclerView" - as the code line below uses that */

        recyclerView.layoutManager = layoutManager

        adapter = ProductAdapter(Repository.products)

        /*connecting the recyclerview to the adapter  */
        recyclerView.adapter = adapter

    }
}