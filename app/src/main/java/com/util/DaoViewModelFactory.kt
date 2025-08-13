package com.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.database.AppDb

fun <VM : ViewModel> daoViewModelFactory(
    create: (db: AppDb) -> VM,
    db: AppDb
): ViewModelProvider.Factory {
    return object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return create(db) as T
        }
    }
}