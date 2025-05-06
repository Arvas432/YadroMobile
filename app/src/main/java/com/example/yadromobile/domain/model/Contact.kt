package com.example.yadromobile.domain.model

import android.net.Uri

data class Contact(
    val name: String,
    val surname: String,
    val phoneNumber: String,
    val photoUri: Uri? = null
)
