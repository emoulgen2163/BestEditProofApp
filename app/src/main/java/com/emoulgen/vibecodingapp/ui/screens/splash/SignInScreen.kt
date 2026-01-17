package com.emoulgen.vibecodingapp.ui.screens.splash

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.emoulgen.vibecodingapp.R
import com.emoulgen.vibecodingapp.ui.theme.BorderFocused
import com.emoulgen.vibecodingapp.ui.theme.FacebookBlue
import com.emoulgen.vibecodingapp.ui.theme.GoogleRed
import com.emoulgen.vibecodingapp.ui.theme.StatusActive
import com.emoulgen.vibecodingapp.ui.theme.TextPrimary
import com.emoulgen.vibecodingapp.ui.theme.TextSecondary
import com.emoulgen.vibecodingapp.ui.theme.TextWhite
import com.emoulgen.vibecodingapp.ui.theme.Typography
import com.emoulgen.vibecodingapp.utils.FacebookLoginHelper
import com.emoulgen.vibecodingapp.utils.GoogleSignInHelper
import com.emoulgen.vibecodingapp.ui.viewModel.AuthViewModel
import com.emoulgen.vibecodingapp.ui.viewModel.AuthState
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException

@Composable
fun SignInScreen(
    navController: NavController = rememberNavController(),
    authViewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    val authState by authViewModel.authState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisibility by rememberSaveable { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val icon = if (passwordVisibility) painterResource(R.drawable.hidden) else painterResource(R.drawable.eye)

    // Google Sign-In Launcher
    val googleSignInClient = remember { GoogleSignInHelper.getGoogleSignInClient(context) }
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            val account = task.getResult(ApiException::class.java)
            account?.let { authViewModel.signInWithGoogle(it) }
        } catch (e: ApiException) {
            Toast.makeText(context, "Google Sign-In failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // Facebook Login Helper
    val facebookLoginHelper = remember { FacebookLoginHelper(authViewModel) }

    // Handle auth state changes
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Loading -> {
                isLoading = true
            }
            is AuthState.Success -> {
                isLoading = false
                navController.navigate("main") {
                    popUpTo("signin") { inclusive = true }
                }
            }
            is AuthState.Error -> {
                isLoading = false
                snackbarHostState.showSnackbar((authState as AuthState.Error).message)
            }
            else -> {
                isLoading = false
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(Color.Black)
                ) {
                    Text(
                        modifier = Modifier
                            .padding(start = 20.dp)
                            .align(Alignment.CenterStart),
                        text = "Login",
                        style = Typography.titleSmall,
                        color = TextWhite
                    )
                }
                Column(Modifier.padding(16.dp)) {
                    Text(
                        text = "Email",
                        style = Typography.bodyLarge,
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
                        enabled = !isLoading,
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
                        style = Typography.bodyLarge,
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
                        enabled = !isLoading,
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
                            .clickable(enabled = !isLoading) {},
                        text = "Forgot your password?",
                        style = Typography.bodyMedium,
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
                            style = Typography.bodyMedium,
                            color = StatusActive,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            modifier = Modifier.clickable(enabled = !isLoading) {
                                navController.navigate("signup")
                            },
                            text = "Sign Up",
                            style = Typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = StatusActive,
                        )
                    }

                    Spacer(Modifier.height(18.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        ButtonComp(
                            text = "Login",
                            color = BorderFocused,
                            enabled = !isLoading && email.isNotBlank() && password.isNotBlank(),
                            onButtonClick = {
                                authViewModel.signInWithEmail(email, password)
                            }
                        )
                        Spacer(Modifier.height(8.dp))
                        ButtonComp(
                            text = "Login with Facebook",
                            color = FacebookBlue,
                            enabled = !isLoading,
                            onButtonClick = {
                                facebookLoginHelper.login(context as Activity)
                            }
                        )
                        Spacer(Modifier.height(8.dp))
                        ButtonComp(
                            text = "Login with Google",
                            color = GoogleRed,
                            enabled = !isLoading,
                            onButtonClick = {
                                googleSignInLauncher.launch(googleSignInClient.signInIntent)
                            }
                        )
                    }
                }
            }
        }
        
        // Loading indicator
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = BorderFocused)
            }
        }
        
        // Snackbar
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun ButtonComp(
    text: String,
    onButtonClick: () -> Unit,
    color: Color,
    enabled: Boolean = true
) {
    Button(
        modifier = Modifier.width(250.dp),
        onClick = onButtonClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            contentColor = Color.White,
            disabledContainerColor = color.copy(alpha = 0.6f),
            disabledContentColor = Color.White.copy(alpha = 0.6f)
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
fun SignInPreview() {
    SignInScreen()
}
