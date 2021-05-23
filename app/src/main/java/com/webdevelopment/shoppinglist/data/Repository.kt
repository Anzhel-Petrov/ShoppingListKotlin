package com.webdevelopment.shoppinglist.data

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

object Repository {
    var products = mutableListOf<Product>()

    //listener to changes that we can then use in the Activity
    private var productListener = MutableLiveData<MutableList<Product>>()
    @SuppressLint("StaticFieldLeak")
    private val db = Firebase.firestore

    fun getData(): MutableLiveData<MutableList<Product>> {
        val docRef = db.collection("products")
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {  //any errors
                Log.d("Repository", "Listen failed.", e)
                return@addSnapshotListener
            }
            products.clear() //to avoid duplicates.
            for (document in snapshot?.documents!!) { //add all products to the list
                Log.d("Repository_snapshotlist", "${document.id} => ${document.data}")
                val product = document.toObject<Product>()!!
                product.id = document.id
                products.add(product)
            }
            productListener.value = products //notify our listener we have new data
        }
        return productListener
    }


    fun addProduct(product: Product): MutableLiveData<MutableList<Product>> {
        db.collection("products")
            .add(product)
            .addOnSuccessListener { documentReference ->
                Log.d("Error", "DocumentSnapshot written with ID: " + documentReference.id)
                product.id = documentReference.id
            }
            .addOnFailureListener { e -> Log.w("Error", "Error adding document", e) }
        products.add(product)
        productListener.value = products //we inform the listener we have new data
        return productListener
    }

    fun deleteProduct(): MutableLiveData<MutableList<Product>> {
        for(product in products){
            if(product.isChecked)
            {
                db.collection("products").document(product.id).delete().addOnSuccessListener {
                    Log.d("Snapshot","DocumentSnapshot with id: ${product.id} successfully deleted!")
                }
                    .addOnFailureListener { e -> Log.w("Error", "Error deleting document", e) }

            }
        }
        products.removeAll{
         product -> product.isChecked
        }
        productListener.value = products //we inform the listener we have new data
        return productListener
    }

    fun deleteAll(): MutableLiveData<MutableList<Product>> {
        for(product in products)
        {
                db.collection("products").document(product.id).delete().addOnSuccessListener {
                Log.d("Snapshot","DocumentSnapshot with id: ${product.id} successfully deleted!")
                }
                    .addOnFailureListener { e -> Log.w("Error", "Error deleting document", e) }
        }
        productListener.value = products //we inform the listener we have new data
        return productListener
    }

    fun editProduct(editTitle:String, editQuantity: Int, position: Int): MutableLiveData<MutableList<Product>> {
        val product = products[position]
        db.collection("products").document(product.id)
            .update("title", editTitle,"quantity", editQuantity)
        products[position].title = editTitle
        products[position].quantity = editQuantity
        productListener.value = products //we inform the listener we have new data
        return productListener
    }

    fun restore(backup:MutableList<Product>): MutableLiveData<MutableList<Product>>
    {
        for(product in backup) {
            db.collection("products")
                .add(product)
                .addOnSuccessListener {documentReference ->
                    Log.d("Error", "DocumentSnapshot written with ID: " + documentReference.id)
                    product.id = documentReference.id}
                .addOnFailureListener { e -> Log.w("Error", "Error adding document", e) }
            products.add(product)
        }
        productListener.value = products //we inform the listener we have new data
        return productListener
    }

}