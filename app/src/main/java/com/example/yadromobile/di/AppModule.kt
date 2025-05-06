package com.example.yadromobile.di

import com.example.yadromobile.data.ContactRepositoryImpl
import com.example.yadromobile.domain.ContactRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindContactRepository(
        repositoryImpl: ContactRepositoryImpl
    ): ContactRepository
}