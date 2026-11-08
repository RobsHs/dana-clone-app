package id.dana.clone.data.repository

import id.dana.clone.data.database.DanaDatabase
import id.dana.clone.data.database.entities.BeneficiaryEntity
import id.dana.clone.data.database.entities.TransactionEntity
import id.dana.clone.data.database.entities.UserAccountEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

sealed class TransactionResult {
    data class Success(val transaction: TransactionEntity, val newBalance: Long) : TransactionResult()
    data class Error(val message: String) : TransactionResult()
}

/**
 * Single source of truth for the DANA Clone application.
 * Executes all financial logic completely offline using internal Room Database.
 */
class DanaInternalRepository(private val database: DanaDatabase) {
    private val userDao = database.userAccountDao()
    private val trxDao = database.transactionDao()
    private val benDao = database.beneficiaryDao()

    val userAccount: Flow<UserAccountEntity?> = userDao.getUserAccount()
    val allTransactions: Flow<List<TransactionEntity>> = trxDao.getAllTransactions()
    val recentTransactions: Flow<List<TransactionEntity>> = trxDao.getRecentTransactions(5)
    val beneficiaries: Flow<List<BeneficiaryEntity>> = benDao.getAllBeneficiaries()

    suspend fun toggleBalanceVisibility(currentVisibility: Boolean) {
        userDao.toggleBalanceVisibility(!currentVisibility)
    }

    suspend fun sendMoney(
        recipientName: String,
        targetAccount: String,
        amount: Long,
        note: String
    ): TransactionResult {
        if (amount < 10000L) {
            return TransactionResult.Error("Jumlah transfer minimum adalah Rp 10.000")
        }

        val user = userDao.getUserAccountDirect()
            ?: return TransactionResult.Error("Data akun pengguna tidak ditemukan")

        if (user.balance < amount) {
            return TransactionResult.Error("Saldo DANA tidak mencukupi untuk transfer ini")
        }

        val newBalance = user.balance - amount
        userDao.updateBalance(newBalance)

        val trxId = "TRX" + System.currentTimeMillis().toString().takeLast(10)
        val transaction = TransactionEntity(
            id = trxId,
            type = "KIRIM_UANG",
            title = "Kirim Uang ke $recipientName",
            amount = amount,
            fee = 0L,
            recipientOrSender = recipientName,
            accountOrPhone = targetAccount,
            note = note.ifBlank { "Transfer DANA" },
            timestamp = System.currentTimeMillis(),
            status = "BERHASIL"
        )
        trxDao.insertTransaction(transaction)

        return TransactionResult.Success(transaction, newBalance)
    }

    suspend fun topUpBalance(amount: Long, method: String): TransactionResult {
        if (amount < 10000L) {
            return TransactionResult.Error("Jumlah isi saldo minimum adalah Rp 10.000")
        }

        val user = userDao.getUserAccountDirect()
            ?: return TransactionResult.Error("Data akun pengguna tidak ditemukan")

        val newBalance = user.balance + amount
        userDao.updateBalance(newBalance)

        val trxId = "TOP" + System.currentTimeMillis().toString().takeLast(10)
        val transaction = TransactionEntity(
            id = trxId,
            type = "ISI_SALDO",
            title = "Isi Saldo via $method",
            amount = amount,
            fee = 0L,
            recipientOrSender = method,
            accountOrPhone = "DANA Wallet",
            note = "Top Up Berhasil",
            timestamp = System.currentTimeMillis(),
            status = "BERHASIL"
        )
        trxDao.insertTransaction(transaction)

        return TransactionResult.Success(transaction, newBalance)
    }

    suspend fun payQris(merchantName: String, amount: Long, nmid: String): TransactionResult {
        if (amount <= 0L) {
            return TransactionResult.Error("Nominal pembayaran harus lebih besar dari Rp 0")
        }

        val user = userDao.getUserAccountDirect()
            ?: return TransactionResult.Error("Data akun pengguna tidak ditemukan")

        if (user.balance < amount) {
            return TransactionResult.Error("Saldo DANA tidak mencukupi untuk pembayaran QRIS")
        }

        val newBalance = user.balance - amount
        userDao.updateBalance(newBalance)

        val trxId = "QRS" + System.currentTimeMillis().toString().takeLast(10)
        val transaction = TransactionEntity(
            id = trxId,
            type = "BAYAR_QRIS",
            title = "Pembayaran QRIS",
            amount = amount,
            fee = 0L,
            recipientOrSender = merchantName,
            accountOrPhone = nmid,
            note = "Pembayaran Merchant",
            timestamp = System.currentTimeMillis(),
            status = "BERHASIL"
        )
        trxDao.insertTransaction(transaction)

        return TransactionResult.Success(transaction, newBalance)
    }
}
