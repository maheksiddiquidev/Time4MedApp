package com.example.time4medapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HistoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        recyclerView = findViewById(R.id.recyclerViewHistory)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val database = AppDatabase.getDatabase(this)

        adapter = HistoryAdapter(
            emptyList(),
            lifecycleScope,
            database
        )

        recyclerView.adapter = adapter

        observeHistory()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun observeHistory() {
        lifecycleScope.launch {
            AppDatabase.getDatabase(this@HistoryActivity)
                .historyDao()
                .getAllHistory()
                .collectLatest { historyList ->
                    adapter.updateData(historyList)
                }
        }
    }
}