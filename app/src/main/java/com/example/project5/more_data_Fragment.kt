package com.example.project5
import android.graphics.Color

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.text.SimpleDateFormat
import java.util.*
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class more_data_Fragment : Fragment() {
    private lateinit var lineChart: LineChart
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_more_data_, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lineChart = view.findViewById(R.id.chart)
        loadDataAndSetupChart()
    }

    private fun setupChart() {

        lineChart.xAxis.valueFormatter = object : ValueFormatter() {
            private val dateFormat = SimpleDateFormat("MMM d", Locale.getDefault())

            override fun getFormattedValue(value: Float): String {
                val startDate = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault()).parse("Mon, Jan 1, 2020") // Match the start date used in conversion
                val calendar = Calendar.getInstance().apply {
                    time = startDate
                    add(Calendar.DAY_OF_YEAR, value.toInt())
                }
                return dateFormat.format(calendar.time)
            }
        }
        // It's also a good place to configure axis, legends, and any other chart settings.
        lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        lineChart.axisRight.isEnabled = false
        // ...
    }

    private fun convertDateToFloat(dateStr: String): Float {
        val dateFormat = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val startDate = dateFormat.parse("Mon, Jan 1, 2020")
        val currentDate = dateFormat.parse(dateStr)
        val difference = currentDate.time - startDate.time
        return (difference / (24 * 60 * 60 * 1000)).toFloat()
    }


    private fun loadDataAndSetupChart() {
        val dao = (requireActivity().application as SleepApplication).db.sleepDao()
        viewLifecycleOwner.lifecycleScope.launch {
            val sleepData = dao.getAll().first()
            val timeEntries = ArrayList<Entry>() // For sleep time
            val qualityEntries = ArrayList<Entry>() // For sleep quality

            sleepData.forEach { sleep ->
                sleep.date?.let { dateStr ->
                    val dateValue = convertDateToFloat(dateStr)
                    // Add sleep time entry
                    sleep.time?.toFloatOrNull()?.let { time ->
                        timeEntries.add(Entry(dateValue, time))
                    }
                    // Add sleep quality entry
                    sleep.quality?.toFloatOrNull()?.let { quality ->
                        qualityEntries.add(Entry(dateValue, quality))
                    }
                }
            }

            timeEntries.sortBy { it.x }
            qualityEntries.sortBy { it.x }

            // Creating two data sets
            val timeDataSet = LineDataSet(timeEntries, "Sleep Time").apply {
                // Customize dataset appearance (color, width, etc.)
                color = Color.BLUE
                valueTextColor = Color.BLACK
                lineWidth = 2f
            }

            val qualityDataSet = LineDataSet(qualityEntries, "Sleep Quality").apply {
                // Customize dataset appearance
                color = Color.RED
                valueTextColor = Color.BLACK
                lineWidth = 2f
            }

            withContext(Dispatchers.Main) {
                val lineData = LineData(timeDataSet, qualityDataSet)
                lineChart.data = lineData
                setupChart() // Apply any additional chart configurations
                lineChart.invalidate() // Refresh the chart
            }
        }
    }


    private fun fetchSleepData(): List<DisplaySleep> {
        // Implement database fetching here
        // This should interact with your database to fetch sleep data
        return emptyList()
    }
}