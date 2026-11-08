package id.dana.clone.ui.riwayat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
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
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTrxTitle)
        private val tvDate: TextView = itemView.findViewById(R.id.tvTrxDate)
        private val tvAmount: TextView = itemView.findViewById(R.id.tvTrxAmount)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvTrxStatus)
        private val ivIcon: ImageView = itemView.findViewById(R.id.ivTrxIcon)

        fun bind(trx: TransactionEntity) {
            val context = itemView.context
            tvTitle.text = trx.title
            tvDate.text = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID")).format(Date(trx.timestamp)) + " WIB"

            val formatted = NumberFormat.getNumberInstance(Locale("id", "ID")).format(trx.amount)
            if (trx.type == "ISI_SALDO") {
                tvAmount.text = "+Rp $formatted"
                tvAmount.setTextColor(context.getColor(R.color.dana_green))
                ivIcon.setImageResource(R.drawable.ic_topup)
            } else {
                tvAmount.text = "-Rp $formatted"
                tvAmount.setTextColor(context.getColor(R.color.text_primary))
                ivIcon.setImageResource(R.drawable.ic_kirim)
            }

            tvStatus.text = trx.status

            itemView.setOnClickListener {
                AlertDialog.Builder(context)
                    .setTitle("Bukti Transaksi Internal")
                    .setMessage(
                        "ID Transaksi: ${trx.id}\n" +
                        "Tipe: ${trx.type}\n" +
                        "Nominal: Rp $formatted\n" +
                        "Pihak Terkait: ${trx.recipientOrSender}\n" +
                        "Akun/HP: ${trx.accountOrPhone}\n" +
                        "Catatan: ${trx.note}\n" +
                        "Waktu: ${tvDate.text}\n" +
                        "Status: ${trx.status}\n\n" +
                        "Tersimpan dalam SQLite Room Internal (/data/user/0/id.dana.clone/)"
                    )
                    .setPositiveButton("Tutup", null)
                    .show()
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
