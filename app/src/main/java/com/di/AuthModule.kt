package com.di

import android.content.Context
import androidx.credentials.CredentialManager
import com.auth.AuthRepository
import com.auth.FirebaseAuthRepository
import com.example.unicdaevento.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.repository.user.FirestoreUserRepository
import com.repository.user.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        auth: FirebaseAuth
    ): AuthRepository = FirebaseAuthRepository(
        auth = auth,
    )

    @Provides
    @Singleton
    fun provideCredentialManager(@ApplicationContext context: Context): CredentialManager =
        CredentialManager.Companion.create(context)

    @Provides
    @Singleton
    fun provideGoogleIdOption(@ApplicationContext context: Context): GetGoogleIdOption =
        GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false) // show all Google accounts
            .setServerClientId(context.getString(R.string.default_web_client_id))
            .build()

}