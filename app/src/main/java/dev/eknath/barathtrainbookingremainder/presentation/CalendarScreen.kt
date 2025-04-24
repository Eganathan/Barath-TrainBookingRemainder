package dev.eknath.barathtrainbookingremainder.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    navController: NavController,
    navigateBack: () -> Unit
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now().plusDays(61)) }
    var bookableDate by remember { mutableStateOf(LocalDate.now()) }

    val calendarState = rememberUseCaseState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Booking Calculator",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Selected date display
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable(onClick = { calendarState.show() })
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Train Start Date:",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = selectedDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy")),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bookable date display
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Bookable Date (60 days later at 08:30 AM):",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "${bookableDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))} at 08:00 AM",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { calendarState.show() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Select Different Date")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                // Navigate to reminder creation screen with the selected date as parameter
                navController.navigate(
                    "add_reminder?date=${selectedDate}&bookableDate=${bookableDate}"
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Text("Create Reminder for Selected Date")
        }
    }

    // Calendar Dialog
    CalendarDialog(
        state = calendarState,
        config = CalendarConfig(
            monthSelection = true,
            yearSelection = true,
            boundary = LocalDate.now().plusDays(61)..LocalDate.now().plusDays(465)
        ),
        selection = CalendarSelection.Date(selectedDate = selectedDate) { date ->
            selectedDate = date
            bookableDate = date.plusDays(61)
        }
    )
}