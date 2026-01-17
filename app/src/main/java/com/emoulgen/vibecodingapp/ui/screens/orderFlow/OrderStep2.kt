package com.emoulgen.vibecodingapp.ui.screens.orderFlow

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.emoulgen.vibecodingapp.domain.model.OrderDraft
import com.emoulgen.vibecodingapp.ui.screens.main.PageTipsSection
import com.emoulgen.vibecodingapp.ui.theme.TealPrimary
import com.emoulgen.vibecodingapp.ui.theme.TextSecondary
import com.emoulgen.vibecodingapp.ui.theme.Typography
import com.emoulgen.vibecodingapp.ui.viewModel.FileUploadViewModel
import com.emoulgen.vibecodingapp.utils.Constants.englishVariant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderStep2(
    orderDraft: OrderDraft,
    onUpdateDraft: (OrderDraft) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
    fileUploadViewModel: FileUploadViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val tips = listOf(
        "You must fill in the Project Title field.",
        "You must upload only one file.",
        "Select a file having extension with '.doc', '.docx', or '.zip' to upload.",
        "File size must be lower than 20 megabyte(MB) to upload using upload button.",
        "You must send file having size greater than 20 megabyte (MB) to our e-mail address.",
        "It is recommended that you fill in the Project Description field for better explanation.",
        "Click the next button to see order summary."
    )

    var title by remember { mutableStateOf(orderDraft.projectTitle) }
    var variant by remember { mutableStateOf(orderDraft.englishVariant.ifEmpty { englishVariant[0] }) }
    var expandedVariant by remember { mutableStateOf(false) }
    var description by remember { mutableStateOf(orderDraft.projectDescription) }
    var fileName by remember { mutableStateOf(orderDraft.fileName ?: "") }
    var fileUrl by remember { mutableStateOf(orderDraft.fileUrl ?: "") }
    var isUploading by remember { mutableStateOf(false) }
    var uploadError by remember { mutableStateOf<String?>(null) }

    val valid = title.isNotBlank()

    // File picker launcher
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // Get file name from URI
            val name = context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                cursor.moveToFirst()
                cursor.getString(nameIndex)
            } ?: "unknown_file"

            // Check file extension
            val extension = name.substringAfterLast('.', "").lowercase()
            if (extension in listOf("doc", "docx", "zip")) {
                // Check file size (20MB limit)
                val fileSize = context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    val sizeIndex = cursor.getColumnIndex(android.provider.OpenableColumns.SIZE)
                    cursor.moveToFirst()
                    cursor.getLong(sizeIndex)
                } ?: 0L

                val maxSize = 20 * 1024 * 1024 // 20MB in bytes
                if (fileSize <= maxSize) {
                    isUploading = true
                    uploadError = null

                    // Upload file to Firebase Storage
                    fileUploadViewModel.uploadFile(
                        uri = uri,
                        fileName = name,
                        onSuccess = { downloadUrl ->
                            fileName = name
                            fileUrl = downloadUrl
                            isUploading = false
                        },
                        onFailure = { error ->
                            uploadError = error
                            isUploading = false
                        }
                    )
                } else {
                    uploadError = "File size exceeds 20MB. Please send large files to our email address."
                }
            } else {
                uploadError = "Invalid file type. Please select .doc, .docx, or .zip files only."
            }
        }
    }

    Column(Modifier.padding(16.dp)) {
        Text(
            text = "Project Title *",
            color = TextSecondary,
            style = Typography.bodyLarge
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TextSecondary,
                unfocusedBorderColor = TextSecondary
            ),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(Modifier.height(18.dp))

        Text(
            text = "English Variant *",
            color = TextSecondary,
            style = Typography.bodyLarge
        )
        Spacer(Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = expandedVariant,
            onExpandedChange = { expandedVariant = it }
        ) {
            OutlinedTextField(
                value = variant,
                onValueChange = { /* readOnly */ },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TextSecondary,
                    unfocusedBorderColor = TextSecondary
                ),
                shape = RoundedCornerShape(12.dp),
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedVariant) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .clickable { expandedVariant = !expandedVariant }
            )

            ExposedDropdownMenu(
                expanded = expandedVariant,
                onDismissRequest = { expandedVariant = false }
            ) {
                englishVariant.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption) },
                        onClick = {
                            variant = selectionOption
                            expandedVariant = false
                        }
                    )
                }
            }
        }
        Spacer(Modifier.height(18.dp))

        Text(
            text = "Project Description",
            color = TextSecondary,
            style = Typography.bodyLarge
        )
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TextSecondary,
                unfocusedBorderColor = TextSecondary
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    // Launch file picker with document MIME types
                    filePickerLauncher.launch("application/*")
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = TealPrimary
                ),
                shape = RoundedCornerShape(8.dp),
                enabled = !isUploading
            ) {
                if (isUploading) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(4.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(
                        text = "Upload File",
                        style = Typography.bodyLarge
                    )
                }
            }

            if (fileName.isNotEmpty()) {
                Spacer(Modifier.width(8.dp))
                Text(
                    text = fileName,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        // Show upload error if any
        uploadError?.let { error ->
            Spacer(Modifier.height(8.dp))
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

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
                    // Write back to draft with file URL
                    val updated = orderDraft.copy(
                        projectTitle = title,
                        englishVariant = variant,
                        projectDescription = description,
                        fileName = fileName.ifBlank { null },
                        fileUrl = fileUrl.ifBlank { null }
                    )
                    onUpdateDraft(updated)
                    onNext()
                },
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                shape = RoundedCornerShape(8.dp),
                enabled = valid && !isUploading
            ) {
                Text(text = "Next", style = Typography.bodyLarge)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OrderStepPreview() {
    OrderStep2(OrderDraft(), onUpdateDraft = {}, onBack = {}, onNext = {})
}