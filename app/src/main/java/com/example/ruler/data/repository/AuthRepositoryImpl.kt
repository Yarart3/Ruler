package com.example.ruler.data.repository

import android.content.Context
import android.util.Log
import com.example.ruler.domain.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.tasks.await

@Singleton
class AuthRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AuthRepository {

    private val tag = "AuthRepository"
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun hasAuthenticatedUser(): Boolean {
        return isFirebaseConfigured() && auth.currentUser?.isEmailVerified == true
    }

    override fun isFirebaseConfigured(): Boolean {
        return context.resources.getIdentifier(
            "google_app_id",
            "string",
            context.packageName
        ) != 0
    }

    override fun currentUserId(): String? = auth.currentUser?.uid

    override fun currentUserEmail(): String? = auth.currentUser?.email

    override fun currentUsername(): String? = auth.currentUser?.displayName

    override suspend fun signIn(email: String, password: String): Result<Unit> {
        if (!isFirebaseConfigured()) {
            Log.e(tag, "signIn: Firebase no configurat")
            return Result.failure(IllegalStateException("Firebase configuration is missing"))
        }

        return runCatching {
            Log.i(tag, "signIn: intent de login amb email=$email")
            auth.signInWithEmailAndPassword(email, password).await()
            val user = auth.currentUser ?: error("Authenticated user is missing")
            user.reload().await()
            if (!user.isEmailVerified) {
                Log.w(tag, "signIn: email no verificat per email=$email")
                auth.signOut()
                error("Verify your email before signing in")
            }
            Log.i(tag, "signIn: login correcte per email=$email")
            Unit
        }.also { result ->
            if (result.isFailure) {
                Log.e(tag, "signIn: error → ${result.exceptionOrNull()?.localizedMessage}")
            }
        }
    }

    override suspend fun register(username: String, email: String, password: String): Result<Unit> {
        if (!isFirebaseConfigured()) {
            Log.e(tag, "register: Firebase no configurat")
            return Result.failure(IllegalStateException("Firebase configuration is missing"))
        }

        return runCatching {
            Log.i(tag, "register: creant usuari amb email=$email")
            auth.createUserWithEmailAndPassword(email, password).await()
            val user = auth.currentUser ?: error("Registered user is missing")
            user.updateProfile(
                userProfileChangeRequest {
                    displayName = username.trim()
                }
            ).await()
            user.sendEmailVerification().await()
            Log.i(tag, "register: perfil i email de verificació configurats per $email")
            auth.signOut()
            Unit
        }.also { result ->
            if (result.isFailure) {
                Log.e(tag, "register: error → ${result.exceptionOrNull()?.localizedMessage}")
            }
        }
    }

    override fun signOut() {
        val email = auth.currentUser?.email ?: "desconegut"
        Log.i(tag, "signOut: tancant sessió de email=$email")
        auth.signOut()
        Log.i(tag, "signOut: sessió tancada correctament")
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        if (!isFirebaseConfigured()) {
            return Result.failure(IllegalStateException("Firebase configuration is missing"))
        }
        return runCatching {
            auth.sendPasswordResetEmail(email).await()
            Unit
        }
    }
}
