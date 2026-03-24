package app.krafted.towerjigsaw

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import app.krafted.towerjigsaw.ui.theme.TowerJigsawTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TowerJigsawTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TowerJigsawNavHost(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun TowerJigsawNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val transitionDuration = 280

    NavHost(
        navController = navController,
        startDestination = "splash",
        modifier = modifier,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(transitionDuration)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(transitionDuration)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(transitionDuration)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(transitionDuration)
            )
        }
    ) {
        composable("splash") {
            PlaceholderScreen(label = "Splash")
        }

        composable("home") {
            PlaceholderScreen(label = "Home")
        }

        composable(
            route = "puzzle_select/{puzzleId}",
            arguments = listOf(
                navArgument("puzzleId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val puzzleId = backStackEntry.arguments?.getString("puzzleId").orEmpty()
            PlaceholderScreen(label = "Puzzle Select (puzzleId=$puzzleId)")
        }

        composable(
            route = "puzzle/{puzzleId}/{difficulty}/{isTimedMode}",
            arguments = listOf(
                navArgument("puzzleId") { type = NavType.StringType },
                navArgument("difficulty") { type = NavType.StringType },
                navArgument("isTimedMode") { type = NavType.BoolType }
            )
        ) { backStackEntry ->
            val puzzleId = backStackEntry.arguments?.getString("puzzleId").orEmpty()
            val difficulty = backStackEntry.arguments?.getString("difficulty").orEmpty()
            val isTimedMode = backStackEntry.arguments?.getBoolean("isTimedMode") ?: false
            PlaceholderScreen(
                label = "Puzzle (puzzleId=$puzzleId, difficulty=$difficulty, timed=$isTimedMode)"
            )
        }

        composable(
            route = "complete/{puzzleId}/{difficulty}/{score}/{stars}/{timeMs}",
            arguments = listOf(
                navArgument("puzzleId") { type = NavType.StringType },
                navArgument("difficulty") { type = NavType.StringType },
                navArgument("score") { type = NavType.IntType },
                navArgument("stars") { type = NavType.IntType },
                navArgument("timeMs") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val puzzleId = backStackEntry.arguments?.getString("puzzleId").orEmpty()
            val difficulty = backStackEntry.arguments?.getString("difficulty").orEmpty()
            val score = backStackEntry.arguments?.getInt("score") ?: 0
            val stars = backStackEntry.arguments?.getInt("stars") ?: 0
            val timeMs = backStackEntry.arguments?.getLong("timeMs") ?: 0L
            PlaceholderScreen(
                label = "Complete (puzzle=$puzzleId, difficulty=$difficulty, score=$score, stars=$stars, time=${timeMs}ms)"
            )
        }

        composable("leaderboard") {
            PlaceholderScreen(label = "Leaderboard")
        }
    }
}

@Composable
fun PlaceholderScreen(label: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = label)
    }
}
