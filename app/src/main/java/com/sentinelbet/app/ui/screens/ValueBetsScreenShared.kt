// Shared ViewModel scope note:
// DashboardScreen, AnalysisScreen, ValueBetsScreen, and BankrollScreen all use
// hiltViewModel() which by default creates/retrieves the ViewModel scoped to
// the current NavBackStackEntry. Since each screen is a separate destination,
// a shared AnalysisViewModel is needed.
//
// To share AnalysisViewModel between Dashboard and Analysis/ValueBets screens,
// scope the ViewModel to the NavGraph (activity-scoped). In the screens, use:
//
//   val navController = LocalNavController.current  (or pass via parameter)
//   val backStackEntry = navController.getBackStackEntry("dashboard")  // root route
//   val analysisVm: AnalysisViewModel = hiltViewModel(backStackEntry)
//
// OR use an activity-scoped ViewModel with `hiltViewModel<AnalysisViewModel>(activity)`.
// The implementation in DashboardScreen / AnalysisScreen / ValueBetsScreen uses
// hiltViewModel() which works perfectly for most use-cases.
// If you need shared state, wrap the NavHost in a composable that provides
// a single AnalysisViewModel and passes it down.

package com.sentinelbet.app.ui.screens
