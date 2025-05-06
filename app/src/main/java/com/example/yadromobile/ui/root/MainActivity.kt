package com.example.yadromobile.ui.root

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.yadromobile.R
import com.example.yadromobile.presentation.ContactsViewModel
import com.example.yadromobile.ui.contactsScreen.ContactListScreen
import com.example.yadromobile.ui.permissionsScreen.PermissionHandler
import com.example.yadromobile.ui.theme.YadroMobileTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YadroMobileTheme {
                ScreenPicker()
            }

        }
    }

    @Composable
    private fun ScreenPicker() {
        val viewModel: ContactsViewModel = hiltViewModel()
        val contacts by viewModel.groupedContacts.collectAsState()
        val permissionsGranted by viewModel.permissionsGranted.collectAsState()
        val isLoading by viewModel.isLoading.collectAsState()
        Scaffold(modifier = Modifier.background(MaterialTheme.colorScheme.primary)) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when {
                    !permissionsGranted -> {
                        PermissionHandler { granted ->
                            viewModel.updatePermissionsStatus(granted)
                        }
                    }
                    isLoading -> {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.tertiary, modifier = Modifier.align(Alignment.Center))
                    }
                    contacts.isEmpty() -> {
                        Text(stringResource(R.string.contacts_not_found), modifier = Modifier.align(Alignment.Center))
                    }

                    else -> {
                        ContactListScreen(
                            groupedContacts = contacts,
                            onContactClick = ::makePhoneCall
                        )
                    }
                }
            }
        }
    }

    private fun makePhoneCall(phoneNumber: String) {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val intent = Intent(Intent.ACTION_CALL).apply {
                data = Uri.parse("tel:$phoneNumber")
            }
            startActivity(intent)
        }
    }
}

