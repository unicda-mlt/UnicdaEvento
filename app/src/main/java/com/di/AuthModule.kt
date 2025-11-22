package com.di

import android.content.Context
import androidx.credentials.CredentialManager
import com.repository.auth.AuthRepository
import com.repository.auth.FirebaseAuthRepository
import com.example.unicdaevento.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.firebase.auth.FirebaseAuth
import com.repository.user.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        auth: FirebaseAuth,
        userRepository: UserRepository,
        @ApplicationScope externalScope: CoroutineScope
    ): AuthRepository = FirebaseAuthRepository(auth, userRepository, externalScope)

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