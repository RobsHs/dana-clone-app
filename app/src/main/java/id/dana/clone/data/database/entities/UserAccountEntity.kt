package id.dana.clone.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing the local user's wallet profile stored strictly in internal storage.
 * No external API or cloud server required.
 */
@Entity(tableName = "user_account")
data class UserAccountEntity(
    @PrimaryKey
    val id: Int = 1,
    val fullName: String,
    val phoneNumber: String,
    val kycTier: String = "DANA Premium",
    val balance: Long = 2500000L,
    val danaPoints: Int = 1250,
    val danaCicilLimit: Long = 5000000L,
    val isBalanceHidden: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis()
)
