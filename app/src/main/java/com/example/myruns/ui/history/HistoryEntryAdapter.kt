package com.example.myruns.ui.history

import android.content.Context
import android.content.Intent
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.example.myruns.R
import com.example.myruns.data.exercise.ExerciseEntry
import java.io.ByteArrayOutputStream
import java.io.ObjectOutputStream
import java.io.Serializable

class ExerciseEntryAdapter(private val exerciseEntries: List<ExerciseEntry>) :
    RecyclerView.Adapter<ExerciseEntryAdapter.ExerciseEntryViewHolder>() {

    class ExerciseEntryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val inputType: TextView by lazy { view.findViewById<TextView>(R.id.input_type) }
        val activityType: TextView by lazy { view.findViewById<TextView>(R.id.activity_type) }
        val dateTime: TextView by lazy { view.findViewById<TextView>(R.id.date_time) }
        val distance: TextView by lazy { view.findViewById<TextView>(R.id.distance) }
        val duration: TextView by lazy { view.findViewById<TextView>(R.id.duration) }

        fun bind(exerciseEntry: ExerciseEntry) {
            inputType.text = exerciseEntry.inputType
            activityType.text = exerciseEntry.activityType.toString()
            dateTime.text = exerciseEntry.dateTime?.time.toString() ?: ""
            distance.text = exerciseEntry.distance?.toString() ?: ""
            duration.text = exerciseEntry.duration?.toString() ?: ""
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ExerciseEntryViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.history_entry_item, viewGroup, false)
        return ExerciseEntryViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseEntryViewHolder, position: Int) {
        val exerciseEntry = exerciseEntries[position]
        holder.bind(exerciseEntry)

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, HistoryEntryActivity::class.java)
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
