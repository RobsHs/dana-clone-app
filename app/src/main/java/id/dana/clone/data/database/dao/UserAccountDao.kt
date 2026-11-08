package id.dana.clone.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import id.dana.clone.data.database.entities.UserAccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserAccountDao {
    @Query("SELECT * FROM user_account WHERE id = 1 LIMIT 1")
    fun getUserAccount(): Flow<UserAccountEntity?>

    @Query("SELECT * FROM user_account WHERE id = 1 LIMIT 1")
    suspend fun getUserAccountDirect(): UserAccountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(account: UserAccountEntity)

    @Query("UPDATE user_account SET balance = :newBalance, lastUpdated = :timestamp WHERE id = 1")
    suspend fun updateBalance(newBalance: Long, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE user_account SET isBalanceHidden = :isHidden WHERE id = 1")
    suspend fun toggleBalanceVisibility(isHidden: Boolean)
}
