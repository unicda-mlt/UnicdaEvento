package com.util

import com.google.firebase.Timestamp


class FirestoreUtils {
    companion object {
        val MAX_FIRESTORE_TIMESTAMP = Timestamp(253402300799, 999999999)
    }
}