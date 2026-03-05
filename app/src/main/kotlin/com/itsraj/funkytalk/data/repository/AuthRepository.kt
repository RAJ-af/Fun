package com.itsraj.funkytalk.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.itsraj.funkytalk.data.model.UserProfile
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    val currentUser: FirebaseUser? get() = auth.currentUser

    suspend fun getProfile(uid: String): UserProfile? {
        return try {
            firestore.collection("users").document(uid).get().await().toObject(UserProfile::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun saveProfile(profile: UserProfile) {
        firestore.collection("users").document(profile.uid).set(profile).await()
    }

    suspend fun login(email: String, pass: String): FirebaseUser? {
        return auth.signInWithEmailAndPassword(email, pass).await().user
    }

    suspend fun signup(email: String, pass: String): FirebaseUser? {
        return auth.createUserWithEmailAndPassword(email, pass).await().user
    }

    fun logout() {
        auth.signOut()
    }
}
