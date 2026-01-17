package com.emoulgen.vibecodingapp.ui.screens.main

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.emoulgen.vibecodingapp.R
import com.emoulgen.vibecodingapp.ui.theme.TealPrimary
import com.emoulgen.vibecodingapp.ui.theme.TextSecondary
import com.emoulgen.vibecodingapp.ui.theme.Typography
import com.emoulgen.vibecodingapp.ui.viewModel.ProfileViewModel
import com.emoulgen.vibecodingapp.utils.Resource

@Composable
fun ProfilePage(navController: NavController = rememberNavController()) {
    var selectedTab by remember { mutableIntStateOf(1) }
    Scaffold(
        topBar = {
            AppHeader(navController)
        },
        bottomBar = {
            BottomNavigationBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                onNavigateToProfile = {  }
            )
        }
    ) {
        Column(Modifier.fillMaxSize().padding(it)) {
            when(selectedTab){
                0 -> DashboardScreen(navController = navController)
                1 -> ProfileScreen(navController = navController)
            }
        }
    }
}

@Composable
fun ProfileScreen(
    navController: NavController = rememberNavController(),
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val userProfileState by profileViewModel.userProfileState.collectAsStateWithLifecycle()
    val updateProfileState by profileViewModel.updateProfileState.collectAsStateWithLifecycle()
    val updatePasswordState by profileViewModel.updatePasswordState.collectAsStateWithLifecycle()

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisibility by rememberSaveable { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    val icon = if (passwordVisibility) painterResource(R.drawable.hidden) else painterResource(R.drawable.eye)

    val tips = listOf(
        "You can change your name and password.",
        "Your password must be at least 6 characters.",
        "You need to enter your current password to change to a new password.",
        "Email cannot be changed from this screen."
    )

    // Load user profile data
    LaunchedEffect(userProfileState) {
        when (val state = userProfileState) {
            is Resource.Success -> {
                state.data?.let { profile ->
                    fullName = profile.fullName
                    email = profile.email
                }
                isLoading = false
            }
            is Resource.Error -> {
                errorMessage = state.message
                isLoading = false
            }
            is Resource.Loading -> {
                isLoading = true
            }

            else -> {}
        }
    }

    // Handle profile update result
    LaunchedEffect(updateProfileState) {
        when (val state = updateProfileState) {
            is Resource.Success -> {
                successMessage = "Profile updated successfully"
                Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                profileViewModel.resetUpdateStates()
            }
            is Resource.Error -> {
                errorMessage = state.message
                Toast.makeText(context, state.message ?: "Failed to update profile", Toast.LENGTH_SHORT).show()
                profileViewModel.resetUpdateStates()
            }
            is Resource.Loading -> {
                // Handle loading if needed
            }

            else -> {}
        }
    }

    // Handle password update result
    LaunchedEffect(updatePasswordState) {
        when (val state = updatePasswordState) {
            is Resource.Success -> {
                successMessage = "Password updated successfully"
                Toast.makeText(context, "Password updated successfully", Toast.LENGTH_SHORT).show()
                // Clear password fields
                currentPassword = ""
                newPassword = ""
                confirmPassword = ""
                profileViewModel.resetUpdateStates()
            }
            is Resource.Error -> {
                errorMessage = state.message
                Toast.makeText(context, state.message ?: "Failed to update password", Toast.LENGTH_SHORT).show()
                profileViewModel.resetUpdateStates()
            }
            is Resource.Loading -> {
                // Handle loading if needed
            }

            else -> {}
        }
    }

    if (isLoading && fullName.isEmpty()) {
        // Show loading state for initial profile load
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = TealPrimary)
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Show error message if any
            errorMessage?.let { error ->
                Text(
                    text = error,
                    color = Color.Red,
                    style = Typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }

            // Show success message if any
            successMessage?.let { success ->
                Text(
                    text = success,
                    color = Color.Green,
                    style = Typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Full Name",
                style = Typography.bodyLarge,
                color = TextSecondary
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = fullName,
                onValueChange = {
                    fullName = it
                    errorMessage = null
                    successMessage = null
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TealPrimary,
                    unfocusedBorderColor = TextSecondary
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(18.dp))

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Email (Read-only)",
                style = Typography.bodyLarge,
                color = TextSecondary
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { }, // Read-only
                enabled = false,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledBorderColor = TextSecondary,
                    disabledTextColor = TextSecondary
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Change Password (Optional)",
                style = Typography.titleMedium,
                color = TextSecondary
            )

            Spacer(Modifier.height(18.dp))

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Current Password",
                style = Typography.bodyLarge,
                color = TextSecondary
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = currentPassword,
                onValueChange = {
                    currentPassword = it
                    errorMessage = null
                    successMessage = null
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TealPrimary,
                    unfocusedBorderColor = TextSecondary
                ),
                trailingIcon = {
                    IconButton(
                        onClick = {
                            passwordVisibility = !passwordVisibility
                        }
                    ) {
                        Icon(
                            painter = icon,
                            contentDescription = "Toggle password visibility"
                        )
                    }
                },
                visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(18.dp))

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "New Password",
                style = Typography.bodyLarge,
                color = TextSecondary
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = newPassword,
                onValueChange = {
                    newPassword = it
                    errorMessage = null
                    successMessage = null
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TealPrimary,
                    unfocusedBorderColor = TextSecondary
                ),
                trailingIcon = {
                    IconButton(
                        onClick = {
                            passwordVisibility = !passwordVisibility
                        }
                    ) {
                        Icon(
                            painter = icon,
                            contentDescription = "Toggle password visibility"
                        )
                    }
                },
                visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(18.dp))

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Confirm New Password",
                style = Typography.bodyLarge,
                color = TextSecondary
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    errorMessage = null
                    successMessage = null
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TealPrimary,
                    unfocusedBorderColor = TextSecondary
                ),
                trailingIcon = {
                    IconButton(
                        onClick = {
                            passwordVisibility = !passwordVisibility
                        }
                    ) {
                        Icon(
                            painter = icon,
                            contentDescription = "Toggle password visibility"
                        )
                    }
                },
                visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(18.dp))

            PageTipsSection(tips)

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceEvenly
            ) {
                // Save Profile Button
                Button(
                    onClick = {
                        errorMessage = null
                        successMessage = null

                        if (fullName.isBlank()) {
                            errorMessage = "Full name cannot be empty"
                            return@Button
                        }

                        profileViewModel.updateProfile(fullName)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(TealPrimary)
                ) {
                    Text(
                        text = "Save Profile",
                        style = Typography.bodyLarge
                    )
                }

                Spacer(Modifier.width(16.dp))

                // Update Password Button
                Button(
                    onClick = {
                        errorMessage = null
                        successMessage = null

                        when {
                            currentPassword.isBlank() && newPassword.isBlank() && confirmPassword.isBlank() -> {
                                errorMessage = "Fill in password fields to change password"
                                return@Button
                            }
                            currentPassword.isBlank() -> {
                                errorMessage = "Current password is required"
                                return@Button
                            }
                            newPassword.isBlank() -> {
                                errorMessage = "New password is required"
                                return@Button
                            }
                            newPassword.length < 6 -> {
                                errorMessage = "Password must be at least 6 characters"
                                return@Button
                            }
                            newPassword != confirmPassword -> {
                                errorMessage = "Passwords do not match"
                                return@Button
                            }
                        }

                        profileViewModel.updatePassword(currentPassword, newPassword)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(TealPrimary),
                    enabled = currentPassword.isNotBlank() || newPassword.isNotBlank() || confirmPassword.isNotBlank()
                ) {
                    Text(
                        text = "Update Password",
                        style = Typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen()
}