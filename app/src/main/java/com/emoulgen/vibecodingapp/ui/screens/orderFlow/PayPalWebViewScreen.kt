package com.emoulgen.vibecodingapp.ui.screens.orderFlow


import android.graphics.Bitmap
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.emoulgen.vibecodingapp.ui.theme.BackgroundDark
import com.emoulgen.vibecodingapp.ui.theme.TealPrimary
import com.emoulgen.vibecodingapp.ui.theme.TextPrimary
import com.emoulgen.vibecodingapp.ui.theme.TextWhite
import com.emoulgen.vibecodingapp.ui.theme.Typography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayPalWebViewScreen(
    navController: NavController = rememberNavController(),
    amount: String,
    orderTitle: String,
    orderId: String,
    onPaymentSuccess: () -> Unit = {},
    onPaymentCancel: () -> Unit = {}
) {
    var isLoading by remember { mutableStateOf(true) }
    var canGoBack by remember { mutableStateOf(false) }
    var webView by remember { mutableStateOf<WebView?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "PayPal Checkout",
                        style = Typography.titleLarge,
                        color = TextWhite
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (canGoBack && webView?.canGoBack() == true) {
                            webView?.goBack()
                        } else {
                            onPaymentCancel()
                            navController.popBackStack()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TealPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundDark
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        webView = this
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            loadWithOverviewMode = true
                            useWideViewPort = true
                            setSupportZoom(true)
                            builtInZoomControls = true
                            displayZoomControls = false
                        }

                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                super.onPageStarted(view, url, favicon)
                                isLoading = true

                                // Check for success URL
                                if (url?.contains("payment-success") == true ||
                                    url?.contains("success") == true) {
                                    onPaymentSuccess()
                                    navController.popBackStack()
                                }

                                // Check for cancel URL
                                if (url?.contains("payment-cancel") == true ||
                                    url?.contains("cancel") == true) {
                                    onPaymentCancel()
                                    navController.popBackStack()
                                }
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                isLoading = false
                                canGoBack = view?.canGoBack() ?: false
                            }

                            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                                // Handle PayPal redirects
                                url?.let {
                                    when {
                                        it.startsWith("http://") || it.startsWith("https://") -> {
                                            view?.loadUrl(it)
                                            return true
                                        }
                                    }
                                }
                                return false
                            }
                        }

                        // Build PayPal URL
                        val paypalUrl = buildPayPalUrl(amount, orderTitle, orderId)
                        loadUrl(paypalUrl)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // Loading indicator
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = TealPrimary
                )
            }
        }
    }
}

/**
 * Build PayPal URL for WebView
 * Replace this with your actual PayPal integration URL
 */
private fun buildPayPalUrl(amount: String, orderTitle: String, orderId: String): String {
    // Option 1: PayPal.me link (simplest)
    // Replace with your PayPal.me username
    // return "https://www.paypal.me/YourPayPalUsername/$amount"

    // Option 2: Your backend API that creates PayPal order and returns checkout URL
    // return "https://your-backend-api.com/create-paypal-payment?amount=$amount&orderId=$orderId"

    // Option 3: PayPal Sandbox for testing
    // This requires you to have a PayPal business account and set up an app
    // return "https://www.sandbox.paypal.com/checkoutnow?token=YOUR_TOKEN"

    // For now, using PayPal homepage as placeholder
    return "https://www.paypal.com"
}

@Preview(showBackground = true)
@Composable
fun PayPalPreview() {

    PayPalWebViewScreen(amount = "", orderTitle = "", orderId = "")

}