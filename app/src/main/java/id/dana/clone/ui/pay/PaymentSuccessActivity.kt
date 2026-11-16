package id.dana.clone.ui.pay

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import id.dana.clone.databinding.ActivityPaymentSuccessBinding
import id.dana.clone.ui.riwayat.TransactionDetailActivity
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PaymentSuccessActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentSuccessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentSuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val merchant = intent.getStringExtra(EXTRA_MERCHANT) ?: "NADDD, Toko HP, Pulsa &\nI"
        val amount = intent.getLongExtra(EXTRA_AMOUNT, 8000L)
        val formattedAmount = intent.getStringExtra(EXTRA_FORMATTED_AMOUNT) ?: "Rp${NumberFormat.getNumberInstance(Locale("id", "ID")).format(amount)}"
        val balance = intent.getStringExtra(EXTRA_BALANCE) ?: "Rp 9.498"

        binding.tvSuccessMerchant.text = merchant
        binding.tvSuccessAmount.text = formattedAmount
        binding.tvSuccessBalance.text = balance

        binding.btnClosePaymentSuccess.setOnClickListener {
            finish()
        }

        binding.btnViewTxDetailFromSuccess.setOnClickListener {
            val now = SimpleDateFormat("dd MMM yyyy • HH:mm", Locale("en", "US")).format(Date())
            val intent = Intent(this, TransactionDetailActivity::class.java).apply {
                putExtra(TransactionDetailActivity.EXTRA_TITLE, merchant.replace("\n", " "))
                putExtra(TransactionDetailActivity.EXTRA_AMOUNT, amount)
                putExtra(TransactionDetailActivity.EXTRA_FORMATTED_AMOUNT, formattedAmount)
                putExtra(TransactionDetailActivity.EXTRA_DATE, now)
                putExtra(TransactionDetailActivity.EXTRA_DANA_ID, "DANA ID 0881•••5924")
                putExtra(TransactionDetailActivity.EXTRA_METHOD, "DANA Balance\n(SmartPay)")
            }
            startActivity(intent)
            finish()
        }
    }

    companion object {
        const val EXTRA_MERCHANT = "extra_merchant"
        const val EXTRA_AMOUNT = "extra_amount"
        const val EXTRA_FORMATTED_AMOUNT = "extra_formatted_amount"
        const val EXTRA_BALANCE = "extra_balance"
    }
}
