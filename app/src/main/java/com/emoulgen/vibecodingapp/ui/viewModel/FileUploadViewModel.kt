package com.emoulgen.vibecodingapp.ui.viewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class FileUploadViewModel @Inject constructor(
    private val storage: FirebaseStorage
) : ViewModel() {

    fun uploadFile(
        uri: Uri,
        fileName: String,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        // Create a unique filename to avoid collisions
        val uniqueFileName = "${UUID.randomUUID()}_$fileName"
        val storageRef = storage.reference.child("project_files/$uniqueFileName")

        storageRef.putFile(uri)
            .addOnSuccessListener {
                // Get download URL after successful upload
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    onSuccess(downloadUri.toString())
                }.addOnFailureListener { exception ->
                    onFailure("Failed to get download URL: ${exception.message}")
                }
            }
            .addOnFailureListener { exception ->
                onFailure("Upload failed: ${exception.message}")
            }
    }
}