package com.emoulgen.vibecodingapp.ui.screens.orderFlow

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.emoulgen.vibecodingapp.R
import com.emoulgen.vibecodingapp.domain.model.CreateOrderRequest
import com.emoulgen.vibecodingapp.domain.model.OrderDraft
import com.emoulgen.vibecodingapp.ui.screens.main.AppHeader
import com.emoulgen.vibecodingapp.ui.screens.main.PageTipsSection
import com.emoulgen.vibecodingapp.ui.theme.TealPrimary
import com.emoulgen.vibecodingapp.ui.theme.TextSecondary
import com.emoulgen.vibecodingapp.ui.theme.Typography
import com.emoulgen.vibecodingapp.ui.viewModel.FirestoreOrderViewModel
import com.emoulgen.vibecodingapp.utils.Constants.deliveryTimes
import com.emoulgen.vibecodingapp.utils.Constants.serviceTypes
import com.emoulgen.vibecodingapp.utils.DeliveryTimeConverter
import com.emoulgen.vibecodingapp.utils.PriceCalculator
import com.emoulgen.vibecodingapp.utils.Resource
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderFlowScreen(
    navController: NavController = rememberNavController(),
    existingOrderId: String? = null,
    orderViewModel: FirestoreOrderViewModel = hiltViewModel()
) {
    var step by remember { mutableStateOf(1) }
    val orderDraft = remember { mutableStateOf(OrderDraft()) }
    var createdOrderId by remember { mutableStateOf(existingOrderId) }
    var isLoadingOrder by remember { mutableStateOf(existingOrderId != null) }
    var loadError by remember { mutableStateOf<String?>(null) }
    var isCreatingOrder by remember { mutableStateOf(false) }

    // Load existing order if orderId is provided
    LaunchedEffect(existingOrderId) {
        if (existingOrderId != null) {
            orderViewModel.getOrderById(existingOrderId)
        }
    }

    val orderDetailsState by orderViewModel.orderDetailsState.collectAsStateWithLifecycle()

    // Convert loaded order to OrderDraft
    LaunchedEffect(orderDetailsState) {
        if (existingOrderId != null) {
            when (val state = orderDetailsState) {
                is Resource.Success -> {
                    state.data?.let { data ->
                        orderDraft.value = OrderDraft(
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
                        isLoadingOrder = false
                    }
                }
                is Resource.Error -> {
                    loadError = state.message
                    isLoadingOrder = false
                }
                is Resource.Loading -> {
                    isLoadingOrder = true
                }

                else -> {}
            }
        }
    }

    // Observe order creation when moving from Step 2 to Step 3
    val createOrderState by orderViewModel.createOrderState.collectAsStateWithLifecycle()

    LaunchedEffect(createOrderState) {
        if (isCreatingOrder) {
            when (val state = createOrderState) {
                is Resource.Success -> {
                    android.util.Log.d("OrderFlow", "Order created successfully: ${state.data?.id}")
                    createdOrderId = state.data?.id
                    isCreatingOrder = false
                    step = 3 // Move to summary step
                }
                is Resource.Error -> {
                    android.util.Log.e("OrderFlow", "Error creating order: ${state.message}")
                    isCreatingOrder = false
                    // Show error to user
                }
                is Resource.Loading -> {
                    isCreatingOrder = true
                }
                else -> {}
            }
        }
    }

    Scaffold(
        topBar = { AppHeader(navController) }
    ) { padding ->
        if (isLoadingOrder || isCreatingOrder) {
            // Show loading state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = TealPrimary)
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = if (isLoadingOrder) "Loading order details..." else "Creating order...",
                        style = Typography.bodyLarge
                    )
                }
            }
        } else if (loadError != null) {
            // Show error state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Error: $loadError",
                        color = Color.Red,
                        style = Typography.bodyLarge
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = { navController.popBackStack() },
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                    ) {
                        Text("Go Back")
                    }
                }
            }
        } else {
            // Show order flow
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                when (step) {
                    1 -> OrderStep1(
                        orderDraft = orderDraft.value,
                        onUpdateDraft = { updated -> orderDraft.value = updated },
                        onNext = { step++ },
                        onBack = { navController.popBackStack() }
                    )
                    2 -> OrderStep2(
                        orderDraft = orderDraft.value,
                        onUpdateDraft = { updated -> orderDraft.value = updated },
                        onNext = {
                            // Create or update order when moving from Step 2 to Step 3
                            if (createdOrderId != null) {
                                // Update existing order
                                android.util.Log.d("OrderFlow", "Updating existing order: $createdOrderId")

                                val updates = mapOf(
                                    "projectTitle" to orderDraft.value.projectTitle,
                                    "serviceType" to orderDraft.value.serviceType,
                                    "deliveryTime" to orderDraft.value.deliveryTimeHours,
                                    "wordCount" to orderDraft.value.wordCount,
                                    "englishVariant" to orderDraft.value.englishVariant,
                                    "projectDescription" to orderDraft.value.projectDescription,
                                    "promotionCode" to (orderDraft.value.promoCode ?: ""),
                                    "fileUrl" to (orderDraft.value.fileUrl ?: ""),
                                    "fileName" to (orderDraft.value.fileName ?: ""),
                                    "price" to (orderDraft.value.price.toDoubleOrNull() ?: 0.0)
                                )

                                orderViewModel.updateOrder(createdOrderId!!, updates)
                                step = 3 // Move to summary
                            } else {
                                // Create new order
                                android.util.Log.d("OrderFlow", "Creating new order")
                                isCreatingOrder = true

                                val request = CreateOrderRequest(
                                    projectTitle = orderDraft.value.projectTitle,
                                    serviceType = orderDraft.value.serviceType,
                                    deliveryTime = orderDraft.value.deliveryTimeHours,
                                    deliveryTimeLabel = orderDraft.value.deliveryTime,
                                    wordCount = orderDraft.value.wordCount,
                                    englishVariant = orderDraft.value.englishVariant,
                                    projectDescription = orderDraft.value.projectDescription,
                                    promotionCode = orderDraft.value.promoCode,
                                    fileUrl = orderDraft.value.fileUrl
                                )

                                orderViewModel.createOrder(request)
                            }
                        },
                        onBack = { step-- }
                    )
                    3 -> OrderSummaryStep(
                        orderDraft = orderDraft.value,
                        existingOrderId = createdOrderId,
                        onNext = { step++ },
                        onBack = { step-- }
                    )
                    4 -> OrderPaymentStep(
                        orderDraft = orderDraft.value,
                        orderId = createdOrderId ?: "",
                        navController = navController,
                        onPay = {
                            navController.navigate("main") {
                                popUpTo("main") { inclusive = true }
                            }
                        }
                    )
                }
                StepIndicator(currentStep = step)
            }
        }
    }
}

// ... rest of the existing code (OrderStep1, OrderStep2, StepIndicator, etc.) remains the same

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderStep1(
    orderDraft: OrderDraft,
    onUpdateDraft: (OrderDraft) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    var expandedService by remember { mutableStateOf(false) }
    var expandedDelivery by remember { mutableStateOf(false) }

    var serviceType by remember { mutableStateOf(orderDraft.serviceType.ifEmpty { serviceTypes[0] }) }
    var deliveryTime by remember { mutableStateOf(orderDraft.deliveryTime.ifEmpty { deliveryTimes[0] }) }
    var promoCode by remember { mutableStateOf(orderDraft.promoCode ?: "") }
    // Keep a string for safe editing, and an int parsed value for validation
    var wordCountStr by remember { mutableStateOf(orderDraft.wordCount.toString()) }
    var wordCount by remember { mutableStateOf(orderDraft.wordCount) }

    // Calculate price automatically based on parameters
    val calculatedPrice = remember(serviceType, deliveryTime, wordCount, promoCode) {
        if (wordCount > 0 && serviceType.isNotBlank() && deliveryTime.isNotBlank()) {
            PriceCalculator.calculatePrice(
                serviceType = serviceType,
                deliveryTime = deliveryTime,
                wordCount = wordCount,
                promotionCode = promoCode.ifBlank { null }
            )
        } else {
            0.0
        }
    }

    // Format price for display
    val priceDisplay = remember(calculatedPrice) {
        val df = DecimalFormat("#0.00")
        df.format(calculatedPrice)
    }

    val valid = serviceType.isNotBlank() && deliveryTime.isNotBlank() && wordCount > 25

    val tips = listOf(
        "You must fill in Type of Services, Delivery Time and only one of Type your word count manually or Your File Content fields to see the order price",
        "If you have a promotion code, you can enter your code in the promotion code field to take advantage of the discount",
        "If you know the number of words in your file, enter it in the Type your word count manually field otherwise go to your file and select(ctrl+A) and copy(ctrl+C) all the text in your file, then paste(ctrl+P) it in the Your File Content field.",
        "How many words will appear in Type your word count manually field automatically in your file.",
        "Use either Type your word count manually field or Your File Content field.",
        "Click the 'Next' button to enter information about the project."
    )

    Column(Modifier.padding(16.dp)) {
        // Service Type Dropdown
        Text(
            text = "Type of Services *",
            style = Typography.bodyLarge,
            color = TextSecondary
        )
        Spacer(Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = expandedService,
            onExpandedChange = { expandedService = it }
        ) {
            OutlinedTextField(
                value = serviceType,
                onValueChange = { /* readOnly */ },
                shape = RoundedCornerShape(12.dp),
                readOnly = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TextSecondary,
                    unfocusedBorderColor = TextSecondary
                ),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedService) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .clickable { expandedService = true }
            )

            ExposedDropdownMenu(
                expanded = expandedService,
                onDismissRequest = { expandedService = false }
            ) {
                serviceTypes.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption) },
                        onClick = {
                            serviceType = selectionOption
                            expandedService = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(18.dp))

        Text(
            text = "Delivery Time *",
            color = TextSecondary,
            style = Typography.bodyLarge
        )
        Spacer(Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = expandedDelivery,
            onExpandedChange = { expandedDelivery = it },
        ) {
            OutlinedTextField(
                value = deliveryTime,
                onValueChange = { /* readOnly */ },
                readOnly = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TextSecondary,
                    unfocusedBorderColor = TextSecondary
                ),
                shape = RoundedCornerShape(12.dp),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDelivery) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .clickable { expandedDelivery = true }
            )

            ExposedDropdownMenu(
                expanded = expandedDelivery,
                onDismissRequest = { expandedDelivery = false }
            ) {
                deliveryTimes.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption) },
                        onClick = {
                            deliveryTime = selectionOption
                            expandedDelivery = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(18.dp))

        OutlinedTextField(
            value = promoCode,
            onValueChange = { promoCode = it },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TextSecondary,
                unfocusedBorderColor = TextSecondary
            ),
            shape = RoundedCornerShape(12.dp),
            placeholder = {
                Text(
                    text = "Promotion Code (Optional)",
                    color = TextSecondary,
                    style = Typography.bodyLarge
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(18.dp))

        // Display calculated price (read-only)
        Text(
            text = "Price (USD)",
            color = TextSecondary,
            style = Typography.bodyLarge
        )
        Spacer(Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Black,
                contentColor = Color.White
            )
        ) {

            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(R.drawable.dollar_symbol),
                    contentDescription = null,
                    tint = TealPrimary
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "$priceDisplay",
                    style = Typography.bodyLarge
                )
            }

        }
//        OutlinedTextField(
//            value = "$$priceDisplay",
//            onValueChange = { /* Read-only - price is calculated automatically */ },
//            readOnly = true,
//            colors = OutlinedTextFieldDefaults.colors(
//                focusedBorderColor = TextSecondary,
//                unfocusedBorderColor = TextSecondary
//            ),
//            shape = RoundedCornerShape(12.dp),
//            modifier = Modifier.fillMaxWidth()
//        )

        Spacer(Modifier.height(18.dp))

        HorizontalDivider(thickness = 1.dp, color = Color.Black)

        Spacer(Modifier.height(18.dp))

        Text(
            text = "Word Count *",
            color = TextSecondary,
            style = Typography.bodyLarge
        )
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = wordCountStr,
            onValueChange = {
                wordCountStr = it
                wordCount = it.toIntOrNull() ?: 0
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TextSecondary,
                unfocusedBorderColor = TextSecondary
            ),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        PageTipsSection(tips)

        Row(Modifier.fillMaxWidth()) {
            Button(
                onClick = { onBack() },
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(text = "Back", style = Typography.bodyLarge)
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = {
                    // Write back to draft
                    val updated = orderDraft.copy(
                        serviceType = serviceType,
                        deliveryTime = deliveryTime,
                        deliveryTimeHours = DeliveryTimeConverter.toHours(deliveryTime),
                        promoCode = promoCode.ifBlank { null },
                        price = priceDisplay,
                        wordCount = wordCount
                    )
                    onUpdateDraft(updated)
                    onNext()
                },
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                shape = RoundedCornerShape(8.dp),
                enabled = valid
            ) {
                Text(text = "Next", style = Typography.bodyLarge)
            }
        }
    }
}

@Composable
fun StepIndicator(currentStep: Int) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        for (i in 1..4) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        if (i == currentStep) TealPrimary else Color.LightGray,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$i",
                    color = if (i == currentStep) Color.White else Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
            if (i < 4) {
                HorizontalDivider(Modifier.width(24.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OrderFlowPreview() {
    OrderFlowScreen()
}
