package com.example.myruns.ui.history

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myruns.R
import com.example.myruns.data.exercise.ExerciseDatabase
import com.example.myruns.data.exercise.ExerciseRepository
import com.example.myruns.ui.ExerciseViewModelFactory
import com.example.myruns.ui.SettingsFragment

/**
 * fragment that displays the history screen with all the user's exercise entries
 */
class HistoryFragment : Fragment() {
    private lateinit var historyViewModel: HistoryViewModel
    private val sharedPreferences by lazy { PreferenceManager.getDefaultSharedPreferences(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val _binding = inflater.inflate(R.layout.fragment_history, container, false)
        val root = _binding.rootView

        // setup mvvm objects
        var database = ExerciseDatabase.getInstance(root.context)
        var databaseDao = database.exerciseDatabaseDao
        var repository = ExerciseRepository(databaseDao)
        var viewModelFactory = ExerciseViewModelFactory(repository)
        historyViewModel = ViewModelProvider(
            this,
            viewModelFactory
        )[HistoryViewModel::class.java]

        // setup recycler view for the exercise entries list
        val recyclerView = root.findViewById<RecyclerView>(R.id.history_enries)
        val layoutManager = LinearLayoutManager(root.context)
        recyclerView.layoutManager = layoutManager
        historyViewModel.exerciseEntries.observe(viewLifecycleOwner) {
            println("setting up history: start")
            val adapter = ExerciseEntryAdapter(it, historyViewModel)
            recyclerView.adapter = adapter
        }

        return root
    }

    /**
     * updates the units every time the fragment is resumed
     */
    override fun onResume() {
        super.onResume()

        // update the units every time fragment is loaded
        val unit = sharedPreferences
            .getString(
                SettingsFragment.UNIT_PREFERENCE,
                SettingsFragment.UNIT_METRIC
            )
        historyViewModel.unitPreference.value = unit
        Log.d("HistoryFragment", "UNIT_PREFERENCE changed to $unit")
    }

}