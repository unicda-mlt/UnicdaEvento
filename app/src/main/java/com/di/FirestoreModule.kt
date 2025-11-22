package com.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.repository.department.DepartmentRepository
import com.repository.department.FirestoreDepartmentRepository
import com.repository.event.EventRepository
import com.repository.event.FirestoreEventRepository
import com.repository.event_category.EventCategoryRepository
import com.repository.event_category.FirestoreEventCategoryRepository
import com.repository.user.FirestoreUserRepository
import com.repository.user.UserRepository
import com.repository.user_role.FirestoreUserRoleRepository
import com.repository.user_role.UserRoleRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirestoreModule {

    @Provides
    @Singleton
    fun provideUserRepository(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth,
        @CoroutineDispatchersModule.IoDispatcher dispatcher: CoroutineDispatcher,
    ): UserRepository = FirestoreUserRepository(
        db = firestore,
        auth = auth,
        dispatcher = dispatcher
    )

    @Provides
    @Singleton
    fun provideDepartmentRepository(
        firestore: FirebaseFirestore,
        @CoroutineDispatchersModule.IoDispatcher dispatcher: CoroutineDispatcher,
    ): DepartmentRepository = FirestoreDepartmentRepository(
        db = firestore,
        dispatcher = dispatcher
    )

    @Provides
    @Singleton
    fun provideEventCategoryRepository(
        firestore: FirebaseFirestore,
        @CoroutineDispatchersModule.IoDispatcher dispatcher: CoroutineDispatcher,
    ): EventCategoryRepository = FirestoreEventCategoryRepository(
        db = firestore,
        dispatcher = dispatcher
    )

    @Provides
    @Singleton
    fun provideEventRepository(
        firestore: FirebaseFirestore,
        @CoroutineDispatchersModule.IoDispatcher dispatcher: CoroutineDispatcher,
    ): EventRepository = FirestoreEventRepository(
        db = firestore,
        dispatcher = dispatcher
    )

    @Provides
    @Singleton
    fun provideUserRoleRepository(
        firestore: FirebaseFirestore,
    ): UserRoleRepository = FirestoreUserRoleRepository(
        db = firestore
    )
}