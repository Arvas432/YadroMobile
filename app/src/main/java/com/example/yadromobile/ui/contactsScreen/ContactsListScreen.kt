package com.example.yadromobile.ui.contactsScreen

import android.graphics.BitmapFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.yadromobile.domain.model.Contact

@Composable
fun ContactListScreen(
    groupedContacts: Map<Char, List<Contact>>,
    onContactClick: (String) -> Unit
) {
    LazyColumn(modifier = Modifier.background(MaterialTheme.colorScheme.primary)) {
        groupedContacts.forEach { (initial, contactList) ->
            stickyHeader {
                HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.tertiary)
                Text(
                    text = initial.toString(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(8.dp),
                    style = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.onPrimary)
                )
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.padding(bottom = 5.dp)
                )
            }

            items(contactList) { contact ->
                ContactListItem(
                    contact = contact,
                    onItemClick = { onContactClick(contact.phoneNumber) },
                    modifier = Modifier.padding(start = 20.dp, end = 10.dp, bottom = 5.dp)
                )
            }
        }
    }
}

@Composable
private fun ContactListItem(
    contact: Contact,
    onItemClick: () -> Unit,
    modifier: Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary
        ),
        shape = RoundedCornerShape(corner = CornerSize(5.dp)),
        onClick = onItemClick
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            val context = LocalContext.current
            val imageBitmap = remember(contact.photoUri) {
                contact.photoUri?.let {
                    try {
                        val inputStream = context.contentResolver.openInputStream(it)
                        inputStream?.use { stream ->
                            BitmapFactory.decodeStream(stream)?.asImageBitmap()
                        }
                    } catch (e: Exception) {
                        null
                    }
                }
            }

            if (imageBitmap != null) {
                Image(
                    bitmap = imageBitmap,
                    contentDescription = "Contact Photo",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Default Avatar",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape),
                    tint = MaterialTheme.colorScheme.tertiary
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = "${contact.name} ${contact.surname}".trim(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondary
                )
                Text(
                    text = contact.phoneNumber,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
        }

    }
}