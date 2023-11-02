package com.example.myruns.ui.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myruns.R
import com.example.myruns.data.exercise.ExerciseEntry

class ExerciseEntryAdapter(private val exerciseEntries: List<ExerciseEntry>) :
    RecyclerView.Adapter<ExerciseEntryAdapter.ExerciseEntryViewHolder>() {

    class ExerciseEntryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val inputType by lazy { view.findViewById<TextView>(R.id.input_type) }
        val activityType by lazy { view.findViewById<TextView>(R.id.activity_type) }
        val dateTime by lazy { view.findViewById<TextView>(R.id.date_time) }
        val distance by lazy { view.findViewById<TextView>(R.id.distance) }
        val duration by lazy { view.findViewById<TextView>(R.id.duration) }

        init {
            // Define click listener for the ViewHolder's View
        }
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ExerciseEntryViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.history_entry_item, viewGroup, false)
        return ExerciseEntryViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseEntryViewHolder, position: Int) {
        val exerciseEntry = exerciseEntries[position]
        println("setting up history: ${exerciseEntry}")
        holder.inputType.text = exerciseEntry.inputType
        holder.activityType.text = exerciseEntry.activityType.toString()
        holder.dateTime.text = exerciseEntry.dateTime?.time.toString() ?: ""
        holder.distance.text = exerciseEntry.distance?.toString() ?: ""
        holder.duration.text = exerciseEntry.duration?.toString() ?: ""

    }

    override fun getItemCount(): Int {
        return exerciseEntries.size
    }
}
