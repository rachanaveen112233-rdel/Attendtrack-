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

@Dao
interface SubjectDao {
    @Query("SELECT * FROM subjects ORDER BY id")
    fun all(): Flow<List<Subject>>

    @Query("SELECT COUNT(*) FROM subjects")
    suspend fun count(): Int

    @Insert
    suspend fun insertAll(list: List<Subject>)

    @Update
    suspend fun update(s: Subject)
}

@Database(entities = [Subject::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): SubjectDao

    companion object {
        @Volatile private var inst: AppDatabase? = null
        fun get(ctx: Context): AppDatabase = inst ?: synchronized(this) {
            inst ?: Room.databaseBuilder(
                ctx.applicationContext, AppDatabase::class.java, "attendtrack.db"
            ).build().also { inst = it }
        }
    }
}
