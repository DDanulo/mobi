package com.donchik.akadeska.utils
import com.google.firebase.firestore.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

fun Query.snapshotsFlow() = callbackFlow {
    val reg = addSnapshotListener { snap, err ->
        if (err != null) { close(err); return@addSnapshotListener }
        if (snap != null) trySend(snap)
    }
    awaitClose { reg.remove() }
}