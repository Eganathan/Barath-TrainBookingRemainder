package dev.eknath.barathtrainbookingremainder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.eknath.barathtrainbookingremainder.presentation.HomeScreen
import dev.eknath.barathtrainbookingremainder.presentation.AddEditReminderScreen
import dev.eknath.barathtrainbookingremainder.presentation.ReminderDetailsScreen
import dev.eknath.barathtrainbookingremainder.ui.theme.BarathTrainBookingRemainderTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.eknath.barathtrainbookingremainder.presentation.ReminderViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BarathTrainBookingRemainderTheme {
                val navController = rememberNavController()
                val reminderViewModel: ReminderViewModel = viewModel()
                
                Scaffold { innerPadding ->
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
                            val reminderId = backStackEntry.arguments?.getString("reminderId")?.toLongOrNull() ?: return@composable
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
                            val reminderId = backStackEntry.arguments?.getString("reminderId")?.toLongOrNull() ?: return@composable
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
                    }
                }
            }
        }
    }
}