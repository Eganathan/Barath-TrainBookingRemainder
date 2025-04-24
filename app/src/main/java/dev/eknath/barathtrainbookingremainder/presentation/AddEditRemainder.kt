package dev.eknath.barathtrainbookingremainder.presentation

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dev.eknath.barathtrainbookingremainder.data.Reminder
import org.threeten.bp.LocalDate
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditReminderScreen(
    reminder: Reminder? = null,
    preselectedDate: LocalDate? = null,
    onSave: (Reminder) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val isEditing = reminder != null

    // Convert preselected LocalDate to Date if provided
    val initialDate = if (preselectedDate != null) {
        val calendar = Calendar.getInstance()
        calendar.set(
            preselectedDate.year,
            preselectedDate.monthValue - 1,
            preselectedDate.dayOfMonth
        )
        calendar.time
    } else {
        reminder?.departureDate ?: Date()
    }

    // Calculate bookable date (90 days before departure or custom value)
    val initialBookableDate = reminder?.bookableDate ?: run {
        val bookableCal = Calendar.getInstance()
        bookableCal.time = initialDate
        bookableCal.add(Calendar.DAY_OF_YEAR, -90) // Default 90 days before
        bookableCal.time
    }

    var trainNumber by remember { mutableStateOf(reminder?.trainNumber ?: "") }
    var fromStation by remember { mutableStateOf(reminder?.fromStation ?: "") }
    var toStation by remember { mutableStateOf(reminder?.toStation ?: "") }
    var departureDate by remember { mutableStateOf(initialDate) }
    var bookableDate by remember { mutableStateOf(initialBookableDate) }
    var departureTime by remember { mutableStateOf(reminder?.departureTime ?: "08:00 AM") }
    var notes by remember { mutableStateOf(reminder?.notes ?: "") }
    var isAlarmSet by remember { mutableStateOf(reminder?.isAlarmSet ?: false) }

    val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val formattedDate = dateFormatter.format(departureDate)
    val formattedBookableDate = dateFormatter.format(bookableDate)

    val departureDatePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            departureDate = calendar.time
        },
        Calendar.getInstance().get(Calendar.YEAR),
        Calendar.getInstance().get(Calendar.MONTH),
        Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    )

    val bookableDatePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            bookableDate = calendar.time
        },
        Calendar.getInstance().get(Calendar.YEAR),
        Calendar.getInstance().get(Calendar.MONTH),
        Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Edit Reminder" else "Add Reminder") },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = trainNumber,
                onValueChange = { trainNumber = it },
                label = { Text("Train Name or Title") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Journey Details:", modifier = Modifier.offset(y = 10.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = fromStation,
                    onValueChange = { fromStation = it },
                    label = { Text("From") },
                    modifier = Modifier.weight(0.5f)
                )

                Spacer(modifier = Modifier.width(5.dp))

                OutlinedTextField(
                    value = toStation,
                    onValueChange = { toStation = it },
                    label = { Text("To") },
                    modifier = Modifier.weight(0.5f)
                )
            }

            // Departure date picker field
            OutlinedTextField(
                value = formattedDate,
                onValueChange = {},
                label = { Text("Departure Date") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { departureDatePickerDialog.show() },
                readOnly = true,
                trailingIcon = {
                    Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                }
            )

            Text(text = "Bookable Date: " + formattedBookableDate + " at 08:00 AM")

            OutlinedTextField(
                value = departureTime,
                onValueChange = { departureTime = it },
                label = { Text("Departure Time (e.g., 08:30 AM)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes (Optional)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Set Reminder Alarm")
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                    checked = isAlarmSet,
                    onCheckedChange = { isAlarmSet = it }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    onSave(
                        Reminder(
                            id = reminder?.id ?: 0,
                            trainNumber = trainNumber,
                            fromStation = fromStation,
                            toStation = toStation,
                            departureDate = departureDate,
                            departureTime = departureTime,
                            notes = notes,
                            isAlarmSet = isAlarmSet,
                            bookableDate = bookableDate
                        )
                    )
                },
                enabled = trainNumber.isNotBlank() && fromStation.isNotBlank() && toStation.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(if (isEditing) "Update" else "Save")
            }
        }
    }
}