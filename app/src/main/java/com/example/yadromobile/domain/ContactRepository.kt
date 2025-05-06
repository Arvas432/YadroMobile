package com.example.yadromobile.domain

import com.example.yadromobile.domain.model.Contact

interface ContactRepository {
    suspend fun getContacts(): List<Contact>
    fun checkPermissions(): Boolean
}