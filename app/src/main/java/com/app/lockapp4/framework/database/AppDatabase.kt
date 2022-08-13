import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [LockTime::class], version = 1, exportSchema = false)
abstract class LockTimeDatabase : RoomDatabase() {
    abstract val lockTimeDatabaseDao: LockTimeDao
}