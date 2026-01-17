package com.emoulgen.vibecodingapp.utils

import android.app.Activity
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.emoulgen.vibecodingapp.ui.viewModel.AuthViewModel

class FacebookLoginHelper(private val authViewModel: AuthViewModel) {
    private val callbackManager = CallbackManager.Factory.create()

    init {
        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                result.accessToken.let { token ->
                    authViewModel.signInWithFacebook(token)
                }
            }

            override fun onCancel() {
                authViewModel.resetAuthState()
            }

            override fun onError(error: FacebookException) {
                authViewModel.resetAuthState()
            }
        })
    }

    fun getCallbackManager(): CallbackManager = callbackManager

    fun login(activity: Activity) {
        LoginManager.getInstance().logInWithReadPermissions(
            activity,
            listOf("email", "public_profile")
        )
    }
}





