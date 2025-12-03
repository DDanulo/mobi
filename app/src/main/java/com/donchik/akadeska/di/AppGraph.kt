package com.donchik.akadeska.di

import com.donchik.akadeska.data.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.messaging.FirebaseMessaging

object AppGraph {
    val auth by lazy { FirebaseAuth.getInstance() }
    val db by lazy { FirebaseFirestore.getInstance() }
    val storage by lazy { FirebaseStorage.getInstance() }
    val messaging by lazy { FirebaseMessaging.getInstance() }

//    val repo by lazy { FirebaseRepository(auth, db, storage) }

    val repo by lazy { FirebaseRepository(auth, db, storage, messaging) }
}