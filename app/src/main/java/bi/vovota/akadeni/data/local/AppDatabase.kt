package bi.vovota.akadeni.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import bi.vovota.akadeni.data.local.model.Loan

@Database(entities = [Loan::class], version = 6)
abstract class AppDatabase: RoomDatabase() {
  abstract fun loanDao(): LoanDao

  companion object {
    @Volatile private var instance: AppDatabase? = null

    /**
     * Migration v5 → v6: adds the nullable [Loan.dueDate] column.
     * Existing rows default to NULL which means "no reminder scheduled".
     */
    private val MIGRATION_5_6 = object : Migration(5, 6) {
      override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE loan ADD COLUMN dueDate INTEGER DEFAULT NULL")
      }
    }

    fun getDatabase(context: Context): AppDatabase = instance ?: synchronized(this) {
      instance ?: Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "app_db"
      ).addMigrations(MIGRATION_5_6)
        .build().also { instance = it }
    }
  }
}