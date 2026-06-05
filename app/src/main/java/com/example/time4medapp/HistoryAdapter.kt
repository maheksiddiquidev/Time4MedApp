package com.example.time4medapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HistoryAdapter(
    private var historyList: List<HistoryEntity>,
    private val lifecycleScope: LifecycleCoroutineScope,
    private val database: AppDatabase
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvMedicineName: TextView = itemView.findViewById(R.id.tvMedicineName)
        val tvDosage: TextView = itemView.findViewById(R.id.tvDosage)
        val tvScheduledTime: TextView = itemView.findViewById(R.id.tvScheduledTime)
        val tvActualTime: TextView = itemView.findViewById(R.id.tvActualTime)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val tvNotes: TextView = itemView.findViewById(R.id.tvNotes)

        val btnTaken: Button = itemView.findViewById(R.id.btnTaken)
        val btnMissed: Button = itemView.findViewById(R.id.btnMissed)
        val ivMore: ImageView = itemView.findViewById(R.id.ivMore)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {

        val item = historyList[position]

        holder.tvMedicineName.text = item.medicineName
        holder.tvDosage.text = item.dosage
        holder.tvScheduledTime.text = "Scheduled: ${item.scheduledDateTime}"

        holder.tvActualTime.text =
            if (item.actualDateTime != null)
                "Actual: ${item.actualDateTime}"
            else
                "Actual: --"

        holder.tvStatus.text = item.status
        holder.tvNotes.text = "Notes: ${item.notes ?: "No Notes"}"

        when (item.status) {
            "Taken" -> holder.tvStatus.setBackgroundColor(
                holder.itemView.context.getColor(R.color.status_taken)
            )
            "Missed" -> holder.tvStatus.setBackgroundColor(
                holder.itemView.context.getColor(R.color.status_missed)
            )
            else -> holder.tvStatus.setBackgroundColor(
                holder.itemView.context.getColor(R.color.status_scheduled)
            )
        }

        holder.btnTaken.setOnClickListener {
            lifecycleScope.launch {
                val currentDateTime = getCurrentDateTime()
                database.historyDao().updateStatusAndTime(
                    id = item.id,
                    newStatus = "Taken",
                    actualDateTime = currentDateTime
                )
            }
        }

        holder.btnMissed.setOnClickListener {
            lifecycleScope.launch {
                database.historyDao().updateStatusAndTime(
                    id = item.id,
                    newStatus = "Missed",
                    actualDateTime = null
                )
            }
        }

        holder.ivMore.setOnClickListener { view ->

            val popup = PopupMenu(view.context, view)
            popup.menu.add("Edit")
            popup.menu.add("Delete")

            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.title) {

                    "Delete" -> {
                        lifecycleScope.launch {
                            database.historyDao().deleteById(item.id)
                        }
                        true
                    }

                    "Edit" -> {

                        val context = view.context
                        val intent = Intent(context, AddMedicationActivity::class.java)

                        val parts = item.scheduledDateTime.split(" ")

                        val startDate = parts[0]
                        val time = parts[1]

                        intent.putExtra("historyId", item.id)
                        intent.putExtra("medicineName", item.medicineName)
                        intent.putExtra("startDate", startDate)
                        intent.putExtra("time", time)
                        intent.putExtra("type", item.dosage)
                        intent.putExtra("notes", item.notes)

                        context.startActivity(intent)
                        true
                    }

                    else -> false
                }
            }

            popup.show()
        }
    }

    override fun getItemCount(): Int = historyList.size

    fun updateData(newList: List<HistoryEntity>) {
        historyList = newList
        notifyDataSetChanged()
    }

    private fun getCurrentDateTime(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date())
    }
}