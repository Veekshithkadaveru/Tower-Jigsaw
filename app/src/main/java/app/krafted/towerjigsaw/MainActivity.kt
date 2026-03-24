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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import app.krafted.towerjigsaw.ui.HomeScreen
import app.krafted.towerjigsaw.ui.LeaderboardScreen
import app.krafted.towerjigsaw.ui.PuzzleSelectScreen
import app.krafted.towerjigsaw.ui.theme.TowerJigsawTheme
import app.krafted.towerjigsaw.viewmodel.HomeViewModel
import app.krafted.towerjigsaw.viewmodel.LeaderboardViewModel
import app.krafted.towerjigsaw.viewmodel.PuzzleSelectViewModel
import kotlinx.coroutines.delay

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
    
    val homeViewModel: HomeViewModel = viewModel()
    val isTimedMode by homeViewModel.isTimedMode.collectAsState()

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
            LaunchedEffect(Unit) {
                delay(1500)
                navController.navigate("home") {
                    popUpTo("splash") { inclusive = true }
                }
            }
            PlaceholderScreen(label = "🏰 TOWER JIGSAW")
        }

        composable("home") {
            val completedKeys by homeViewModel.completedKeys.collectAsState()

            HomeScreen(
                isTimedMode = isTimedMode,
                completedKeys = completedKeys,
                onPuzzleSelected = { puzzleId ->
                    navController.navigate("puzzle_select/$puzzleId")
                },
                onToggleMode = { homeViewModel.toggleMode() },
                onLeaderboardClick = { navController.navigate("leaderboard") }
            )
        }

        composable(
            route = "puzzle_select/{puzzleId}",
            arguments = listOf(
                navArgument("puzzleId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val puzzleId = backStackEntry.arguments?.getInt("puzzleId") ?: 1
            
            val puzzleSelectViewModel: PuzzleSelectViewModel = viewModel()
            val completedKeys by puzzleSelectViewModel.completedKeys.collectAsState()
            val bestResults by puzzleSelectViewModel.bestResults.collectAsState()
            
            LaunchedEffect(puzzleId) {
                puzzleSelectViewModel.loadBestResults(puzzleId)
            }
            
            PuzzleSelectScreen(
                puzzleId = puzzleId,
                isTimedMode = isTimedMode,
                completedKeys = completedKeys,
                bestResults = bestResults,
                onDifficultySelected = { difficulty ->
                    navController.navigate("puzzle/$puzzleId/${difficulty.name}/$isTimedMode")
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "puzzle/{puzzleId}/{difficulty}/{isTimedMode}",
            arguments = listOf(
                navArgument("puzzleId") { type = NavType.IntType },
                navArgument("difficulty") { type = NavType.StringType },
                navArgument("isTimedMode") { type = NavType.BoolType }
            )
        ) { backStackEntry ->
            val puzzleId = backStackEntry.arguments?.getInt("puzzleId") ?: 1
            val difficulty = backStackEntry.arguments?.getString("difficulty").orEmpty()
            val isTimedMode = backStackEntry.arguments?.getBoolean("isTimedMode") ?: false
            PlaceholderScreen(
                label = "Puzzle (puzzleId=$puzzleId, difficulty=$difficulty, timed=$isTimedMode)"
            )
        }

        composable(
            route = "complete/{puzzleId}/{difficulty}/{score}/{stars}/{timeMs}",
            arguments = listOf(
                navArgument("puzzleId") { type = NavType.IntType },
                navArgument("difficulty") { type = NavType.StringType },
                navArgument("score") { type = NavType.IntType },
                navArgument("stars") { type = NavType.IntType },
                navArgument("timeMs") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val puzzleId = backStackEntry.arguments?.getInt("puzzleId") ?: 1
            val difficulty = backStackEntry.arguments?.getString("difficulty").orEmpty()
            val score = backStackEntry.arguments?.getInt("score") ?: 0
            val stars = backStackEntry.arguments?.getInt("stars") ?: 0
            val timeMs = backStackEntry.arguments?.getLong("timeMs") ?: 0L
            PlaceholderScreen(
                label = "Complete (puzzle=$puzzleId, difficulty=$difficulty, score=$score, stars=$stars, time=${timeMs}ms)"
            )
        }

        composable("leaderboard") {
            val leaderboardViewModel: LeaderboardViewModel = viewModel()
            val selectedPuzzleId by leaderboardViewModel.selectedPuzzleId.collectAsState()
            val selectedDifficulty by leaderboardViewModel.selectedDifficulty.collectAsState()
            val scores by leaderboardViewModel.scores.collectAsState()
            
            LeaderboardScreen(
                selectedPuzzleId = selectedPuzzleId,
                selectedDifficulty = selectedDifficulty,
                scores = scores,
                onPuzzleSelected = { leaderboardViewModel.selectPuzzle(it) },
                onDifficultySelected = { leaderboardViewModel.selectDifficulty(it) },
                onBack = { navController.popBackStack() }
            )
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
