package dev.eknath.barathtrainbookingremainder.presentation

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import dev.eknath.barathtrainbookingremainder.R

data class BottomNavItem(
    val name: String,
    val route: String,
    val icon: Int
)

@Composable
fun BottomNavBar(navController: NavController) {
    val navItems = listOf(
        BottomNavItem(
            name = "Home",
            route = "home",
            icon = R.drawable.ic_home
        ),
        BottomNavItem(
            name = "Calendar",
            route = "calendar",
            icon = R.drawable.ic_calendar
        )
        // Add more navigation items as needed
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        tonalElevation = 8.dp
    ) {
        navItems.forEach { item ->
            NavigationBarItem(
                icon = { Icon(painterResource(id = item.icon), contentDescription = item.name) },
                label = { Text(item.name) },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}
