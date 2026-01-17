package com.emoulgen.vibecodingapp.ui.screens.main

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.emoulgen.vibecodingapp.domain.model.Order
import com.emoulgen.vibecodingapp.ui.theme.BorderColor
import com.emoulgen.vibecodingapp.ui.theme.BorderFocused
import com.emoulgen.vibecodingapp.ui.theme.CardBackground
import com.emoulgen.vibecodingapp.ui.theme.StatusError
import com.emoulgen.vibecodingapp.ui.theme.TealPrimary
import com.emoulgen.vibecodingapp.ui.theme.TextPrimary
import com.emoulgen.vibecodingapp.ui.theme.TextWhite
import com.emoulgen.vibecodingapp.ui.theme.Typography
import com.emoulgen.vibecodingapp.ui.viewModel.FirestoreOrderViewModel
import com.emoulgen.vibecodingapp.utils.OrderStatus
import com.emoulgen.vibecodingapp.utils.Resource
import kotlinx.coroutines.delay

@Composable
fun DashboardScreen(
    navController: NavController = rememberNavController(),
    orderViewModel: FirestoreOrderViewModel = hiltViewModel()
) {
    val ordersState by orderViewModel.ordersState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // Filter incomplete orders
    val incompleteOrders = when (ordersState) {
        is Resource.Success -> {
            ordersState.data?.filter { order ->
                order.status == OrderStatus.INCOMPLETE || order.status == OrderStatus.PENDING
            } ?: emptyList()
        }
        else -> emptyList()
    }

    // Fetch orders on initial load and when search changes
    LaunchedEffect(searchQuery) {
        // Debounce search
        delay(300)
        orderViewModel.getOrdersRealtime(
            status = OrderStatus.INCOMPLETE,
            search = searchQuery.ifBlank { null }
        )
    }

    // Handle delete order
    val handleDeleteOrder = { orderId: String ->
        orderViewModel.deleteOrder(orderId) { result ->
            when (result) {
                is Resource.Success -> {
                    // Refresh orders after deletion
                    orderViewModel.getOrdersRealtime(status = OrderStatus.INCOMPLETE, search = if (searchQuery.isBlank()) null else searchQuery)
                }
                is Resource.Error -> {
                    Toast.makeText(context, result.message ?: "Failed to delete order", Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }

    // Handle complete order - navigate to order flow with existing order ID
    val handleCompleteOrder = { orderId: String ->
        try {
            navController.navigate("orderflow/$orderId")
        } catch (e: Exception) {
            Toast.makeText(context, "$e", Toast.LENGTH_SHORT).show()
        }
    }

    val tips = listOf(
        "Click on the \"New Order\" button to give a new order.",
        "Use the \"Filter\" section to search your previous orders.",
        "Click on the \"Complete Order\" button to complete orders that you have not paid and go directly to the order summary and then payment section.",
        "Click to view button for detailed information about your order and to reach \"Order Details\", \"Order Messages\", \"Order History\", and \"Order Files\".",
        "If you received a payment difference message about your order, click \"Pay Difference\" button."
    )

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp)
        ) {
            // Orders Section
            item {
                when (ordersState) {
                    is Resource.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = TealPrimary)
                        }
                    }
                    is Resource.Error -> {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, StatusError)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Error loading orders",
                                    color = StatusError,
                                    style = Typography.titleMedium
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = ordersState.message ?: "Unknown error",
                                    color = TextPrimary,
                                    style = Typography.bodyMedium
                                )
                            }
                        }
                    }
                    is Resource.Success -> {
                        OrdersSection(
                            searchQuery = searchQuery,
                            onSearchQueryChange = { searchQuery = it },
                            orders = incompleteOrders,
                            onNewOrderClick = { navController.navigate("orderflow") },
                            onDeleteOrder = handleDeleteOrder,
                            onCompleteOrder = handleCompleteOrder
                        )
                    }
                    else -> {
                        // Idle state - show empty or initial state
                        OrdersSection(
                            searchQuery = searchQuery,
                            onSearchQueryChange = { searchQuery = it },
                            orders = emptyList(),
                            onNewOrderClick = { navController.navigate("orderflow") },
                            onDeleteOrder = handleDeleteOrder,
                            onCompleteOrder = handleCompleteOrder
                        )
                    }
                }
            }

            // Page Tips Section
            item {
                Spacer(modifier = Modifier.height(24.dp))
                PageTipsSection(tips)
            }
        }

        // Snackbar for errors
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun OrdersSection(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    orders: List<Order>,
    onNewOrderClick: () -> Unit,
    onDeleteOrder: (String) -> Unit,
    onCompleteOrder: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BorderFocused),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Search and New Order Button Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Header
                Text(
                    text = "Orders",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    style = Typography.titleLarge,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.width(12.dp))

                // New Order Button
                Button(
                    onClick = onNewOrderClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TealPrimary
                    ),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text(
                        text = "New Order",
                        fontSize = 14.sp,
                        style = Typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Search Bar
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                border = BorderStroke(1.dp, BorderFocused),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                ),
                shape = RoundedCornerShape(0)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(TealPrimary)
                            .size(50.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = TextWhite,
                        )
                    }

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        placeholder = {
                            Text(
                                text = "Search...",
                                style = Typography.bodyMedium
                            )
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TealPrimary,
                            unfocusedBorderColor = BorderColor
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Orders Table
            OrdersTable(
                orders = orders,
                onDeleteOrder = onDeleteOrder,
                onCompleteOrder = onCompleteOrder
            )
        }
    }
}

@Composable
fun OrdersTable(
    orders: List<Order>,
    onDeleteOrder: (String) -> Unit,
    onCompleteOrder: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
    ) {
        // Table Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(TextPrimary)
                .padding(12.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TableHeaderText("ID", modifier = Modifier.width(60.dp))
            TableHeaderText("Title", modifier = Modifier.width(110.dp))
            TableHeaderText("Status", modifier = Modifier.width(100.dp))
            TableHeaderText("Price", modifier = Modifier.width(70.dp))
            TableHeaderText("Rate", modifier = Modifier.width(60.dp))
            TableHeaderText("Action", modifier = Modifier.width(100.dp))
            TableHeaderText("", modifier = Modifier.width(60.dp))
        }

        // Scrollable Table Rows
        if (orders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = "No incomplete orders",
                    style = Typography.bodyMedium,
                    color = TextPrimary,
                )
            }
        } else {
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(250.dp) // Fixed height for scrollable area
                .background(CardBackground),
                contentAlignment = Alignment.Center) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(orders) { order ->
                        OrderRow(
                            order = order,
                            onDelete = { onDeleteOrder(order.id) },
                            onComplete = { onCompleteOrder(order.id) }
                        )
                        HorizontalDivider(
                            color = BorderColor,
                            thickness = 1.dp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TableHeaderText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = Color.White,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        style = Typography.bodyMedium,
        modifier = modifier,
        textAlign = TextAlign.Center
    )
}

@Composable
fun OrderRow(
    order: Order,
    onDelete: () -> Unit,
    onComplete: () -> Unit
) {
    android.util.Log.d("OrderRow", "Rendering order: ${order.id}")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ID
        Text(
            text = order.id.take(8), // Show first 8 characters of ID
            fontSize = 14.sp,
            color = TextPrimary,
            style = Typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(60.dp)
        )

        // Title
        Text(
            text = order.title,
            fontSize = 14.sp,
            color = TextPrimary,
            style = Typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(110.dp)
        )

        // Status
        Text(
            text = order.status,
            fontSize = 14.sp,
            color = TextPrimary,
            style = Typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(100.dp)
        )

        // Price
        Text(
            text = "$${order.price}",
            fontSize = 14.sp,
            color = TextPrimary,
            style = Typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(70.dp)
        )

        // Rate
        Text(
            text = if (order.rate.isNotBlank()) order.rate else "-",
            fontSize = 14.sp,
            color = TextPrimary,
            style = Typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(60.dp)
        )

        // Complete Order Button
        Button(
            onClick = { onComplete() },
            colors = ButtonDefaults.buttonColors(
                containerColor = TealPrimary
            ),
            shape = RoundedCornerShape(6.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            modifier = Modifier.height(36.dp)
        ) {
            Text(
                text = "Complete Order",
                fontSize = 12.sp,
                style = Typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(Modifier.width(16.dp))

        // Delete Button
        Text(
            text = "X",
            color = StatusError,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            style = Typography.bodyMedium,
            modifier = Modifier.clickable { onDelete() }
        )
    }
}

@Composable
fun PageTipsSection(tips: List<String> = emptyList()) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BorderFocused),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TextPrimary, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                    .padding(12.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = TealPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Page Tips",
                        color = Color.White,
                        fontSize = 16.sp,
                        style = Typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(Modifier.padding(16.dp)) {
                tips.forEach { tip ->
                    TipItem(tip)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun TipItem(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = "•",
            fontSize = 14.sp,
            color = TextPrimary,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = text,
            fontSize = 14.sp,
            color = TextPrimary,
            style = Typography.bodyMedium,
            lineHeight = 20.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    DashboardScreen()
}
