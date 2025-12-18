package com.pagadasports.pagada

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pagadasports.pagada.screens.ForgotPasswordScreen
import com.pagadasports.pagada.screens.HomeScreen
import com.pagadasports.pagada.screens.LandingScreen
import com.pagadasports.pagada.screens.LoginScreen
import com.pagadasports.pagada.screens.ProfileScreen
import com.pagadasports.pagada.screens.RegisterScreen
import com.pagadasports.pagada.ui.theme.PagadaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PagadaTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "landing") {
                    composable("landing") {
                        LandingScreen(
                            onLoginClick = { navController.navigate("login") },
                            onRegisterClick = { navController.navigate("register") }
                        )
                    }
                    composable("login") {
                        LoginScreen(
                            onRegisterClick = { navController.navigate("register") },
                            onBackClick = { navController.popBackStack() },
                            onLoginSuccess = {
                                navController.navigate("home") {
                                    popUpTo("landing") { inclusive = true }
                                }
                            },
                            onForgotPasswordClick = { navController.navigate("forgot_password") }
                        )
                    }
                    composable("register") {
                        RegisterScreen(
                            onLoginClick = { navController.navigate("login") },
                            onBackClick = { navController.popBackStack() },
                            onRegisterSuccess = {
                                navController.navigate("profile") {
                                    popUpTo("landing") { inclusive = true }
                                }
                            }
                        )
                    }
                    composable("forgot_password") {
                        ForgotPasswordScreen(
                            onBackClick = { navController.popBackStack() },
                            onSuccessNavigateToLogin = {
                                navController.navigate("login") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        )
                    }
                    composable("profile") {
                        ProfileScreen(
                            onLogoutClick = {
                                navController.navigate("landing") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        )
                    }
                    composable("home") {
                        HomeScreen(
                            onSignOut = {
                                navController.navigate("landing") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
