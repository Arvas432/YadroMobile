package com.example.yadromobile.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.ContactsContract
import androidx.core.content.ContextCompat
import com.example.yadromobile.domain.ContactRepository
import com.example.yadromobile.domain.model.Contact
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactRepositoryImpl @Inject constructor(@ApplicationContext private val context: Context) :
    ContactRepository {
    override suspend fun getContacts(): List<Contact> = withContext(Dispatchers.IO) {
        val contacts = mutableListOf<Contact>()
        val projection = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.PHOTO_URI
        )

        context.contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            projection,
            null,
            null,
            null
        )?.use { cursor ->
            val idColumnIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID)
            val nameColumnIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            val photoColumnIndex = cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI)
            if (idColumnIndex == -1 || nameColumnIndex == -1) {
                return@withContext emptyList()
            }

            while (cursor.moveToNext()) {
                val id = cursor.getString(idColumnIndex)
                val fullName = cursor.getString(nameColumnIndex) ?: continue
                val nameParts = fullName.split(" ").filter { it.isNotBlank() }

                val photoUriString = cursor.getString(photoColumnIndex)
                val photoUri = photoUriString?.let { Uri.parse(it) }

                if (nameParts.isEmpty()) continue

                getMobilePhoneNumber(id)?.let { phoneNumber ->
                    contacts.add(
                        Contact(
                            name = nameParts.first(),
                            surname = nameParts.drop(1).joinToString(" "),
                            phoneNumber = phoneNumber,
                            photoUri = photoUri
                        )
                    )
                }
            }
        }
        return@withContext contacts.sortedWith(
            compareBy({ it.name.firstOrNull()?.uppercaseChar() ?: '#' }, { it.name })
        )
    }

    private fun getMobilePhoneNumber(contactId: String): String? {
        val phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.TYPE
        )

        return context.contentResolver.query(
            phoneUri,
            projection,
            "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
            arrayOf(contactId),
            null
        )?.use { cursor ->
            val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val typeIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE)

            if (numberIndex == -1 || typeIndex == -1) return@use null

            var mobileNumber: String? = null
            while (cursor.moveToNext() && mobileNumber == null) {
                val type = cursor.getInt(typeIndex)
                if (type == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE) {
                    mobileNumber = cursor.getString(numberIndex)?.takeIf { it.isNotBlank() }
                }
            }
            mobileNumber
        }
    }
    override fun checkPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
    }
}