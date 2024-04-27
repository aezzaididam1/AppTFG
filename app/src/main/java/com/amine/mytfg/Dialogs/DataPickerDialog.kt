package com.amine.mytfg.dialogs

import android.app.DatePickerDialog
import android.content.Context
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Locale

class DatePickerDialog(val context: Context, val onDateSet: (String) -> Unit) {
    private val dateFormatter = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())

    fun show() {
        // Obtener la fecha actual para preseleccionarla en el DatePicker
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)
                val formattedDate = dateFormatter.format(selectedDate.time)
                onDateSet(formattedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}
