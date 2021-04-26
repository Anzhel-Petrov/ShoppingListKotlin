package com.webdevelopment.shoppinglist.data

import androidx.lifecycle.MutableLiveData

object Repository {
    var products = mutableListOf<Product>()

    //listener to changes that we can then use in the Activity
    private var productListener = MutableLiveData<MutableList<Product>>()


    fun getData(): MutableLiveData<MutableList<Product>> {
        if (products.isEmpty())
            createTestData()
        productListener.value = products //we inform the listener we have new data
        return productListener
    }

    fun addProduct(product: Product): MutableLiveData<MutableList<Product>> {
        products.add(product)
        productListener.value = products //we inform the listener we have new data
        return productListener
    }

    fun deleteProduct(): MutableLiveData<MutableList<Product>> {
        products.removeAll{
         product -> product.isChecked
        }
        productListener.value = products //we inform the listener we have new data
        return productListener
    }

    fun createTestData()
    {
        //add some products to the products list - for testing purposes
        for (i in 0..10) {
            products.add(Product("Product $i"))
        }
    }

}