package dev.eknath.barathtrainbookingremainder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jakewharton.threetenabp.AndroidThreeTen
import dev.eknath.barathtrainbookingremainder.data.Reminder
import dev.eknath.barathtrainbookingremainder.presentation.AddEditReminderScreen
import dev.eknath.barathtrainbookingremainder.presentation.BottomNavBar
import dev.eknath.barathtrainbookingremainder.presentation.CalendarScreen
import dev.eknath.barathtrainbookingremainder.presentation.DebugScreen
import dev.eknath.barathtrainbookingremainder.presentation.HomeScreen
import dev.eknath.barathtrainbookingremainder.presentation.ReminderDetailsScreen
import dev.eknath.barathtrainbookingremainder.presentation.ReminderViewModel
import dev.eknath.barathtrainbookingremainder.ui.theme.BarathTrainBookingRemainderTheme
import org.threeten.bp.LocalDate
import java.util.Calendar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidThreeTen.init(this)
        enableEdgeToEdge()
        setContent {
            BarathTrainBookingRemainderTheme {
                val navController = rememberNavController()
                val reminderViewModel: ReminderViewModel = viewModel()
                val reminders by reminderViewModel.reminders.collectAsState(initial = emptyList())

                Scaffold(
                    bottomBar = {
                        BottomNavBar(navController = navController)
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "add",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("home") {
                            HomeScreen(
                                reminders = reminders,
                                onReminderClick = { reminderId ->
                                    navController.navigate("details/$reminderId")
                                },
                                onAddReminderClick = {
                                    navController.navigate("add")
                                },
                                onDeleteReminder = { reminderId ->
                                    reminderViewModel.deleteReminder(reminderId)
                                }
                            )
                        }
                        composable("add") {
                            AddEditReminderScreen(
                                onSave = { reminder ->
                                    reminderViewModel.addReminder(reminder)
                                    navController.popBackStack()
                                },
                                onCancel = {
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable("edit/{reminderId}") { backStackEntry ->
                            val reminderId =
                                backStackEntry.arguments?.getString("reminderId")?.toLongOrNull()
                                    ?: return@composable

                            var reminder by remember { mutableStateOf<Reminder?>(null) }

                            LaunchedEffect(reminderId) {
                                reminder = reminderViewModel.getReminderById(reminderId)
                            }

                            if (reminder != null) {
                                AddEditReminderScreen(
                                    reminder = reminder,
                                    onSave = { updatedReminder ->
                                        reminderViewModel.updateReminder(updatedReminder)
                                        navController.popBackStack()
                                    },
                                    onCancel = {
                                        navController.popBackStack()
                                    }
                                )
                            }
                        }
                        composable("details/{reminderId}") { backStackEntry ->
                            val reminderId =
                                backStackEntry.arguments?.getString("reminderId")?.toLongOrNull()
                                    ?: return@composable

                            var reminder by remember { mutableStateOf<Reminder?>(null) }

                            LaunchedEffect(reminderId) {
                                reminder = reminderViewModel.getReminderById(reminderId)
                            }

                            if (reminder != null) {
                                ReminderDetailsScreen(
                                    reminder = reminder,
                                    onEditClick = {
                                        navController.navigate("edit/$reminderId")
                                    },
                                    onBackClick = {
                                        navController.popBackStack()
                                    }
                                )
                            }
                        }

                        composable("calendar") {
                            CalendarScreen(
                                navController = navController,
                                navigateBack = { navController.popBackStack() }
                            )
                        }

                        composable("debug") {
                            DebugScreen(navController = navController)
                        }

                        composable(
                            route = "add_reminder?date={date}&bookableDate={bookableDate}",
                            arguments = listOf(
                                navArgument("date") { 
                                    type = NavType.StringType 
                                    nullable = true
                                    defaultValue = null
                                },
                                navArgument("bookableDate") { 
                                    type = NavType.StringType 
                                    nullable = true
                                    defaultValue = null
                                }
                            )
                        ) { backStackEntry ->
                            val dateString = backStackEntry.arguments?.getString("date")
                            val bookableDateString = backStackEntry.arguments?.getString("bookableDate")

                            val selectedDate = parseLocalDate(dateString, 60)
                            val bookableDate = parseLocalDate(bookableDateString, 90)

                            // Convert the bookable date to Date for use in addReminder
                            val bookableDateAsDate = localDateToDate(bookableDate)

                            AddEditReminderScreen(
                                preselectedDate = selectedDate,
                                onSave = { reminder ->
                                    val reminderWithBookableDate = reminder.copy(
                                        bookableDate = bookableDateAsDate
                                    )
                                    reminderViewModel.addReminder(reminderWithBookableDate)
                                    navController.navigateUp()
                                },
                                onCancel = { navController.navigateUp() }
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun parseLocalDate(dateString: String?, defaultDaysToAdd: Long): LocalDate {
    if (dateString.isNullOrEmpty()) {
        return LocalDate.now().plusDays(defaultDaysToAdd)
    }

    return LocalDate.parse(dateString)
}

private fun localDateToDate(localDate: LocalDate): java.util.Date {
    val calendar = Calendar.getInstance()
    calendar.set(
        localDate.year,
        localDate.monthValue - 1,
        localDate.dayOfMonth
    )
    return calendar.time
}
