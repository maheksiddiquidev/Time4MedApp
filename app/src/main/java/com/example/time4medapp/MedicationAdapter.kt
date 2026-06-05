package com.example.time4medapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MedicationAdapter(private val medications: List<Medication>) :
    RecyclerView.Adapter<MedicationAdapter.MedicationViewHolder>() {

    class MedicationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewDetails: TextView = itemView.findViewById(R.id.textViewMedicationDetails)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_medication, parent, false)
        return MedicationViewHolder(view)
    }

    override fun onBindViewHolder(holder: MedicationViewHolder, position: Int) {
        val medication = medications[position]
        holder.textViewDetails.text = """
            Name: ${medication.name}
            Start Date: ${medication.startDate}
            End Date: ${medication.endDate}
            Time: ${medication.time}
            Notes: ${medication.notes}
            Type: ${medication.type}
        """.trimIndent()
    }

    override fun getItemCount(): Int = medications.size
}
