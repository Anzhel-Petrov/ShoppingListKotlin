package com.webdevelopment.shoppinglist

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseApp
import com.webdevelopment.shoppinglist.adapters.ProductAdapter
import com.webdevelopment.shoppinglist.data.Product
import com.webdevelopment.shoppinglist.data.Repository
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), ProductAdapter.OnLongItemClickListener {

    //you need to have an Adapter for the products
    private val RESULT_CODE_PREFERENCES = 1
    private lateinit var adapter: ProductAdapter
    var checked: Boolean = false
    private var ascending: Boolean = false
    private var quantityHighest: Boolean = false
    var counter: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        val darkmode = PreferenceHandler.useDarkMode(this)
        if (darkmode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(applicationContext)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        Repository.getData().observe(this, Observer {
            Log.d("Products", "Found ${it.size} products")
            updateUI()
        })

        ShopButtonAdd.setOnClickListener {
            val productTitle = ShopListTitle.text.toString()
            val productQuantity = ShopListQuantity.text.toString()
            if (productTitle.isEmpty() || productQuantity.isEmpty() || productQuantity.toInt() == 0) {
                Toast.makeText(applicationContext, "Product title and quantity cannot be empty. Quantity cannot be 0.", Toast.LENGTH_SHORT).show()
            } else {
                val product = Product(productTitle, productQuantity.toInt())
                Repository.addProduct(product).observe(this, Observer {
                    Log.d("Product", "${product.title} added to the list")
                    adapter.notifyDataSetChanged()
                })
                ShopListTitle.text.clear()
                ShopListQuantity.text.clear()
            }
        }
    }

    private fun updateUI() {
        val layoutManager = LinearLayoutManager(this)

        /*you need to have a defined a recyclerView in your
        xml file - in this case the id of the recyclerview should
        be "recyclerView" - as the code line below uses that */

        recyclerView.layoutManager = layoutManager

        adapter = ProductAdapter(Repository.products, this)

        /*connecting the recyclerview to the adapter  */
        recyclerView.adapter = adapter

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.overflow_menu, menu)
        if (checked) {
            menu?.findItem(R.id.action_delete)?.isVisible = true
            menu?.findItem(R.id.action_delete_all)?.isVisible = true
            menu?.findItem(R.id.action_sort)?.isVisible = true
            menu?.findItem(R.id.action_sort)?.isVisible = false
            menu?.findItem(R.id.action_edit)?.isVisible = false
            menu?.findItem(R.id.action_back)?.isVisible = true
            menu?.findItem(R.id.action_settings)?.isVisible = false
            menu?.findItem(R.id.action_share)?.isVisible = false
            menu?.findItem(R.id.action_items_selected)?.isVisible = true
            if (counter == 0)
                menu?.findItem(R.id.action_items_selected)?.title = "No products selected"
            if (counter == 1)
                menu?.findItem(R.id.action_items_selected)?.title = "$counter product selected"
            if (counter > 1)
                menu?.findItem(R.id.action_items_selected)?.title = "$counter products selected"
        }
        return true
    }

    // Make the array of products into more understandable stream of strings when we share it
    fun convertListToString(): String
    {
        var result = ""
        for (product in Repository.products)
        {
            result += "Product: ${product.title} Quantity: ${product.quantity}\n"
        }
        return result
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Hide the keyboard
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        val parent = layout
        imm!!.hideSoftInputFromWindow(parent.windowToken, 0)
        when (item.itemId) {
            R.id.action_sort_by_name -> {
                // If ascending is false switch it to true and order the list in ascending order
                if(!ascending) {
                    Repository.products.sortBy { it.title }
                    Toast.makeText(this, "List ordered by name (Ascending)", Toast.LENGTH_SHORT)
                        .show()
                    ascending = true
                } else {
                    // If ascending is true switch it to false and order the list in descending order
                    Repository.products.sortByDescending { it.title }
                    ascending = false
                    Toast.makeText(this, "List ordered by name (Descending)", Toast.LENGTH_SHORT)
                        .show()
                }
                adapter.notifyDataSetChanged()
            }
            R.id.action_sort_by_quantity -> {
                // Same logic as order by name using a boolean
                if(!quantityHighest) {
                    Repository.products.sortBy { it.quantity }
                    Toast.makeText(this, "List ordered by quantity (Ascending)", Toast.LENGTH_SHORT)
                        .show()
                    quantityHighest = true
                } else {
                    Repository.products.sortByDescending { it.quantity }
                    quantityHighest = false
                    Toast.makeText(this, "List ordered by name (Descending)", Toast.LENGTH_SHORT)
                        .show()
                }
                adapter.notifyDataSetChanged()
            }
            R.id.action_edit -> {
                checked = true
                invalidateOptionsMenu()
                adapter.notifyDataSetChanged()
            }
            R.id.action_delete_all -> {
                if (Repository.products.isNotEmpty()) {
                    val dialog = CustomDialogFragment(::positiveClicked, ::negativeClick)
                    //Here we show the dialog
                    //The tag "MyFragment" is not important for us.
                    dialog.show(supportFragmentManager, "myFragment")
                } else {
                    Toast.makeText(applicationContext, "The list is empty.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            R.id.action_delete -> {
                val productsBackup = mutableListOf<Product>()

                if (Repository.products.isNotEmpty()) {
                    if (Repository.products.any { product -> product.isChecked }) {
                        for (product in Repository.products) {
                            if (product.isChecked)
                                productsBackup.add(product)
                        }
                        Repository.deleteProduct().observe(this, Observer {
                            Log.d("Product", "Product deleted")
                            checked = false
                            counter = 0
                            invalidateOptionsMenu()
                            updateUI()
                        })
                        val snack = Snackbar
                            .make(parent, "Products deleted.", Snackbar.LENGTH_LONG)
                            .setAction("UNDO") {
                                //This code will ONLY be executed in case that
                                //the user has hit the UNDO button
                                Repository.restore(productsBackup)
                                val snack = Snackbar.make(
                                    parent,
                                    "Deleted products restored.",
                                    Snackbar.LENGTH_SHORT
                                )
                                //Show the user we have restored the name - but here
                                //on this snack there is NO UNDO - so no SetAction method is called
                                //if you wanted, you could include a REDO on the second action button
                                //for instance.
                                snack.show()
                            }
                        snack.show()
                    } else {
                        Toast.makeText(applicationContext, "Nothing selected", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(applicationContext, "The list is empty.", Toast.LENGTH_SHORT)
                        .show()
                }

            }
            R.id.action_back -> {
                checked = false
                counter = 0
                invalidateOptionsMenu()
                updateUI()
            }
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivityForResult(intent, RESULT_CODE_PREFERENCES)
            }
            R.id.action_share -> {

                /* Share content */
                val text = convertListToString() //from EditText
                val sharingIntent = Intent(Intent.ACTION_SEND)
                sharingIntent.type = "text/plain" //MIME-TYPE
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "This is my shopping list")
                sharingIntent.putExtra(Intent.EXTRA_TEXT, text)
                startActivity(Intent.createChooser(sharingIntent, "Share Using"))
                return true
            }
        }
        return false //we did not handle the event
    }

    private fun positiveClicked() {
        Repository.deleteAll().observe(this, Observer {
            Log.d("Products", "Found ${it.size} products")
            updateUI()
        })
        Toast.makeText(applicationContext, "Shopping list deleted.", Toast.LENGTH_SHORT).show()
    }


    //callback function from yes/no dialog - for no choice
    private fun negativeClick() {
    }

    override fun onLongItemClick(position: Int) {
        // Identify the product and save it in a variable based on the position value of the recycler view item, received from the adapter
        val clickedItem: Product = Repository.products[position]
        // Initialize the Alert Dialog builder and inflate our custom edit layout
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.edit_custom_layout, null)
        // Include the two EditText view items in our dialog - one for product title and one for quantity
        val editTitleInput = dialogLayout.findViewById<EditText>(R.id.edit_product_title)
        val editQuantityInput = dialogLayout.findViewById<EditText>(R.id.edit_product_quantity)
        // Set the value of the two EditText to the values of the specific item that was clicked - product title and quantity values
        editTitleInput.setText(clickedItem.title)
        editQuantityInput.setText(clickedItem.quantity.toString())
        builder.setView(dialogLayout)
        builder.setPositiveButton("OK") { _, _ ->
            // On positive "OK" button click save the values of the two EditView view items to variables (reversing the process above)
            val editTitle = editTitleInput.text.toString()
            val editQuantity = editQuantityInput.text.toString()
            // Make a check if the user is trying to enter empty values or change the quantity to 0
            if(editTitle.isEmpty() || editQuantity.isEmpty() || editQuantity.toInt() == 0) {
                Toast.makeText(applicationContext, "Product title and quantity cannot be empty. Quantity cannot be 0.", Toast.LENGTH_SHORT).show()
            }
            // Make a check if there are difference of the EditText values and the product properties in our products array
            // If there are pass the values to the Repository function
            else if (editTitle != Repository.products[position].title || editQuantity.toInt() != Repository.products[position].quantity) {
//                clickedItem.title = m_Text
                Repository.editProduct(editTitle, editQuantity.toInt(), position).observe(this, Observer {
                    Log.d("Products", "Found ${it.size} products")
                    updateUI()
                })
                adapter.notifyDataSetChanged()
            }
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
        builder.show()
    }
}


