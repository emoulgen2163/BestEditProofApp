package com.emoulgen.vibecodingapp.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.emoulgen.vibecodingapp.domain.model.Order
import com.emoulgen.vibecodingapp.domain.model.OrderDraft
import com.emoulgen.vibecodingapp.ui.screens.main.MainScreen
import com.emoulgen.vibecodingapp.ui.screens.main.ProfilePage
import com.emoulgen.vibecodingapp.ui.screens.main.ProfileScreen
import com.emoulgen.vibecodingapp.ui.screens.orderFlow.OrderFlowScreen
import com.emoulgen.vibecodingapp.ui.screens.orderFlow.OrderPaymentStep
import com.emoulgen.vibecodingapp.ui.screens.orderFlow.PayPalWebViewScreen
import com.emoulgen.vibecodingapp.ui.screens.splash.SignInScreen
import com.emoulgen.vibecodingapp.ui.screens.splash.SignUpScreen
import com.emoulgen.vibecodingapp.ui.screens.splash.SplashScreen
import com.emoulgen.vibecodingapp.ui.theme.VibeCodingAppTheme
import com.emoulgen.vibecodingapp.ui.viewModel.AppStateViewModel
import com.emoulgen.vibecodingapp.ui.viewModel.AuthViewModel
import com.emoulgen.vibecodingapp.ui.viewModel.FirestoreOrderViewModel
import com.emoulgen.vibecodingapp.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VibeCodingAppTheme {
                val navController = rememberNavController()
                val appStateViewModel: AppStateViewModel = viewModel()
                val authViewModel: AuthViewModel = viewModel()
                
                // Check if user is already logged in
                val currentUser by authViewModel.currentUser.collectAsState()
                
                LaunchedEffect(currentUser) {
                    if (currentUser != null) {
                        // User is logged in, navigate to main if not already there
                        if (navController.currentDestination?.route != "main") {
                            navController.navigate("main") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }
                }
                
                AppNavHost(navController, appStateViewModel, authViewModel)
            }
        }
    }
    
    // Handle Facebook callback (if using Facebook SDK directly)
    // Note: The FacebookLoginHelper uses ActivityResultContracts which handles this automatically
    // But if you need manual handling, uncomment below:
    // override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    //     super.onActivityResult(requestCode, resultCode, data)
    //     // Facebook callback handling is done through ActivityResultContracts in Compose
    // }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    appStateViewModel: AppStateViewModel,
    authViewModel: AuthViewModel
) {
    val currentUser by authViewModel.currentUser.collectAsState()

    NavHost(
        navController,
        startDestination = if (currentUser != null) "main" else "splash"
    ) {
        composable("splash") {
            SplashScreen(navController)
        }
        composable("signin") {
            SignInScreen(navController, authViewModel)
        }
        composable("signup") {
            SignUpScreen(navController, authViewModel)
        }
        composable("main") {
            MainScreen(navController)
        }
        composable("profile") {
            ProfilePage(navController)
        }
        // Route for NEW order (no orderId)
        composable("orderflow") {
            OrderFlowScreen(
                navController = navController,
                existingOrderId = null
            )
        }

        // Route for EDITING existing order (with orderId)
        composable(
            route = "orderflow/{orderId}",
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId")
            android.util.Log.d("Navigation", "OrderFlow with orderId: $orderId")

            OrderFlowScreen(
                navController = navController,
                existingOrderId = orderId
            )
        }

        // In your NavHost setup
        composable(
            route = "paypal_webview/{amount}/{orderTitle}/{orderId}",
            arguments = listOf(
                navArgument("amount") { type = NavType.StringType },
                navArgument("orderTitle") { type = NavType.StringType },
                navArgument("orderId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val amount = backStackEntry.arguments?.getString("amount") ?: "0.00"
            val orderTitle = backStackEntry.arguments?.getString("orderTitle") ?: ""
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""

            PayPalWebViewScreen(
                navController = navController,
                amount = amount,
                orderTitle = orderTitle,
                orderId = orderId,
                onPaymentSuccess = {
                    // Handle successful payment
                    // Update order status to "COMPLETE" or "PAID"
                    navController.navigate("main") {
                        popUpTo("main") { inclusive = true }
                    }
                },
                onPaymentCancel = {
                    // Handle cancelled payment
                    // Order remains "INCOMPLETE"
                }
            )
        }

        composable(
            route = "order_payment/{orderId}",
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            val orderViewModel: FirestoreOrderViewModel = hiltViewModel()

            LaunchedEffect(orderId) {
                orderViewModel.getOrderById(orderId)
            }

            val orderState by orderViewModel.orderDetailsState.collectAsStateWithLifecycle()

            when (orderState) {
                is Resource.Success -> {
                    val orderData = (orderState as Resource.Success).data
                    orderData?.let { data ->
                        val orderDraft = OrderDraft(
                            serviceType = data.serviceType,
                            deliveryTime = data.deliveryTimeLabel ?: "${data.deliveryTime} hours",
                            deliveryTimeHours = data.deliveryTime,
                            promoCode = data.promotionCode,
                            price = String.format("%.2f", data.price),
                            wordCount = data.wordCount,
                            projectTitle = data.projectTitle,
                            englishVariant = data.englishVariant,
                            projectDescription = data.projectDescription,
                            fileName = data.fileName ?: data.fileUrl?.substringAfterLast("/"),
                            fileUrl = data.fileUrl
                        )

                        OrderPaymentStep(
                            orderDraft = orderDraft,
                            orderId = orderId,
                            navController = navController,
                            onPay = {
                                navController.navigate("main") {
                                    popUpTo("main") { inclusive = true }
                                }
                            }
                        )
                    }
                }
                // ... handle loading and error states
                else -> {}
            }
        }
    }
}
