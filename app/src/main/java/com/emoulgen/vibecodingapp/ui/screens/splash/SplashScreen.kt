package com.emoulgen.vibecodingapp.ui.screens.splash

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.emoulgen.vibecodingapp.R
import com.emoulgen.vibecodingapp.ui.theme.BorderFocused
import com.emoulgen.vibecodingapp.ui.theme.Typography

@Composable
fun SplashScreen(navController: NavController = rememberNavController()) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black), // Or theme color
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(R.drawable.best_edit_logo), // Place your logo in res/drawable
                contentDescription = "Logo",
                modifier = Modifier.size(140.dp)
            )
            Spacer(modifier = Modifier.height(40.dp))
            Button(
                onClick = { navController.navigate("signin") },
                modifier = Modifier.fillMaxWidth(0.7f),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                border = BorderStroke(1.dp, BorderFocused)
            ) {
                Text(text = "SIGN IN", style = Typography.bodyLarge)
            }
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = { navController.navigate("signup") },
                modifier = Modifier.fillMaxWidth(0.7f),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                border = BorderStroke(1.dp, BorderFocused)
            ) {
                Text(text = "SIGN UP", style = Typography.bodyLarge)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    SplashScreen()
}