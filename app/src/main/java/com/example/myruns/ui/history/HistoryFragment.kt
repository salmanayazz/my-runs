package com.example.myruns.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myruns.R
import com.example.myruns.data.exercise.ExerciseDatabase
import com.example.myruns.data.exercise.ExerciseRepository
import com.example.myruns.ui.ExerciseViewModelFactory

/**
 * A simple [Fragment] subclass.
 * Use the [HistoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HistoryFragment : Fragment() {
    private lateinit var historyViewModel: HistoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val _binding = inflater.inflate(R.layout.fragment_history, container, false)
        val root = _binding.rootView

        var database = ExerciseDatabase.getInstance(root.context)
        var databaseDao = database.exerciseDatabaseDao
        var repository = ExerciseRepository(databaseDao)
        var viewModelFactory = ExerciseViewModelFactory(repository)
        historyViewModel = ViewModelProvider(
            this,
            viewModelFactory
        )[HistoryViewModel::class.java]

        val recyclerView = root.findViewById<RecyclerView>(R.id.history_enries)
        val layoutManager = LinearLayoutManager(root.context)
        recyclerView.layoutManager = layoutManager
        historyViewModel.exerciseEntries.observe(viewLifecycleOwner) {
            println("setting up history: start")
            val adapter = ExerciseEntryAdapter(it)
            recyclerView.adapter = adapter
        }

        return root
    }
}