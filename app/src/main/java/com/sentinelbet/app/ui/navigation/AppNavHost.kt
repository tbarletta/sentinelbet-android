package com.sentinelbet.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.sentinelbet.app.R
import com.sentinelbet.app.data.repository.SubscriptionRepository.Feature
import com.sentinelbet.app.ui.screens.*
import com.sentinelbet.app.viewmodel.AnalysisViewModel
import com.sentinelbet.app.viewmodel.AuthState
import com.sentinelbet.app.viewmodel.AuthViewModel
import com.sentinelbet.app.viewmodel.ThemeViewModel

sealed class Screen(
    val route: String,
    val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
) {
    data object Login     : Screen("login",    R.string.nav_dashboard,   Icons.Filled.AccountCircle,         Icons.Outlined.AccountCircle)
    data object Dashboard : Screen("dashboard",R.string.nav_dashboard,   Icons.Filled.Dashboard,             Icons.Outlined.Dashboard)
    data object Live      : Screen("live",     R.string.nav_live,        Icons.Filled.LiveTv,                Icons.Outlined.LiveTv)
    data object Analysis  : Screen("analysis", R.string.nav_analysis,    Icons.Filled.AutoAwesome,           Icons.Outlined.AutoAwesome)
    data object ValueBets : Screen("valuebets",R.string.nav_value_bets,  Icons.Filled.MonetizationOn,        Icons.Outlined.MonetizationOn)
    data object Bankroll  : Screen("bankroll", R.string.nav_bankroll,    Icons.Filled.AccountBalanceWallet,  Icons.Outlined.AccountBalanceWallet)
    // Off-nav-bar screens
    data object Settings  : Screen("settings", R.string.nav_settings,    Icons.Filled.Settings,              Icons.Outlined.Settings)
    data object Paywall   : Screen("paywall",  R.string.nav_dashboard,   Icons.Filled.Stars,                 Icons.Outlined.Stars)
}

// Only 5 items in the nav bar — Settings + Login live outside
private val navBarScreens = listOf(
    Screen.Dashboard, Screen.Live, Screen.Analysis, Screen.ValueBets, Screen.Bankroll
)

// Screens where the nav bar should be hidden
private val hiddenNavBar = setOf(Screen.Settings.route, Screen.Login.route, Screen.Paywall.route)

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val analysisVm: AnalysisViewModel = hiltViewModel()
    val themeVm:    ThemeViewModel    = hiltViewModel()
    val authVm:     AuthViewModel     = hiltViewModel()

    val authState by authVm.authState.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDest = navBackStackEntry?.destination

    // ── Auth redirect ────────────────────────────────────────────────────
    LaunchedEffect(authState) {
        val currentRoute = navController.currentDestination?.route
        when (authState) {
            is AuthState.Unauthenticated -> {
                if (currentRoute != Screen.Login.route) {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
            is AuthState.Authenticated -> {
                if (currentRoute == Screen.Login.route || currentRoute == null) {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
            else -> { /* Loading — do nothing */ }
        }
    }

    fun navigateTo(screen: Screen) {
        navController.navigate(screen.route) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState    = true
        }
    }

    fun navigateToPaywall() {
        navController.navigate(Screen.Paywall.route) {
            launchSingleTop = true
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            val hideNav = currentDest?.hierarchy?.any { it.route in hiddenNavBar } == true
            if (!hideNav) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = androidx.compose.ui.unit.Dp(0f),
                ) {
                    navBarScreens.forEach { screen ->
                        val selected = currentDest?.hierarchy?.any { it.route == screen.route } == true
                        NavigationBarItem(
                            selected  = selected,
                            onClick   = { navigateTo(screen) },
                            icon = {
                                Icon(
                                    if (selected) screen.selectedIcon else screen.unselectedIcon,
                                    contentDescription = stringResource(screen.labelRes),
                                )
                            },
                            label  = { Text(stringResource(screen.labelRes)) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor   = MaterialTheme.colorScheme.primary,
                                selectedTextColor   = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor      = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            ),
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = Screen.Login.route,   // Auth gate starts here; LaunchedEffect redirects
            modifier         = Modifier.padding(innerPadding),
        ) {
            // ── Auth ────────────────────────────────────────────────────────
            composable(Screen.Login.route) {
                LoginScreen(
                    authVm          = authVm,
                    onAuthenticated = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                )
            }

            // ── Main screens ────────────────────────────────────────────────
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    analysisVm           = analysisVm,
                    onNavigateToSettings = {
                        navController.navigate(Screen.Settings.route) {
                            launchSingleTop = true
                        }
                    },
                )
            }

            composable(Screen.Live.route) {
                LiveScreen()
            }

            composable(Screen.Analysis.route) {
                // PRO gate: unlimited AI analysis
                if (!authVm.canAccess(Feature.AI_ANALYSIS)) {
                    PaywallScreen(
                        onBack    = { navController.popBackStack() },
                        onUpgrade = ::navigateToPaywall,
                    )
                } else {
                    AnalysisScreen(analysisVm = analysisVm)
                }
            }

            composable(Screen.ValueBets.route) {
                ValueBetsScreen(
                    analysisVm    = analysisVm,
                    isPro         = authVm.canAccess(Feature.VALUE_BETS_UNLIMITED),
                    onUpgrade     = ::navigateToPaywall,
                )
            }

            composable(Screen.Bankroll.route) {
                BankrollScreen()
            }

            // ── Off-bar screens ─────────────────────────────────────────────
            composable(Screen.Settings.route) {
                SettingsScreen(
                    themeVm  = themeVm,
                    authVm   = authVm,
                    onBack   = { navController.popBackStack() },
                )
            }

            composable(Screen.Paywall.route) {
                PaywallScreen(
                    onBack    = { navController.popBackStack() },
                    onUpgrade = { navController.popBackStack() }, // TODO: BILL-01 Google Play Billing
                )
            }
        }
    }
}
