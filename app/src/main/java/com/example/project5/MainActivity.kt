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
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

var sleep_hour = 0
var sleep_quality = 0
var total_sleep_count = 0
var total_quality_count = 0
class MainActivity : AppCompatActivity(), SleepDataAdapter.OnItemOptionsClickListener {
    private lateinit var addSleepDataButton: View
    private lateinit var saveButton: View
    private lateinit var saveButton1: View
    private lateinit var sleepDataRecyclerView: RecyclerView
    private lateinit var inputSection: View
    private lateinit var sleepHourInput: android.widget.EditText // Updated
    private lateinit var sleepQualityInput: android.widget.EditText // Updated
    private lateinit var notesInput: android.widget.EditText
    private lateinit var averagesleep: TextView
    private lateinit var averagequality: TextView

    private val sleepDataAdapter = SleepDataAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        setupRecyclerView()
        setupListeners()
        observeSleepData()
    }

    private fun initializeViews() {
        addSleepDataButton = findViewById(R.id.addSleepDataButton)
        saveButton = findViewById(R.id.saveButton)
        saveButton1 = findViewById(R.id.saveButton1)
        sleepDataRecyclerView = findViewById(R.id.sleepDataRecyclerView)
        inputSection = findViewById(R.id.inputSection)
        sleepHourInput = findViewById(R.id.sleepHourInput) // Assuming you have this EditText in your XML
        sleepQualityInput = findViewById(R.id.sleepQualityInput) // Assuming you have this EditText
        notesInput = findViewById(R.id.notesInput)
        averagesleep = findViewById(R.id.averageSleepHour)
        averagequality = findViewById(R.id.averageSleepQuality)
    }

    private fun observeSleepData() {
        val dao = (application as SleepApplication).db.sleepDao()
        lifecycleScope.launch {
            dao.getAll().collect { list ->
                sleep_hour = 0
                sleep_quality = 0
                total_sleep_count = 0
                total_quality_count = 0

                // Assuming your adapter expects a list of DisplaySleep objects
                val displayList = list.map { entity ->
                    sleep_hour += (entity.time?.toInt() ?: 0)
                    sleep_quality += (entity.quality?.toInt() ?: 0)
                    total_sleep_count++
                    total_quality_count++
                    DisplaySleep(entity.id, entity.date, entity.time, entity.quality, entity.notes)
                }
                // You need to switch back to the Main thread to submit the list to the adapter
                runOnUiThread {
                    sleepDataAdapter.setSleepData(displayList)
                    averagesleep.text = "Average Hour of Sleep: " + if (total_sleep_count > 0) (sleep_hour / total_sleep_count).toString() + " hours" else "N/A"
                    averagequality.text = "Average Sleep Quality: " + if (total_quality_count > 0) (sleep_quality / total_quality_count).toString() + " / 10" else "N/A"
                }
            }
        }
    }

    private fun setupRecyclerView() {
        sleepDataRecyclerView.layoutManager = LinearLayoutManager(this)
        sleepDataRecyclerView.adapter = sleepDataAdapter
        val dividerItemDecoration = DividerItemDecoration(
            sleepDataRecyclerView.context,
            (sleepDataRecyclerView.layoutManager as LinearLayoutManager).orientation
        )
        sleepDataRecyclerView.addItemDecoration(dividerItemDecoration)
    }

    private fun setupListeners() {
        addSleepDataButton.setOnClickListener {
            toggleInputSection(true)
        }

        saveButton.setOnClickListener {
            // Assuming inputs are valid numbers. You might want to add validation here.
            val sleepHours = sleepHourInput.text.toString().toIntOrNull() ?: 0
            val sleepQuality = sleepQualityInput.text.toString().toIntOrNull() ?: 0
            val notes = notesInput.text.toString()
            val today = Calendar.getInstance().time
            val dateFormat = SimpleDateFormat(
                "EEE, MMM d, yyyy",
                Locale.getDefault()
            ) // Adjust the format as needed
            val dateString = dateFormat.format(today)
            val id = System.currentTimeMillis()
            lifecycleScope.launch(IO) {
                (application as SleepApplication).db.sleepDao().insert(
                    SleepEntity(
                        id = id,
                        date = dateString,
                        time = sleepHours.toString(),
                        quality = sleepQuality.toString(),
                        notes = notes
                    )
                )
            }
            sleepHourInput.text.clear()
            sleepQualityInput.text.clear()
            notesInput.text.clear()
            sleepDataAdapter.addSleepData(
                DisplaySleep(
                    id,
                    dateString,
                    sleepHours.toString(),
                    sleepQuality.toString(),
                    notes
                )
            )
            toggleInputSection(false)

        }
    }

    private fun toggleInputSection(show: Boolean) {
        inputSection.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun onEditClicked(position: Int) {
        saveButton.visibility = View.GONE
        saveButton1.visibility = View.VISIBLE
        val getId = sleepDataAdapter.getItem(position).id
        lifecycleScope.launch(IO) {
            val data = (application as SleepApplication).db.sleepDao().findById(getId)
            withContext(Main) {
                if (data != null) {
                    // Prepopulate your EditText fields with existing data
                    sleepHourInput.setText(data.time)
                    sleepQualityInput.setText(data.quality)
                    notesInput.setText(data.notes)

                }
                toggleInputSection(true) // Show the input section for editing
            }
        }
        saveButton1.setOnClickListener {
            onSaveClicked(position)
            saveButton.visibility = View.VISIBLE
            saveButton1.visibility = View.INVISIBLE
            toggleInputSection(false)
        }
    }

    override fun onDeleteClicked(position: Int) {
        val itemToDelete = sleepDataAdapter.getItem(position)
        sleepDataAdapter.removeItem(position)
        lifecycleScope.launch(IO) {
            (application as SleepApplication).db.sleepDao().deleteById(itemToDelete.id)
        }
        sleepDataAdapter.notifyItemRemoved(position)

    }

    override fun onSaveClicked(position: Int) {
        val data = sleepDataAdapter.getItem(position)
        val id = data.id
        val updatedTime = sleepHourInput.text.toString()
        val updatedQuality = sleepQualityInput.text.toString()
        val updatedNotes = notesInput.text.toString()

        val entry = SleepEntity(
            id,
            data.date,
            updatedTime,
            updatedQuality,
            updatedNotes
        )

        lifecycleScope.launch(IO) {
            (application as SleepApplication).db.sleepDao().update(entry)

            withContext(Main) {
                sleepDataAdapter.updateItem(position, entry.toDisplaySleep())
                sleepHourInput.text.clear()
                sleepQualityInput.text.clear()
                notesInput.text.clear()
                toggleInputSection(false)
            }
        }
    }
}
