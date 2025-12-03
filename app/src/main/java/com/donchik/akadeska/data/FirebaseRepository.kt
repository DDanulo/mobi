package com.donchik.akadeska.data

import com.donchik.akadeska.domain.model.PostType
import com.donchik.akadeska.domain.model.PostWrite
import com.donchik.akadeska.utils.snapshotsFlow
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

data class FeedItem(
    val id: String,
    val title: String,
    val imageUrl: String?
)

data class ShopItem(
    val id: String,
    val title: String,
    val description: String,
    val price: Double?,
    val imageUrl: String?,
    val sellerId: String?
)

data class PostDetails(
    val id: String,
    val type: String,
    val title: String,
    val body: String,
    val imageUrl: String?,
    val createdAt: com.google.firebase.Timestamp?
)

fun Query.snapshotsFlow() = callbackFlow {
    val reg = addSnapshotListener { snap, err ->
        if (err != null) {
            close(err); return@addSnapshotListener
        }
        if (snap != null) trySend(snap)
    }
    awaitClose { reg.remove() }
}

fun DocumentReference.snapshotsFlow() = callbackFlow {
    val reg = addSnapshotListener { snap, err ->
        if (err != null) {
            close(err); return@addSnapshotListener
        }
        if (snap != null) trySend(snap)
    }
    awaitClose { reg.remove() }
}

class FirebaseRepository(
    val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage
) {
    val currentUser get() = auth.currentUser

    suspend fun signIn(email: String, password: String): Result<Unit> = runCatching {
        auth.signInWithEmailAndPassword(email.trim(), password).await()
    }

    suspend fun signUp(email: String, password: String): Result<Unit> = runCatching {
        auth.createUserWithEmailAndPassword(email.trim(), password).await()
    }

    fun signOut() {
        auth.signOut()
    }

    fun authStateFlow(): kotlinx.coroutines.flow.Flow<Boolean> =
        kotlinx.coroutines.flow.callbackFlow {
            val listener = FirebaseAuth.AuthStateListener { trySend(it.currentUser != null) }
            auth.addAuthStateListener(listener)
            awaitClose { auth.removeAuthStateListener(listener) }
        }

    suspend fun createPost(
        type: PostType,
        title: String,
        body: String,
        price: Double?,
        imageBytes: ByteArray? // null = bez obrazka
    ): Result<String> = runCatching {
        val uid = auth.currentUser?.uid ?: error("Not signed in")

        // 1) utwórz doc id (przyda się do ścieżki w Storage)
        val docRef = db.collection("posts").document()
        val id = docRef.id

        // 2) jeśli jest obrazek → wrzuć do Storage i pobierz URL
        val imageUrl = if (imageBytes != null) {
            val ref = storage.reference.child("posts/$id/main.jpg")
            ref.putBytes(imageBytes).await()
            ref.downloadUrl.await().toString()
        } else null

        // 3) zapisz dokument
        val post = PostWrite(
            type = type,
            title = title.trim(),
            body = body.trim(),
            price = if (type == PostType.LISTING) price else null,
            status = "pending",
            createdBy = uid,
            createdAt = Timestamp.now(),
            imageUrl = imageUrl
        )
        docRef.set(post, SetOptions.merge()).await()
        id
    }

    fun isAdminFlow(): kotlinx.coroutines.flow.Flow<Boolean> =
        kotlinx.coroutines.flow.callbackFlow {
            val l = FirebaseAuth.AuthStateListener { fa ->
                val user = fa.currentUser
                if (user == null) trySend(false) else {
                    user.getIdToken(/* forceRefresh = */ true)
                        .addOnSuccessListener { result ->
                            trySend(result.claims["role"] == "admin")
                        }.addOnFailureListener { trySend(false) }
                }
            }
            auth.addAuthStateListener(l)
            awaitClose { auth.removeAuthStateListener(l) }
        }

    fun observePendingPosts() =
        db.collection("posts")
            .whereEqualTo("status", "pending")
            .orderBy("createdAt")
            .snapshotsFlow() // extension below
            .map { qs -> qs.documents.map { it.id to it.data!! } }

    suspend fun approvePost(id: String) = runCatching {
        db.collection("posts").document(id)
            .update(mapOf("status" to "approved")).await()
    }

    suspend fun rejectPost(id: String) = runCatching {
        db.collection("posts").document(id)
            .update(mapOf("status" to "rejected")).await()
    }

    fun observeApprovedAnnouncements(): Flow<List<FeedItem>> {
        // NOTE: we store enum names ("INFO","EVENT","LISTING") in Firestore (from PostWrite)
        return db.collection("posts")
            .whereEqualTo("status", "approved")
            .whereIn("type", listOf("INFO", "EVENT"))
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshotsFlow()
            .map { qs ->
                qs.documents.map { d ->
                    FeedItem(
                        id = d.id,
                        title = d.getString("title") ?: "(no title)",
                        imageUrl = d.getString("imageUrl")
                    )
                }
            }
    }

    fun observePost(id: String): Flow<PostDetails?> =
        db.collection("posts").document(id)
            .snapshotsFlow()
            .map { d ->
                if (!d.exists()) null else PostDetails(
                    id = d.id,
                    type = (d.getString("type") ?: "").uppercase(),
                    title = d.getString("title") ?: "",
                    body = d.getString("body") ?: "",
                    imageUrl = d.getString("imageUrl"),
                    createdAt = d.getTimestamp("createdAt")
                )
            }
    private fun getCutoffTimestamp(): Timestamp {
        val calendar = java.util.Calendar.getInstance()
        calendar.add(java.util.Calendar.DAY_OF_YEAR, -14)
        return Timestamp(calendar.time)
    }

    // 2. Modified: Only shows posts NEWER than 14 days
    fun observeRecentPosts(): Flow<List<FeedItem>> {
        val cutoff = getCutoffTimestamp()
        return db.collection("posts")
            .whereEqualTo("status", "approved")
            .whereIn("type", listOf("INFO", "EVENT"))
            .whereGreaterThan("createdAt", cutoff) // > 14 days ago
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshotsFlow()
            .map { qs ->
                qs.documents.map { d ->
                    FeedItem(
                        id = d.id,
                        title = d.getString("title") ?: "(no title)",
                        imageUrl = d.getString("imageUrl")
                    )
                }
            }
    }

    // 3. New: Only shows posts OLDER than 14 days
    fun observeArchivedPosts(): Flow<List<FeedItem>> {
        val cutoff = getCutoffTimestamp()
        return db.collection("posts")
            .whereEqualTo("status", "approved")
            .whereIn("type", listOf("INFO", "EVENT"))
            .whereLessThan("createdAt", cutoff) // < 14 days ago
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshotsFlow()
            .map { qs ->
                qs.documents.map { d ->
                    FeedItem(
                        id = d.id,
                        title = d.getString("title") ?: "(no title)",
                        imageUrl = d.getString("imageUrl")
                    )
                }
            }
    }

    suspend fun updateUserName(name: String): Result<Unit> = runCatching {
        val user = auth.currentUser ?: error("Not signed in")
        val updates = com.google.firebase.auth.userProfileChangeRequest {
            displayName = name
        }
        user.updateProfile(updates).await()
    }

    fun observeShopListings(): Flow<List<ShopItem>> {
        // We filter by approved status and type LISTING
        return db.collection("posts")
            .whereEqualTo("status", "approved")
            .whereEqualTo("type", "LISTING")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshotsFlow()
            .map { qs ->
                qs.documents.map { d ->
                    ShopItem(
                        id = d.id,
                        title = d.getString("title") ?: "",
                        description = d.getString("body") ?: "",
                        price = d.getDouble("price"),
                        imageUrl = d.getString("imageUrl"),
                        sellerId = d.getString("createdBy")
                    )
                }
            }
    }


}