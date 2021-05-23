package com.webdevelopment.shoppinglist


import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportFragmentManager
            .beginTransaction()
            .replace(android.R.id.content, MySettingsContent())
            .commit()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.overflow_menu, menu)
        menu?.findItem(R.id.action_sort)?.isVisible = false
        menu?.findItem(R.id.action_edit)?.isVisible = false
        menu?.findItem(R.id.action_share)?.isVisible = false
        menu?.findItem(R.id.action_back)?.isVisible = true
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_back -> {
                finish()
        }
    }
    return super.onOptionsItemSelected(item)
    }
}