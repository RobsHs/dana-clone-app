package id.dana.clone.ui.riwayat

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import id.dana.clone.R
import id.dana.clone.data.database.entities.TransactionEntity
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransactionAdapter : ListAdapter<TransactionEntity, TransactionAdapter.TransactionViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTxTitle)
        private val tvTimestamp: TextView = itemView.findViewById(R.id.tvTxTimestamp)
        private val tvAmount: TextView = itemView.findViewById(R.id.tvTxAmount)
        private val ivLogo: ImageView = itemView.findViewById(R.id.ivTxLogo)
        private val ivBadge: ImageView = itemView.findViewById(R.id.ivTxBadge)

        fun bind(trx: TransactionEntity) {
            val context = itemView.context
            tvTitle.text = trx.title

            val sdf = SimpleDateFormat("dd MMM yyyy • HH:mm", Locale("en", "US"))
            tvTimestamp.text = sdf.format(Date(trx.timestamp))

            val formatted = NumberFormat.getNumberInstance(Locale("id", "ID")).format(trx.amount)
            val displayAmount = if (trx.type == "ISI_SALDO" || trx.type == "MINTA_UANG") {
                "Rp$formatted"
            } else {
                "-Rp$formatted"
            }
            tvAmount.text = displayAmount
            tvAmount.setTextColor(context.getColor(R.color.text_primary))

            // Map exact logo to transaction title
            when {
                trx.title.contains("Axis", ignoreCase = true) -> {
                    ivLogo.setImageResource(R.drawable.ic_tx_axis)
                    ivBadge.setImageResource(R.drawable.ic_dana_logo)
                }
                trx.title.contains("AMIDO", ignoreCase = true) -> {
                    ivLogo.setImageResource(R.drawable.ic_tx_amido)
                    ivBadge.setImageResource(R.drawable.ic_nav_wallet)
                }
                trx.title.contains("TikTok", ignoreCase = true) -> {
                    ivLogo.setImageResource(R.drawable.ic_tx_tiktok)
                    ivBadge.setImageResource(R.drawable.ic_dana_logo)
                }
                trx.title.contains("itemku", ignoreCase = true) -> {
                    ivLogo.setImageResource(R.drawable.ic_tx_itemku)
                    ivBadge.setImageResource(R.drawable.ic_dana_logo)
                }
                trx.title.contains("Indah", ignoreCase = true) -> {
                    ivLogo.setImageResource(R.drawable.ic_tx_indah)
                    ivBadge.setImageResource(R.drawable.ic_dana_logo)
                }
                trx.title.contains("Receive", ignoreCase = true) -> {
                    ivLogo.setImageResource(R.drawable.ic_tx_receive)
                    ivBadge.setImageResource(R.drawable.ic_dana_logo)
                }
                else -> {
                    ivLogo.setImageResource(R.drawable.ic_dana_logo)
                    ivBadge.setImageResource(R.drawable.ic_dana_logo)
                }
            }

            itemView.setOnClickListener {
                val intent = Intent(context, TransactionDetailActivity::class.java).apply {
                    putExtra(TransactionDetailActivity.EXTRA_TITLE, trx.title)
                    putExtra(TransactionDetailActivity.EXTRA_AMOUNT, trx.amount)
                    putExtra(TransactionDetailActivity.EXTRA_FORMATTED_AMOUNT, "Rp$formatted")
                    putExtra(TransactionDetailActivity.EXTRA_DATE, tvTimestamp.text.toString())
                    putExtra(TransactionDetailActivity.EXTRA_DANA_ID, "DANA ID 0881•••5924")
                    putExtra(
                        TransactionDetailActivity.EXTRA_METHOD,
                        if (trx.type == "ISI_SALDO") "Bank Transfer\n(Virtual Account)" else "DANA Balance\n(SmartPay)"
                    )
                }
                context.startActivity(intent)
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<TransactionEntity>() {
        override fun areItemsTheSame(oldItem: TransactionEntity, newItem: TransactionEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TransactionEntity, newItem: TransactionEntity): Boolean {
            return oldItem == newItem
        }
    }
}
