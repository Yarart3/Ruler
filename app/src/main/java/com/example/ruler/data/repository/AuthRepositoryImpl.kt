package com.example.ruler.data.repository

import android.content.Context
import com.example.ruler.domain.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.tasks.await

@Singleton
class AuthRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AuthRepository {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun hasAuthenticatedUser(): Boolean {
        return isFirebaseConfigured() && auth.currentUser != null
    }

    override fun isFirebaseConfigured(): Boolean {
        return context.resources.getIdentifier(
            "google_app_id",
            "string",
            context.packageName
        ) != 0
    }

    override suspend fun signIn(email: String, password: String): Result<Unit> {
        if (!isFirebaseConfigured()) {
            return Result.failure(IllegalStateException("Firebase configuration is missing"))
        }

        return runCatching {
            auth.signInWithEmailAndPassword(email, password).await()
            Unit
        }
    }
}
