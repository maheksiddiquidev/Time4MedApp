package com.example.time4medapp

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.launch
import java.util.*

class AddMedicationActivity : AppCompatActivity() {

    private lateinit var editTextMedicineName: EditText
    private lateinit var editTextStartDate: EditText
    private lateinit var editTextEndDate: EditText
    private lateinit var editTextTime: EditText
    private lateinit var editTextNotes: EditText
    private lateinit var medicineTypeContainer: LinearLayout

    private var selectedMedicineType: String? = null
    private var isEditMode = false
    private var historyId: Int = -1
    private var oldRequestCode: Int = 0

    private val medicineTypes = listOf(
        Pair("Tablet", R.drawable.ic_tablet),
        Pair("Syrup", R.drawable.ic_syrup),
        Pair("Cream", R.drawable.ic_cream)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_medication)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.hide()

        editTextMedicineName = findViewById(R.id.editTextMedicineName)
        editTextStartDate = findViewById(R.id.editTextStartDate)
        editTextEndDate = findViewById(R.id.editTextEndDate)
        editTextTime = findViewById(R.id.editTextTime)
        editTextNotes = findViewById(R.id.editTextNotes)
        medicineTypeContainer = findViewById(R.id.medicineTypeContainer)

        setupDatePicker(editTextStartDate)
        setupDatePicker(editTextEndDate)
        setupTimePicker(editTextTime)
        loadMedicineTypeCards()

        if (intent.hasExtra("historyId")) {
            isEditMode = true
            historyId = intent.getIntExtra("historyId", -1)

            editTextMedicineName.setText(intent.getStringExtra("medicineName"))
            editTextStartDate.setText(intent.getStringExtra("startDate"))
            editTextTime.setText(intent.getStringExtra("time"))
            editTextNotes.setText(intent.getStringExtra("notes"))

            selectedMedicineType = intent.getStringExtra("type")
            highlightSelectedCardByType(selectedMedicineType)

            oldRequestCode = editTextMedicineName.text.toString().hashCode()
        }

        findViewById<Button>(R.id.buttonDone).setOnClickListener {
            if (isEditMode) {
                updateMedication()
            } else {
                saveMedication()
            }
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.navigation_home

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.navigation_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }

                R.id.navigation_history -> {
                    startActivity(Intent(this, HistoryActivity::class.java))
                    finish()
                    true
                }

                else -> false
            }
        }
    }

    private fun setupDatePicker(editText: EditText) {
        editText.setOnClickListener {
            val calendar = Calendar.getInstance()

            DatePickerDialog(
                this,
                { _, year, month, day ->
                    editText.setText("$day/${month + 1}/$year")
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    //  CLOCK TIME PICKER
    private fun setupTimePicker(editText: EditText) {

        editText.setOnClickListener {

            val calendar = Calendar.getInstance()

            val picker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(calendar.get(Calendar.HOUR_OF_DAY))
                .setMinute(calendar.get(Calendar.MINUTE))
                .setTitleText("Select Time")
                .build()

            picker.show(supportFragmentManager, "TIME_PICKER")

            picker.addOnPositiveButtonClickListener {

                val hour = picker.hour
                val minute = picker.minute

                editText.setText(String.format("%02d:%02d", hour, minute))
            }
        }
    }

    private fun loadMedicineTypeCards() {

        medicineTypeContainer.removeAllViews()

        for ((label, iconRes) in medicineTypes) {

            val card = layoutInflater.inflate(
                R.layout.item_medicine_type_card,
                medicineTypeContainer,
                false
            ) as CardView

            val imageView = card.findViewById<ImageView>(R.id.imageViewType)
            val textView = card.findViewById<TextView>(R.id.textViewType)

            imageView.setImageResource(iconRes)
            textView.text = label

            card.setOnClickListener {
                selectedMedicineType = label
                highlightSelectedCard(card)
            }

            medicineTypeContainer.addView(card)
        }
    }

    private fun highlightSelectedCard(selectedCard: CardView) {

        for (i in 0 until medicineTypeContainer.childCount) {

            val card = medicineTypeContainer.getChildAt(i) as CardView

            card.setCardBackgroundColor(
                getColor(
                    if (card == selectedCard)
                        R.color.teal_700
                    else
                        android.R.color.darker_gray
                )
            )
        }
    }

    private fun highlightSelectedCardByType(type: String?) {

        for (i in 0 until medicineTypeContainer.childCount) {

            val card = medicineTypeContainer.getChildAt(i) as CardView
            val textView = card.findViewById<TextView>(R.id.textViewType)

            if (textView.text.toString() == type) {
                highlightSelectedCard(card)
                break
            }
        }
    }

    private fun saveMedication() {

        val name = editTextMedicineName.text.toString()
        val startDate = editTextStartDate.text.toString()
        val time = editTextTime.text.toString()
        val notes = editTextNotes.text.toString()
        val type = selectedMedicineType

        if (name.isEmpty() || startDate.isEmpty() || time.isEmpty() || type == null) {

            ToastUtils.showCustomToast(this, "Please Fill All Required Fields!")
            return
        }

        lifecycleScope.launch {

            val scheduledDateTime = "$startDate $time"

            val historyItem = HistoryEntity(
                id = 0,
                medicineName = name,
                dosage = type,
                scheduledDateTime = scheduledDateTime,
                actualDateTime = null,
                status = "Scheduled",
                notes = notes
            )

            val insertedId = AppDatabase.getDatabase(this@AddMedicationActivity)
                .historyDao()
                .insertHistory(historyItem)

            val requestCode = name.hashCode()

            scheduleExactAlarm(time, requestCode, insertedId.toInt())

            ToastUtils.showCustomToast(
                this@AddMedicationActivity,
                "Medication Added Successfully!"
            )

            startActivity(Intent(this@AddMedicationActivity, HistoryActivity::class.java))
            finish()
        }
    }

    private fun updateMedication() {

        val name = editTextMedicineName.text.toString()
        val startDate = editTextStartDate.text.toString()
        val time = editTextTime.text.toString()
        val notes = editTextNotes.text.toString()
        val type = selectedMedicineType

        val newRequestCode = name.hashCode()

        cancelAlarm(oldRequestCode)
        scheduleExactAlarm(time, newRequestCode, historyId)

        lifecycleScope.launch {

            val updatedItem = HistoryEntity(
                id = historyId,
                medicineName = name,
                dosage = type!!,
                scheduledDateTime = "$startDate $time",
                actualDateTime = null,
                status = "Scheduled",
                notes = notes
            )

            AppDatabase.getDatabase(this@AddMedicationActivity)
                .historyDao()
                .updateHistory(updatedItem)

            ToastUtils.showCustomToast(
                this@AddMedicationActivity,
                "Medication Updated Successfully"
            )

            startActivity(Intent(this@AddMedicationActivity, HistoryActivity::class.java))
            finish()
        }
    }

    private fun cancelAlarm(requestCode: Int) {

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleExactAlarm(
        timeString: String,
        requestCode: Int,
        historyId: Int
    ){

        val parts = timeString.split(":")
        val hour = parts[0].toInt()
        val minute = parts[1].toInt()

        val calendar = Calendar.getInstance()

        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)

        if (calendar.timeInMillis <= System.currentTimeMillis()) {

            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        val intent = Intent(this, AlarmReceiver::class.java)

        intent.putExtra("medicineName", editTextMedicineName.text.toString())
        intent.putExtra("time", editTextTime.text.toString())
        intent.putExtra("historyId", historyId)

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }
}