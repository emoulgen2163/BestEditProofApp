package com.emoulgen.vibecodingapp.ui.screens.orderFlow

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.emoulgen.vibecodingapp.R
import com.emoulgen.vibecodingapp.domain.model.OrderDraft
import com.emoulgen.vibecodingapp.ui.theme.BorderFocused
import com.emoulgen.vibecodingapp.ui.theme.PayPalYellow
import com.emoulgen.vibecodingapp.ui.theme.TealPrimary
import com.emoulgen.vibecodingapp.ui.theme.TextSecondary
import com.emoulgen.vibecodingapp.ui.theme.Typography

@Composable
fun OrderPaymentStep(
    orderDraft: OrderDraft,
    orderId: String = "", // Pass the created order ID from summary step
    navController: NavController = rememberNavController(),
    onPay: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var card by remember { mutableStateOf("") }
    var month by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var cvc by remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        border = BorderStroke(1.dp, BorderFocused),
        colors = CardDefaults.cardColors(Color.Transparent)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(painter = painterResource(R.drawable.image_1), contentDescription = null)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = name, onValueChange = { name = it },
                label = {
                    Text(
                        text = "Card Holder Name",
                        style = Typography.bodyLarge,
                        color = TextSecondary
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = TextSecondary,
                    focusedBorderColor = TextSecondary
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = card, onValueChange = { card = it },
                label = {
                    Text(
                        text = "Card Number",
                        style = Typography.bodyLarge,
                        color = TextSecondary
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = TextSecondary,
                    focusedBorderColor = TextSecondary
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            Row {
                OutlinedTextField(
                    value = month, onValueChange = { month = it },
                    label = {
                        Text(
                            text = "Month",
                            style = Typography.bodyLarge,
                            color = TextSecondary
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = TextSecondary,
                        focusedBorderColor = TextSecondary
                    ),
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                OutlinedTextField(
                    value = year, onValueChange = { year = it },
                    label = {
                        Text(
                            text = "Year",
                            style = Typography.bodyLarge,
                            color = TextSecondary
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = TextSecondary,
                        focusedBorderColor = TextSecondary
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = cvc, onValueChange = { cvc = it },
                label = {
                    Text(
                        text = "CVC",
                        style = Typography.bodyLarge,
                        color = TextSecondary
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = TextSecondary,
                    focusedBorderColor = TextSecondary
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    // Process credit card payment
                    // TODO: Implement actual payment processing
                    onPay()
                },
                modifier = Modifier.width(180.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(TealPrimary)
            ) {
                Text(
                    text = "Pay",
                    style = Typography.titleSmall
                )
            }
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    // Navigate to PayPal WebView
                    val encodedTitle = Uri.encode(orderDraft.projectTitle)
                    navController.navigate(
                        "paypal_webview/${orderDraft.price}/$encodedTitle/$orderId"
                    )
                },
                colors = ButtonDefaults.buttonColors(PayPalYellow),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.width(180.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Check out with ",
                        color = Color.Black,
                        style = Typography.titleSmall,
                        fontSize = 14.sp
                    )
                    Spacer(Modifier.width(4.dp))
                    Image(
                        painter = painterResource(R.drawable.paypal),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OrderPaymentPreview() {
    OrderPaymentStep(
        OrderDraft(),
        navController = rememberNavController(),
        onPay = { }
    )
}