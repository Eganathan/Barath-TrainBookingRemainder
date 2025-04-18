package dev.eknath.barathtrainbookingremainder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.eknath.barathtrainbookingremainder.presentation.HomeScreen
import dev.eknath.barathtrainbookingremainder.presentation.AddEditReminderScreen
import dev.eknath.barathtrainbookingremainder.presentation.ReminderDetailsScreen
import dev.eknath.barathtrainbookingremainder.ui.theme.BarathTrainBookingRemainderTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.jakewharton.threetenabp.AndroidThreeTen
import dev.eknath.barathtrainbookingremainder.presentation.BottomNavBar
import dev.eknath.barathtrainbookingremainder.presentation.CalendarScreen
import dev.eknath.barathtrainbookingremainder.presentation.ReminderViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidThreeTen.init(this)
        enableEdgeToEdge()
        setContent {
            BarathTrainBookingRemainderTheme {
                val navController = rememberNavController()
                val reminderViewModel: ReminderViewModel = viewModel()

                Scaffold(
                    bottomBar = {
                        BottomNavBar(navController = navController)
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("home") {
                            HomeScreen(
                                reminders = reminderViewModel.reminders,
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
                            val reminder = reminderViewModel.getReminder(reminderId)
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
                        composable("details/{reminderId}") { backStackEntry ->
                            val reminderId =
                                backStackEntry.arguments?.getString("reminderId")?.toLongOrNull()
                                    ?: return@composable
                            val reminder = reminderViewModel.getReminder(reminderId)
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

                        composable("calendar") {
                            CalendarScreen(
                                navController = navController,
                                navigateBack = { navController.popBackStack() }
                            )
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

                            AddEditReminderScreen(
                                preselectedDate = selectedDate,
                                onSave = { newReminder ->
                                    reminderViewModel.addReminder(newReminder)
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

private fun parseLocalDate(dateString: String?, defaultDaysToAdd: Long): org.threeten.bp.LocalDate {
    if (dateString.isNullOrEmpty()) {
        return org.threeten.bp.LocalDate.now().plusDays(defaultDaysToAdd)
    }

    return org.threeten.bp.LocalDate.parse(dateString)
}
