package com.emoulgen.besteditproofapp.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.emoulgen.besteditproofapp.R
import com.emoulgen.besteditproofapp.ui.theme.BestEditProofTheme
import com.emoulgen.besteditproofapp.ui.theme.BorderFocused
import com.emoulgen.besteditproofapp.ui.theme.FacebookBlue
import com.emoulgen.besteditproofapp.ui.theme.GoogleRed
import com.emoulgen.besteditproofapp.ui.theme.StatusActive
import com.emoulgen.besteditproofapp.ui.theme.TextPrimary
import com.emoulgen.besteditproofapp.ui.theme.TextSecondary
import com.emoulgen.besteditproofapp.ui.theme.TextWhite

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BestEditProofTheme {
                LoginScreen()
            }
        }
    }
}

@Composable
fun LoginScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 40.dp, start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            modifier = Modifier.size(200.dp),
            painter = painterResource(R.drawable.best_edit_logo),
            contentDescription = null
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, BorderFocused),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            )
        ){
            Box(Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(Color.Black)) {
                Text(
                    modifier = Modifier
                        .padding(start = 20.dp)
                        .align(Alignment.CenterStart),
                    text = "Login",
                    fontFamily = FontFamily(Font(R.font.raleway)),
                    fontWeight = FontWeight.SemiBold,
                    color = TextWhite
                )
            }
            Column(Modifier.padding(16.dp)) {
                val context = LocalContext.current
                var email by rememberSaveable { mutableStateOf("") }
                var password by rememberSaveable { mutableStateOf("") }
                var passwordVisibility by rememberSaveable { mutableStateOf(false) }

                val icon = if (passwordVisibility) painterResource(R.drawable.hidden) else painterResource(R.drawable.eye)

                Text(
                    text = "Email",
                    fontFamily = FontFamily(Font(R.font.lato)),
                    color = TextSecondary
                )

                Spacer(Modifier.height(18.dp))

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    value = email,
                    onValueChange = { email = it },
                    maxLines = 1,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = TextSecondary,
                        unfocusedBorderColor = TextSecondary,
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(Modifier.height(18.dp))
                Text(
                    text = "Password",
                    fontFamily = FontFamily(Font(R.font.lato)),
                    color = TextSecondary
                )

                Spacer(Modifier.height(18.dp))
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    value = password,
                    onValueChange = { password = it },
                    maxLines = 1,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = TextSecondary,
                        unfocusedBorderColor = TextSecondary,
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ),
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                passwordVisibility = !passwordVisibility
                            }
                        ) {
                            Icon(
                                painter = icon,
                                contentDescription = null
                            )
                        }
                    },
                    visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation()
                )
                Spacer(Modifier.height(8.dp))

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {},
                    text = "Forgot your password?",
                    fontFamily = FontFamily(Font(R.font.lato)),
                    color = StatusActive,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Do not have an account?",
                        fontFamily = FontFamily(Font(R.font.lato)),
                        color = StatusActive,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        modifier = Modifier.clickable{
                            context.startActivity(Intent(context, SignUpActivity::class.java))
                        },
                        text = "Sign Up",
                        fontFamily = FontFamily(Font(R.font.lato)),
                        fontWeight = FontWeight.SemiBold,
                        color = StatusActive,
                    )
                }

                Spacer(Modifier.height(18.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    ButtonComp(text = "Login", color = BorderFocused, onButtonClick = { })
                    Spacer(Modifier.height(8.dp))
                    ButtonComp(text = "Login with Facebook", color = FacebookBlue, onButtonClick = { })
                    Spacer(Modifier.height(8.dp))
                    ButtonComp(text = "Login with Google", color = GoogleRed, onButtonClick = { })
                }
            }
        }

    }
}

@Composable
fun ButtonComp(text: String, onButtonClick: () -> Unit, color: Color){
    Button(
        modifier = Modifier.width(250.dp),
        onClick = onButtonClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            fontFamily = FontFamily(Font(R.font.raleway)),
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}