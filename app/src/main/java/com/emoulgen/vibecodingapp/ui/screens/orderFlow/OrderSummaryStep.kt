package com.emoulgen.vibecodingapp.ui.screens.orderFlow

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emoulgen.vibecodingapp.domain.model.CreateOrderRequest
import com.emoulgen.vibecodingapp.domain.model.OrderDraft
import com.emoulgen.vibecodingapp.ui.screens.main.PageTipsSection
import com.emoulgen.vibecodingapp.ui.theme.TealPrimary
import com.emoulgen.vibecodingapp.ui.theme.TextSecondary
import com.emoulgen.vibecodingapp.ui.theme.Typography
import com.emoulgen.vibecodingapp.ui.viewModel.FirestoreOrderViewModel
import com.emoulgen.vibecodingapp.utils.Resource

@Composable
fun OrderSummaryStep(
    orderDraft: OrderDraft,
    existingOrderId: String? = null,
    onNext: () -> Unit,
    onBack: () -> Unit,
    orderViewModel: FirestoreOrderViewModel = hiltViewModel()
) {
    val tips = listOf(
        "Check your project information before proceeding with payment step.",
        "Click the next button to proceed payment."
    )

    var isProcessing by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            border = BorderStroke(1.dp, Color.Black)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    text = "Order Summary",
                    style = Typography.titleLarge
                )

                Spacer(Modifier.height(12.dp))

                // All your order details display code...
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "•",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(
                        text = "Type of Services: ",
                        style = Typography.bodyLarge
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = orderDraft.serviceType,
                    style = Typography.bodyMedium,
                    color = TextSecondary
                )

                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("•", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.width(5.dp))
                    Text(
                        text = "Due Date: ",
                        style = Typography.bodyLarge
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = orderDraft.deliveryTime,
                    style = Typography.bodyMedium,
                    color = TextSecondary
                )

                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("•", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.width(5.dp))
                    Text(
                        text = "Number of Words: ",
                        style = Typography.bodyLarge
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = orderDraft.wordCount.toString(),
                    style = Typography.bodyMedium,
                    color = TextSecondary
                )

                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("•", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.width(5.dp))
                    Text(
                        text = "Project Title: ",
                        style = Typography.bodyLarge
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = orderDraft.projectTitle,
                    style = Typography.bodyMedium,
                    color = TextSecondary
                )

                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("•", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.width(5.dp))
                    Text(
                        text = "English Variant: ",
                        style = Typography.bodyLarge
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = orderDraft.englishVariant,
                    style = Typography.bodyMedium,
                    color = TextSecondary
                )

                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("•", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.width(5.dp))
                    Text(
                        text = "Project Description: ",
                        style = Typography.bodyLarge
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = orderDraft.projectDescription.ifBlank { "None" },
                    style = Typography.bodyMedium,
                    color = TextSecondary
                )

                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("•", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.width(5.dp))
                    Text(
                        text = "File: ",
                        style = Typography.bodyLarge
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = orderDraft.fileName ?: "None",
                    style = Typography.bodyMedium,
                    color = TextSecondary
                )

                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("•", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.width(5.dp))
                    Text(
                        text = "Total Amount to be Paid : ",
                        style = Typography.bodyLarge
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "$${orderDraft.price} (USD)",
                    style = Typography.bodyMedium,
                    color = TextSecondary
                )

                Spacer(Modifier.height(16.dp))
            }
        }

        Spacer(Modifier.height(16.dp))

        // Show error message if any
        errorMessage?.let { error ->
            Text(
                text = error,
                color = Color.Red,
                style = Typography.bodyMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        PageTipsSection(tips)

        Row(Modifier.fillMaxWidth()) {
            Button(
                onClick = { onBack() },
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                shape = RoundedCornerShape(8.dp),
                enabled = !isProcessing
            ) {
                Text(text = "Back", style = Typography.bodyLarge)
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = {
                    // Order already created/updated in Step 2
                    // Just proceed to payment
                    android.util.Log.d("OrderSummary", "Proceeding to payment step")
                    onNext()
                },
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                shape = RoundedCornerShape(8.dp),
                enabled = !isProcessing
            ) {
                Text(
                    text = "Proceed to Payment",
                    style = Typography.bodyLarge
                )
            }
        }
    }
}