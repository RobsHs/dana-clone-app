package id.dana.clone.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity for tracking wallet transactions locally within app internal database.
 */
@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey
    val id: String,
    val type: String, // "KIRIM_UANG", "ISI_SALDO", "MINTA_UANG", "BAYAR_QRIS", "TAGIHAN"
    val title: String,
    val amount: Long,
    val fee: Long = 0L,
    val recipientOrSender: String,
    val accountOrPhone: String,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "BERHASIL" // "BERHASIL", "MENUNGGU", "GAGAL"
)
