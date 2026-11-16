package id.dana.clone.ui.riwayat

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import id.dana.clone.R
import id.dana.clone.databinding.ActivityTransactionDetailBinding
import java.text.NumberFormat
import java.util.Locale

class TransactionDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTransactionDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bindDataFromIntent()
        setupListeners()
    }

    private fun bindDataFromIntent() {
        val title = intent.getStringExtra(EXTRA_TITLE) ?: "Payment to Indah Gametopup"
        val amount = intent.getLongExtra(EXTRA_AMOUNT, 12177L)
        val formattedAmount = intent.getStringExtra(EXTRA_FORMATTED_AMOUNT) ?: "Rp12.177"
        val date = intent.getStringExtra(EXTRA_DATE) ?: "06 Nov 2026 • 23:07"
        val danaId = intent.getStringExtra(EXTRA_DANA_ID) ?: "DANA ID 0881•••5924"
        val method = intent.getStringExtra(EXTRA_METHOD) ?: "DANA Balance\n(SmartPay)"

        binding.tvTxDetailTitle.text = if (title.startsWith("Payment to", ignoreCase = true)) title else "Payment to $title"
        binding.tvTxDetailDate.text = date
        binding.tvTxDetailDanaId.text = danaId
        binding.tvTxDetailTotal.text = "$formattedAmount  ∨"
        binding.tvTxDetailMethod.text = method

        // Check logo icon based on transaction title
        when {
            title.contains("Indah", ignoreCase = true) -> binding.ivTxDetailLogo.setImageResource(R.drawable.ic_qris_large)
            title.contains("Axis", ignoreCase = true) -> binding.ivTxDetailLogo.setImageResource(R.drawable.ic_tx_axis)
            title.contains("AMIDO", ignoreCase = true) -> binding.ivTxDetailLogo.setImageResource(R.drawable.ic_tx_amido)
            title.contains("TikTok", ignoreCase = true) -> binding.ivTxDetailLogo.setImageResource(R.drawable.ic_tx_tiktok)
            title.contains("itemku", ignoreCase = true) -> binding.ivTxDetailLogo.setImageResource(R.drawable.ic_tx_itemku)
            else -> binding.ivTxDetailLogo.setImageResource(R.drawable.ic_qris_large)
        }
    }

    private fun setupListeners() {
        binding.btnBackTxDetail.setOnClickListener {
            finish()
        }

        binding.btnShareTxDetail.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Bukti Transaksi DANA:\n" +
                    "${binding.tvTxDetailTitle.text}\n" +
                    "Total: ${binding.tvTxDetailTotal.text}\n" +
                    "Waktu: ${binding.tvTxDetailDate.text}\n" +
                    "Status: Transaction success!\n" +
                    "Secured by DANA Protection"
                )
            }
            startActivity(Intent.createChooser(shareIntent, "Bagikan Bukti Transaksi"))
        }

        binding.btnCreateSplitBill.setOnClickListener {
            Toast.makeText(this, "Fitur Split Bill: Bagikan tagihan ini dengan teman", Toast.LENGTH_SHORT).show()
        }

        binding.btnToggleTxDetail.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Rincian Transaksi")
                .setMessage(
                    "Penerima: ${binding.tvTxDetailTitle.text}\n" +
                    "Metode: ${binding.tvTxDetailMethod.text.toString().replace("\n", " ")}\n" +
                    "Nominal: ${binding.tvTxDetailTotal.text.toString().replace("∨", "").trim()}\n" +
                    "Biaya Layanan: Rp0\n" +
                    "Total: ${binding.tvTxDetailTotal.text.toString().replace("∨", "").trim()}\n\n" +
                    "Referensi: TRX-ID-" + System.currentTimeMillis().toString().takeLast(8)
                )
                .setPositiveButton("Tutup", null)
                .show()
        }

        binding.btnNeedHelpTxDetail.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Bantuan Transaksi DIANA")
                .setMessage("Apakah kamu mengalami kendala dengan transaksi ini? DIANA Customer Care siap membantu 24/7.")
                .setPositiveButton("Hubungi DIANA", null)
                .setNegativeButton("Tutup", null)
                .show()
        }
    }

    companion object {
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_AMOUNT = "extra_amount"
        const val EXTRA_FORMATTED_AMOUNT = "extra_formatted_amount"
        const val EXTRA_DATE = "extra_date"
        const val EXTRA_DANA_ID = "extra_dana_id"
        const val EXTRA_METHOD = "extra_method"
    }
}

