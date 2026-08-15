package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.example.data.local.AppDatabase
import com.example.data.repository.StoryLocalRepository
import com.example.ui.*
import com.example.ui.theme.WorthWylTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "worthwyl-db").build()
    val repository = StoryLocalRepository(db.storyDao())
    enableEdgeToEdge()
    setContent {
      WorthWylTheme {
        val viewModel: StoryViewModel = viewModel(factory = StoryViewModelFactory(repository))
        WorthWylApp(viewModel)
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorthWylApp(viewModel: StoryViewModel) {
  val navController = rememberNavController()
  val items = listOf(Screen.Forge, Screen.Writer, Screen.Tracker, Screen.Bible, Screen.StyleSync)
  
  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
        @OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
        androidx.compose.material3.TopAppBar(
            title = { com.example.ui.components.CognitiveCoreBranding() }
        )
    },
    bottomBar = {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        if (currentRoute != Screen.Splash.route) {
            NavigationBar {
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Menu, contentDescription = null) },
                        label = { Text(screen.title) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    }
  ) { innerPadding ->
    Box(modifier = Modifier.padding(innerPadding)) {
      MainNavigation(navController = navController, viewModel = viewModel)
    }
  }
}
