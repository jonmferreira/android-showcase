package com.jonathan.showcase.notes

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

// id = UUID gerado no cliente. Ele é a CHAVE DE IDEMPOTÊNCIA: reenviar a mesma nota
// nunca cria duplicata no servidor (upsert por id). version sobe a cada edição →
// resolve conflito por last-write-wins. syncState separa o que ainda está PENDENTE de subir.
@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey val id: String,
    val text: String,
    val updatedAt: Long,
    val version: Int,
    val syncState: String,   // "PENDING" | "SYNCED"
    val deleted: Boolean = false,
)

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes WHERE deleted = 0 ORDER BY updatedAt DESC")
    fun observeVisible(): Flow<List<NoteEntity>>

    @Query("SELECT COUNT(*) FROM notes WHERE syncState = 'PENDING'")
    fun pendingCount(): Flow<Int>

    @Query("SELECT * FROM notes WHERE syncState = 'PENDING'")
    suspend fun pending(): List<NoteEntity>

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun byId(id: String): NoteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(note: NoteEntity)

    // Só marca SYNCED se a versão não mudou desde o envio. Se o usuário editou durante o
    // sync, a versão subiu, esta linha não casa, e a nota segue PENDENTE (re-sincroniza).
    @Query("UPDATE notes SET syncState = 'SYNCED' WHERE id = :id AND version = :version")
    suspend fun markSynced(id: String, version: Int)
}

@Database(entities = [NoteEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile private var instance: AppDatabase? = null
        fun get(context: Context): AppDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext, AppDatabase::class.java, "showcase.db",
            ).build().also { instance = it }
        }
    }
}
