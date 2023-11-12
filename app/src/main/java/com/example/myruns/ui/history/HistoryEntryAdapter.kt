package com.example.myruns.ui.history

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.myruns.R
import com.example.myruns.data.exercise.ExerciseEntry
import com.example.myruns.ui.SettingsFragment
import com.example.myruns.ui.StartFragment
import java.io.ByteArrayOutputStream
import java.io.ObjectOutputStream

/**
 * adapter for the RecyclerView in HistoryFragment
 * @param exerciseEntries
 * the list of ExerciseEntries to display
 * @param historyViewModel
 * the view model for the HistoryFragment
 */
class ExerciseEntryAdapter(
    private val exerciseEntries: List<ExerciseEntry>,
    private val historyViewModel: HistoryViewModel
) : RecyclerView.Adapter<ExerciseEntryAdapter.ExerciseEntryViewHolder>() {

    /**
     * view holder for the RecyclerView
     * @param view
     * the view to display
     * @param historyViewModel
     * the view model for the HistoryFragment
     */
    class ExerciseEntryViewHolder(view: View, private val historyViewModel: HistoryViewModel) : RecyclerView.ViewHolder(view) {
        val inputType: TextView by lazy { view.findViewById<TextView>(R.id.input_type) }
        val activityType: TextView by lazy { view.findViewById<TextView>(R.id.activity_type) }
        val dateTime: TextView by lazy { view.findViewById<TextView>(R.id.date_time) }
        val distance: TextView by lazy { view.findViewById<TextView>(R.id.distance) }
        val duration: TextView by lazy { view.findViewById<TextView>(R.id.duration) }

        fun bind(exerciseEntry: ExerciseEntry) {
            // populate TextViews with ExerciseEntry data
            inputType.text = exerciseEntry.inputType
            activityType.text = StartFragment.activityTypeList[exerciseEntry.activityType]
            dateTime.text = exerciseEntry.dateTime?.time.toString()
            
            val mins = (exerciseEntry.duration?.toInt()?.toString() ?: "0") + " mins"
            // get decimal part of the duration
            val secs = ((exerciseEntry.duration?.rem(1.0) ?: 0.0) * 60).toInt().toString() + " secs"
            duration.text = mins + " " + secs

            // observer for the unit preference
            historyViewModel.unitPreference.observe(itemView.context as LifecycleOwner) { unitPref ->
                if (unitPref == SettingsFragment.UNIT_METRIC) {
                    distance.text = (exerciseEntry.distance?.toString() ?: "0") + " kilometers"
                } else {
                    distance.text = (exerciseEntry.distance?.toString() ?: "0") + " miles"
                }
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ExerciseEntryViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.history_entry_item, viewGroup, false)
        return ExerciseEntryViewHolder(view, historyViewModel)
    }

    override fun onBindViewHolder(holder: ExerciseEntryViewHolder, position: Int) {
        val exerciseEntry = exerciseEntries[position]
        holder.bind(exerciseEntry)
        
        // click listener that opens HistoryEntryActivity
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, HistoryEntryActivity::class.java)
            // sends a byte array of the ExerciseEntry to the HistoryEntryActivity
            val byteArray = ByteArrayOutputStream()
            val out = ObjectOutputStream(byteArray)
            out.writeObject(exerciseEntry)
            out.close()
            intent.putExtra(HistoryEntryActivity.EXERCISE_ENTRY, byteArray.toByteArray())
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return exerciseEntries.size
    }
}
