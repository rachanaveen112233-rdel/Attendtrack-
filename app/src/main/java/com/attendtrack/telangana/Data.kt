package com.attendtrack.telangana

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "subjects")
data class Subject(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val attended: Int = 0,
    val held: Int = 0
)

@Entity(
    tableName = "records",
    indices = [Index(value = ["subjectId", "date"], unique = true)]
)
data class AttRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val subjectId: Int,
    val date: String,
    val present: Boolean
)

@Dao
interface SubjectDao {
    @Query("SELECT * FROM subjects ORDER BY id")
    fun all(): Flow<List<Subject>>

    @Query("SELECT COUNT(*) FROM subjects")
    suspend fun count(): Int

    @Query("SELECT * FROM subjects WHERE id = :id")
    suspend fun getSubject(id: Int): Subject

    @Insert
    suspend fun insertAll(list: List<Subject>)

    @Update
    suspend fun update(s: Subject)

    @Query("SELECT * FROM records WHERE date = :date")
    fun forDate(date: String): Flow<List<AttRecord>>

    @Query("SELECT * FROM records WHERE subjectId = :sid AND date = :date")
    suspend fun find(sid: Int, date: String): AttRecord?

    @Insert
    suspend fun insertRecord(r: AttRecord)

    @Update
    suspend fun updateRecord(r: AttRecord)

    @Delete
    suspend fun deleteRecord(r: AttRecord)
}

@Database(entities = [Subject::class, AttRecord::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): SubjectDao

    companion object {
        @Volatile private var inst: AppDatabase? = null
        fun get(ctx: Context): AppDatabase = inst ?: synchronized(this) {
            inst ?: Room.databaseBuilder(
                ctx.applicationContext, AppDatabase::class.java, "attendtrack.db"
            ).fallbackToDestructiveMigration().build().also { inst = it }
        }
    }
}
