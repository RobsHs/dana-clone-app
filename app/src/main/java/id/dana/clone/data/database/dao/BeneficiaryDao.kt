package id.dana.clone.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import id.dana.clone.data.database.entities.BeneficiaryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BeneficiaryDao {
    @Query("SELECT * FROM beneficiaries ORDER BY isFavorite DESC, name ASC")
    fun getAllBeneficiaries(): Flow<List<BeneficiaryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBeneficiary(beneficiary: BeneficiaryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(beneficiaries: List<BeneficiaryEntity>)

    @Query("SELECT COUNT(*) FROM beneficiaries")
    suspend fun getBeneficiaryCount(): Int
}
