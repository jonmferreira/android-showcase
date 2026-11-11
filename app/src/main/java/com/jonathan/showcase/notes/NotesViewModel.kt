package com.jonathan.showcase.notes

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotesViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = NotesRepository(AppDatabase.get(app).noteDao())

    val notes: StateFlow<List<NoteEntity>> =
        repo.notes.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val pending: StateFlow<Int> =
        repo.pendingCount.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    var online by mutableStateOf(FakeServer.online); private set
    var status by mutableStateOf(""); private set
    var syncing by mutableStateOf(false); private set

    fun toggleOnline() {
        online = !online
        FakeServer.online = online
        status = if (online) "Online" else "Offline — a fila acumula localmente"
    }

    fun add(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch { repo.add(text.trim()) }
    }

    fun bumpEdit(id: String, text: String) {
        viewModelScope.launch { repo.edit(id, "$text ·") }
    }

    fun delete(id: String) {
        viewModelScope.launch { repo.delete(id) }
    }

    fun sync() {
        viewModelScope.launch {
            syncing = true
            status = "Sincronizando..."
            val r = repo.sync()
            syncing = false
            status = when {
                !r.ok -> "Falhou (offline): ${r.attempted} na fila"
                r.attempted == 0 -> "Nada a sincronizar"
                else -> "Subiu ${r.synced}/${r.attempted} · servidor tem ${FakeServer.size()}"
            }
        }
    }
}
