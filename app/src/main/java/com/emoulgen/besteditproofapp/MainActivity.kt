package com.emoulgen.besteditproofapp

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
import androidx.compose.ui.tooling.preview.Preview
import com.emoulgen.besteditproofapp.ui.theme.BestEditProofAppTheme
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.emoulgen.besteditproofapp.ui.splash.SplashScreen
import com.emoulgen.besteditproofapp.ui.auth.SignInScreen
import com.emoulgen.besteditproofapp.ui.auth.SignUpScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BestEditProofAppTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "splash"
                ) {
                    composable("splash") {
                        SplashScreen(
                            onSignIn = { navController.navigate("signIn") },
                            onSignUp = { navController.navigate("signUp") }
                        )
                    }
                    composable("signIn") {
                        SignInScreen(
                            onSignIn = { /* Handle real sign in */ },
                            onSignUp = { navController.navigate("signUp") }
                        )
                    }
                    composable("signUp") {
                        SignUpScreen(
                            onRegister = { /* Handle real register */ },
                            onSignIn = { navController.navigate("signIn") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BestEditProofAppTheme {
        Greeting("Android")
    }
}