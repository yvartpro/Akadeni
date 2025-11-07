package bi.vovota.akadeni.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import bi.vovota.akadeni.data.local.model.Loan

@Database(entities = [Loan::class], version = 3)
abstract class AppDatabase: RoomDatabase() {
  abstract fun loanDao(): LoanDao

  companion object {
    @Volatile private var instance: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase = instance ?: synchronized(this) {
      instance ?: Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "app_db"
      ).fallbackToDestructiveMigration()
        .build().also { instance = it }
    }
  }
}