package com.jonathan.showcase.notes

import kotlinx.coroutines.delay
import java.io.IOException
import java.util.UUID

// "Servidor" simulado (em memória). O upsert é por id (idempotente) com last-write-wins
// por version — mandar a mesma nota 2x não gera redundância. `online` simula perda de rede.
object FakeServer {
    data class Row(val id: String, val text: String, val version: Int, val updatedAt: Long, val deleted: Boolean)

    private val store = LinkedHashMap<String, Row>()

    @Volatile var online: Boolean = true

    suspend fun upsertBatch(rows: List<Row>): Set<String> {
        delay(700) // latência de rede
        if (!online) throw IOException("sem conexão")
        val accepted = HashSet<String>()
        for (r in rows) {
            val current = store[r.id]
            if (current == null || r.version >= current.version) {
                store[r.id] = r   // upsert idempotente, last-write-wins
            }
            accepted.add(r.id)    // o servidor reconhece o id (mesmo se já estava igual)
        }
        return accepted
    }

    fun size(): Int = store.values.count { !it.deleted }
}

data class SyncResult(val attempted: Int, val synced: Int, val ok: Boolean)

// Repositório offline-first: a UI sempre lê/escreve o LOCAL (Room). O sync empurra só o
// DELTA (linhas PENDING) num único request em lote; nada de reenviar a base inteira.
class NotesRepository(private val dao: NoteDao) {

    val notes = dao.observeVisible()
    val pendingCount = dao.pendingCount()

    suspend fun add(text: String) {
        val now = System.currentTimeMillis()
        dao.upsert(NoteEntity(UUID.randomUUID().toString(), text, now, 1, "PENDING"))
    }

    suspend fun edit(id: String, text: String) {
        val cur = dao.byId(id) ?: return
        dao.upsert(cur.copy(text = text, updatedAt = System.currentTimeMillis(), version = cur.version + 1, syncState = "PENDING"))
    }

    suspend fun delete(id: String) {
        val cur = dao.byId(id) ?: return
        dao.upsert(cur.copy(deleted = true, updatedAt = System.currentTimeMillis(), version = cur.version + 1, syncState = "PENDING"))
    }

    suspend fun sync(): SyncResult {
        val pending = dao.pending()
        if (pending.isEmpty()) return SyncResult(0, 0, true)
        return try {
            val rows = pending.map { FakeServer.Row(it.id, it.text, it.version, it.updatedAt, it.deleted) }
            val accepted = FakeServer.upsertBatch(rows)
            var marked = 0
            for (p in pending) {
                if (p.id in accepted) {
                    dao.markSynced(p.id, p.version) // só casa se a versão não mudou durante o envio
                    marked++
                }
            }
            SyncResult(pending.size, marked, true)
        } catch (e: Exception) {
            SyncResult(pending.size, 0, false) // offline: nada sobe, tudo segue PENDENTE
        }
    }
}
