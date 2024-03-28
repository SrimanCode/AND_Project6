import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.project5.DisplaySleep
import com.example.project5.R


class SleepDataAdapter(private val listener: OnItemOptionsClickListener) : RecyclerView.Adapter<SleepDataAdapter.ViewHolder>() {
    interface OnItemOptionsClickListener {

        fun onEditClicked(position: Int)
        fun onDeleteClicked(position: Int)
        fun onSaveClicked(position: Int)
    }
    private val sleepDataList = mutableListOf<DisplaySleep>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sleep_data, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sleepData = sleepDataList[position]
        holder.bind(sleepData)
    }

    override fun getItemCount() = sleepDataList.size
    fun addSleepData(sleep: DisplaySleep) {
        sleepDataList.add(sleep)
        notifyItemInserted(sleepDataList.size - 1)
    }
    fun setSleepData(newSleepData: List<DisplaySleep>) {
        sleepDataList.clear()
        sleepDataList.addAll(newSleepData)
        notifyDataSetChanged()
    }
    fun removeItem(position: Int) {
        sleepDataList.removeAt(position)
        notifyItemRemoved(position)
    }
    fun getItem(position: Int): DisplaySleep {
        return sleepDataList[position]
    }

    fun updateItem(position: Int, newData: DisplaySleep) {
        if (position >= 0 && position < sleepDataList.size) {
            sleepDataList[position] = newData
            notifyItemChanged(position)
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val sleepHoursTextView: TextView = view.findViewById(R.id.sleepHoursTextView)
        private val sleepQualityTextView: TextView = view.findViewById(R.id.sleepQualityTextView)
        private val notesTextView: TextView = view.findViewById(R.id.notesTextView)
        private val dateTextView: TextView = view.findViewById(R.id.DateTime)
        private val optionsLayout: LinearLayout = view.findViewById(R.id.optionsLayout)
        private val editButton: Button = view.findViewById(R.id.editButton)
        private val deleteButton: Button = view.findViewById(R.id.deleteButton)

        init {
            view.setOnClickListener {
                // Toggle visibility
                optionsLayout.visibility = if (optionsLayout.visibility == View.GONE) View.VISIBLE else View.GONE
            }

            editButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onEditClicked(position)
                }

            }

            deleteButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onDeleteClicked(position)
                }
            }
        }

        fun bind(sleepData: DisplaySleep) {
            sleepHoursTextView.text = " Slept ${sleepData.time} hours"
            sleepQualityTextView.text = "Feeling ${sleepData.quality} / 10"
            notesTextView.text = sleepData.notes
            dateTextView.text = sleepData.date
        }
    }
}

