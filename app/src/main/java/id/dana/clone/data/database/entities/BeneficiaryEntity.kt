package id.dana.clone.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Saved beneficiaries / favorite recipient contacts stored in app internal database.
 */
@Entity(tableName = "beneficiaries")
data class BeneficiaryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val accountNumber: String,
    val bankOrProvider: String = "DANA",
    val avatarColor: String = "#118EEA",
    val isFavorite: Boolean = true
)
