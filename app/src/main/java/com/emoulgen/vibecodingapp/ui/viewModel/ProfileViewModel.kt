package com.emoulgen.vibecodingapp.ui.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emoulgen.vibecodingapp.utils.Resource
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class UserProfile(
    val fullName: String = "",
    val email: String = ""
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _userProfileState = MutableStateFlow<Resource<UserProfile>>(Resource.Loading())
    val userProfileState: StateFlow<Resource<UserProfile>> = _userProfileState.asStateFlow()

    private val _updateProfileState = MutableStateFlow<Resource<Unit>>(Resource.Loading())
    val updateProfileState: StateFlow<Resource<Unit>> = _updateProfileState.asStateFlow()

    private val _updatePasswordState = MutableStateFlow<Resource<Unit>>(Resource.Loading())
    val updatePasswordState: StateFlow<Resource<Unit>> = _updatePasswordState.asStateFlow()

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            try {
                _userProfileState.value = Resource.Loading()

                val currentUser = auth.currentUser
                if (currentUser != null) {
                    val profile = UserProfile(
                        fullName = currentUser.displayName ?: "",
                        email = currentUser.email ?: ""
                    )
                    _userProfileState.value = Resource.Success(profile)
                } else {
                    _userProfileState.value = Resource.Error("User not authenticated")
                }
            } catch (e: Exception) {
                _userProfileState.value = Resource.Error(e.localizedMessage ?: "Failed to load profile")
            }
        }
    }

    fun updateProfile(fullName: String) {
        viewModelScope.launch {
            try {
                _updateProfileState.value = Resource.Loading()

                val currentUser = auth.currentUser
                if (currentUser == null) {
                    _updateProfileState.value = Resource.Error("User not authenticated")
                    return@launch
                }

                // Update display name
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(fullName)
                    .build()

                currentUser.updateProfile(profileUpdates).await()

                _updateProfileState.value = Resource.Success(Unit)

                // Reload profile after update
                loadUserProfile()
            } catch (e: Exception) {
                _updateProfileState.value = Resource.Error(e.localizedMessage ?: "Failed to update profile")
            }
        }
    }

    fun updatePassword(currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            try {
                _updatePasswordState.value = Resource.Loading()

                val currentUser = auth.currentUser
                if (currentUser == null || currentUser.email == null) {
                    _updatePasswordState.value = Resource.Error("User not authenticated")
                    return@launch
                }

                // Re-authenticate user before changing password
                val credential = EmailAuthProvider.getCredential(currentUser.email!!, currentPassword)
                currentUser.reauthenticate(credential).await()

                // Update password
                currentUser.updatePassword(newPassword).await()

                _updatePasswordState.value = Resource.Success(Unit)
            } catch (e: Exception) {
                _updatePasswordState.value = Resource.Error(
                    when {
                        e.message?.contains("password is invalid") == true -> "Current password is incorrect"
                        e.message?.contains("weak-password") == true -> "New password is too weak"
                        else -> e.localizedMessage ?: "Failed to update password"
                    }
                )
            }
        }
    }

    fun resetUpdateStates() {
        _updateProfileState.value = Resource.Loading()
        _updatePasswordState.value = Resource.Loading()
    }
}