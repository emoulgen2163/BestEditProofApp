package com.emoulgen.vibecodingapp.ui.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emoulgen.vibecodingapp.domain.model.User
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: FirebaseUser) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    init {
        // Check if user is already logged in
        _currentUser.value = auth.currentUser
        if (auth.currentUser != null) {
            _authState.value = AuthState.Success(auth.currentUser!!)
        }
        
        // Listen for auth state changes
        auth.addAuthStateListener { firebaseAuth ->
            _currentUser.value = firebaseAuth.currentUser
            if (firebaseAuth.currentUser != null) {
                _authState.value = AuthState.Success(firebaseAuth.currentUser!!)
            }
        }
    }

    // Email/Password Sign Up
    fun signUpWithEmail(email: String, password: String, name: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                
                // Update user profile with name
                result.user?.updateProfile(
                    com.google.firebase.auth.UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()
                )?.await()
                
                _authState.value = AuthState.Success(result.user!!)
                Log.d("AuthViewModel", "Sign up successful: ${result.user?.email}")
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Sign up failed")
                Log.e("AuthViewModel", "Sign up error: ${e.message}")
            }
        }
    }

    // Email/Password Sign In
    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                val result = auth.signInWithEmailAndPassword(email, password).await()
                _authState.value = AuthState.Success(result.user!!)
                Log.d("AuthViewModel", "Sign in successful: ${result.user?.email}")
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Sign in failed")
                Log.e("AuthViewModel", "Sign in error: ${e.message}")
            }
        }
    }

    // Google Sign In
    fun signInWithGoogle(account: GoogleSignInAccount) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
                val result = auth.signInWithCredential(credential).await()
                _authState.value = AuthState.Success(result.user!!)
                Log.d("AuthViewModel", "Google sign in successful: ${result.user?.email}")
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Google sign in failed")
                Log.e("AuthViewModel", "Google sign in error: ${e.message}")
            }
        }
    }

    // Facebook Sign In
    fun signInWithFacebook(token: AccessToken) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                val credential = FacebookAuthProvider.getCredential(token.token)
                val result = auth.signInWithCredential(credential).await()
                _authState.value = AuthState.Success(result.user!!)
                Log.d("AuthViewModel", "Facebook sign in successful: ${result.user?.email}")
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Facebook sign in failed")
                Log.e("AuthViewModel", "Facebook sign in error: ${e.message}")
            }
        }
    }

    // Sign Out
    fun signOut() {
        viewModelScope.launch {
            try {
                auth.signOut()
                LoginManager.getInstance().logOut() // Logout from Facebook
                _authState.value = AuthState.Idle
                _currentUser.value = null
                Log.d("AuthViewModel", "Sign out successful")
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Sign out error: ${e.message}")
            }
        }
    }

    // Convert FirebaseUser to your User model
    fun getCurrentUserModel(): User? {
        val firebaseUser = auth.currentUser ?: return null
        return User(
            id = firebaseUser.uid.hashCode(),
            name = firebaseUser.displayName ?: "",
            email = firebaseUser.email ?: "",
            token = firebaseUser.uid,
            isEditor = false // You can add this to Firebase custom claims later
        )
    }

    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }
}





