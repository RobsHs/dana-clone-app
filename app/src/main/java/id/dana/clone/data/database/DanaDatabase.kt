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
import java.util.Calendar

/**
 * Main Room Database residing strictly in app internal storage:
 * Path: /data/user/0/id.dana.clone/databases/dana_internal.db
 * Seeded with authentic Indonesian DANA transaction history.
 */
@Database(
    entities = [
        UserAccountEntity::class,
        TransactionEntity::class,
        BeneficiaryEntity::class
    ],
    version = 2,
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
                    .fallbackToDestructiveMigration()
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

                // Exact user account matching screenshots
                userDao.insertOrUpdate(
                    UserAccountEntity(
                        id = 1,
                        fullName = "dana1497uwpx",
                        phoneNumber = "08••••••••24 >",
                        kycTier = "DANA Premium Failed",
                        balance = 6832L,
                        danaPoints = 2850,
                        danaCicilLimit = 7500000L,
                        isBalanceHidden = false
                    )
                )

                // 15 Nov 2026 19:00
                val cal = Calendar.getInstance().apply {
                    set(2026, Calendar.NOVEMBER, 15, 19, 0, 0)
                }

                trxDao.insertTransaction(
                    TransactionEntity(
                        id = "TRX20261115001",
                        type = "TAGIHAN",
                        title = "Axis",
                        amount = 13500L,
                        fee = 0L,
                        recipientOrSender = "Axis",
                        accountOrPhone = "Pulsa & Data",
                        note = "Beli Paket Data",
                        timestamp = cal.timeInMillis,
                        status = "BERHASIL"
                    )
                )

                // 15 Nov 2026 18:47
                cal.set(2026, Calendar.NOVEMBER, 15, 18, 47, 0)
                trxDao.insertTransaction(
                    TransactionEntity(
                        id = "TRX20261115002",
                        type = "ISI_SALDO",
                        title = "Top Up from AMIDO PAY",
                        amount = 20000L,
                        fee = 0L,
                        recipientOrSender = "AMIDO PAY",
                        accountOrPhone = "DANA Wallet",
                        note = "Top Up Berhasil",
                        timestamp = cal.timeInMillis,
                        status = "BERHASIL"
                    )
                )

                // 07 Nov 2026 22:20
                cal.set(2026, Calendar.NOVEMBER, 7, 22, 20, 0)
                trxDao.insertTransaction(
                    TransactionEntity(
                        id = "TRX20261107001",
                        type = "BAYAR_QRIS",
                        title = "TikTok",
                        amount = 2886L,
                        fee = 0L,
                        recipientOrSender = "TikTok",
                        accountOrPhone = "Google Play",
                        note = "TikTok Coins",
                        timestamp = cal.timeInMillis,
                        status = "BERHASIL"
                    )
                )

                // 07 Nov 2026 12:44
                cal.set(2026, Calendar.NOVEMBER, 7, 12, 44, 0)
                trxDao.insertTransaction(
                    TransactionEntity(
                        id = "TRX20261107002",
                        type = "BAYAR_QRIS",
                        title = "itemku.com - TR0000016...",
                        amount = 4731L,
                        fee = 0L,
                        recipientOrSender = "itemku.com",
                        accountOrPhone = "TR0000016...",
                        note = "Pembelian Voucher",
                        timestamp = cal.timeInMillis,
                        status = "BERHASIL"
                    )
                )

                // 06 Nov 2026 23:07
                cal.set(2026, Calendar.NOVEMBER, 6, 23, 7, 0)
                trxDao.insertTransaction(
                    TransactionEntity(
                        id = "TRX20261106001",
                        type = "BAYAR_QRIS",
                        title = "Indah Gametopup",
                        amount = 12177L,
                        fee = 0L,
                        recipientOrSender = "Indah Gametopup",
                        accountOrPhone = "QRIS Merchant",
                        note = "Game Topup",
                        timestamp = cal.timeInMillis,
                        status = "BERHASIL"
                    )
                )

                // 06 Nov 2026 07:12
                cal.set(2026, Calendar.NOVEMBER, 6, 7, 12, 0)
                trxDao.insertTransaction(
                    TransactionEntity(
                        id = "TRX20261106002",
                        type = "MINTA_UANG",
                        title = "Receive Money",
                        amount = 20000L,
                        fee = 0L,
                        recipientOrSender = "DANA User",
                        accountOrPhone = "DANA Transfer",
                        note = "Terima Saldo",
                        timestamp = cal.timeInMillis,
                        status = "BERHASIL"
                    )
                )

                // Beneficiaries
                benDao.insertBeneficiary(
                    BeneficiaryEntity(
                        accountNumber = "081288997711",
                        accountName = "Siti Rahma",
                        bankName = "DANA",
                        isFavorite = true
                    )
                )
            }
        }
    }
}
