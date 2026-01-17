package com.emoulgen.vibecodingapp.dependencyInjection


import android.content.Context
import com.emoulgen.vibecodingapp.data.repository.FirestoreOrderRepositoryImplementation
import com.emoulgen.vibecodingapp.domain.repository.FirestoreOrderRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HiltModule {

    // 🔹 Provide FirebaseFirestore
    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    // 🔹 Provide FirebaseAuth
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }

    /**
     * Provides Firestore Order Repository
     * Used for: Order CRUD operations, Real-time order updates
     * Dependencies: Firestore, FirebaseAuth
     */
    @Provides
    @Singleton
    fun provideFirestoreOrderRepository(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth
    ): FirestoreOrderRepository {
        return FirestoreOrderRepositoryImplementation(firestore, auth)
    }
}