package com.example.project5
import SleepDataAdapter
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.fragment.app.Fragment


class MainActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupBottomNavigationView()
    }

    private fun setupBottomNavigationView() {
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener { item ->
            var selectedFragment: Fragment? = null
            when (item.itemId) {
                R.id.mav_home -> selectedFragment =
                    SleepFragment() // Assuming Home corresponds to SleepFragment
                R.id.nav_more -> selectedFragment =
                    more_data_Fragment() // Corresponding to MoreDataFragment
                // Add other cases if you have more items
            }
            if (selectedFragment != null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, selectedFragment)
                    .commit()
            }
            true
        }
        // Optionally set the default selected item if needed
        bottomNavigationView.selectedItemId = R.id.mav_home
    }
}

