package com.example.yadromobile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yadromobile.domain.ContactRepository
import com.example.yadromobile.domain.model.Contact
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val repository: ContactRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _groupedContacts = MutableStateFlow<Map<Char, List<Contact>>>(emptyMap())
    val groupedContacts: StateFlow<Map<Char, List<Contact>>> = _groupedContacts

    private val _permissionsGranted = MutableStateFlow(false)
    val permissionsGranted: StateFlow<Boolean> = _permissionsGranted

    fun updatePermissionsStatus(isGranted: Boolean) {
        _permissionsGranted.value = isGranted
        if (isGranted) {
            loadContacts()
        }
    }

    private fun loadContacts() {
        viewModelScope.launch {
            _isLoading.value = true
            if (repository.checkPermissions()) {
                val loadedContacts = repository.getContacts()

                _groupedContacts.value = loadedContacts
                    .sortedWith(compareBy({ it.name.firstOrNull()?.uppercaseChar() ?: '#' }, { it.name }))
                    .groupBy { it.name.firstOrNull()?.uppercaseChar() ?: '#' }
                _isLoading.value = false
            }
        }
    }

}