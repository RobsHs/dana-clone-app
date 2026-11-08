package id.dana.clone.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import id.dana.clone.data.database.dao.BeneficiaryDao
import id.dana.clone.data.database.dao.TransactionDao
import id.dana.clone.data.database.dao.UserAccountDao
import id.dana.clone.data.database.entities.BeneficiaryEntity
import id.dana.clone.data.database.entities.TransactionEntity
import id.dana.clone.data.database.entities.UserAccountEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Main Room Database residing strictly in app internal storage:
 * Path: /data/user/0/id.dana.clone/databases/dana_internal.db
 * Zero dependencies on external storage or remote network APIs.
 */
@Database(
    entities = [
        UserAccountEntity::class,
        TransactionEntity::class,
        BeneficiaryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class DanaDatabase : RoomDatabase() {
    abstract fun userAccountDao(): UserAccountDao
    abstract fun transactionDao(): TransactionDao
    abstract fun beneficiaryDao(): BeneficiaryDao

    companion object {
        @Volatile
        private var INSTANCE: DanaDatabase? = null

        fun getInstance(context: Context, scope: CoroutineScope): DanaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DanaDatabase::class.java,
                    "dana_internal.db"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        seedDatabase(database)
                    }
                }
            }

            private suspend fun seedDatabase(database: DanaDatabase) {
                val userDao = database.userAccountDao()
                val trxDao = database.transactionDao()
                val benDao = database.beneficiaryDao()

                // Default Internal User Account
                userDao.insertOrUpdate(
                    UserAccountEntity(
                        id = 1,
                        fullName = "Budi Pratama",
                        phoneNumber = "0812-8899-7711",
                        kycTier = "DANA Premium",
                        balance = 3450000L,
                        danaPoints = 2850,
                        danaCicilLimit = 7500000L,
                        isBalanceHidden = false
                    )
                )

                // Default Internal Transactions (Authentic Indonesian DANA copy)
                val now = System.currentTimeMillis()
                trxDao.insertTransaction(
                    TransactionEntity(
                        id = "TRX20261108001",
                        type = "KIRIM_UANG",
                        title = "Kirim Uang ke BCA",
                        amount = 250000L,
                        fee = 0L,
                        recipientOrSender = "Siti Rahma",
                        accountOrPhone = "8219082138",
                        note = "Bayar makan siang",
                        timestamp = now - (1000 * 60 * 45), // 45 mins ago
                        status = "BERHASIL"
                    )
                )
                trxDao.insertTransaction(
                    TransactionEntity(
                        id = "TRX20261108002",
                        type = "BAYAR_QRIS",
                        title = "Pembayaran QRIS",
                        amount = 45000L,
                        fee = 0L,
                        recipientOrSender = "Kopi Kenangan Grand Indonesia",
                        accountOrPhone = "NMID: ID102003891238",
                        note = "2x Kopi Kenangan Mantan",
                        timestamp = now - (1000 * 60 * 60 * 3), // 3 hours ago
                        status = "BERHASIL"
                    )
                )
                trxDao.insertTransaction(
                    TransactionEntity(
                        id = "TRX20261107001",
                        type = "ISI_SALDO",
                        title = "Isi Saldo via BCA OneKlik",
                        amount = 1000000L,
                        fee = 0L,
                        recipientOrSender = "Bank BCA",
                        accountOrPhone = "•••• 8912",
                        note = "Top Up DANA Saldo",
                        timestamp = now - (1000 * 60 * 60 * 24), // 1 day ago
                        status = "BERHASIL"
                    )
                )

                // Default Saved Beneficiaries
                benDao.insertAll(
                    listOf(
                        BeneficiaryEntity(
                            name = "Siti Rahma",
                            accountNumber = "0813-9821-4455",
                            bankOrProvider = "DANA",
                            avatarColor = "#118EEA",
                            isFavorite = true
                        ),
                        BeneficiaryEntity(
                            name = "Ahmad Fauzi",
                            accountNumber = "5270192831",
                            bankOrProvider = "BCA",
                            avatarColor = "#005E9E",
                            isFavorite = true
                        ),
                        BeneficiaryEntity(
                            name = "Dewi Lestari",
                            accountNumber = "0856-1122-3344",
                            bankOrProvider = "DANA",
                            avatarColor = "#16B364",
                            isFavorite = true
                        ),
                        BeneficiaryEntity(
                            name = "Rian Hidayat",
                            accountNumber = "012398210398",
                            bankOrProvider = "Bank Mandiri",
                            avatarColor = "#003A70",
                            isFavorite = false
                        )
                    )
                )
            }
        }
    }
}
