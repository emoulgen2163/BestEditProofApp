package com.emoulgen.besteditproofapp.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.emoulgen.besteditproofapp.R
import com.emoulgen.besteditproofapp.ui.theme.BackgroundDark
import com.emoulgen.besteditproofapp.ui.theme.BestEditProofTheme
import com.emoulgen.besteditproofapp.ui.theme.BorderFocused

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BestEditProofTheme {
                SplashScreen()
            }
        }
    }

}

@Composable
fun SplashScreen() {

    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize().background(BackgroundDark),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            modifier = Modifier.size(200.dp),
            painter = painterResource(R.drawable.best_edit_logo),
            contentDescription = null
        )

        Spacer(Modifier.height( 56.dp))

        Column {
            Button(
                modifier = Modifier.width(250.dp).padding(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.White
                ),
                border = BorderStroke(1.dp, BorderFocused),
                onClick = {
                    context.startActivity(Intent(context, LoginActivity::class.java))
                }
            ) {
                Text(
                    text = "SIGN IN",
                    fontFamily = FontFamily(Font(R.font.lato))
                )
            }

            Button(
                modifier = Modifier.width(250.dp).padding(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.White
                ),
                border = BorderStroke(1.dp, BorderFocused),
                onClick = {
                    context.startActivity(Intent(context, SignUpActivity::class.java))
                }
            ) {
                Text(
                    text = "SIGN UP",
                    fontFamily = FontFamily(Font(R.font.lato))
                )
            }
        }
    }
}