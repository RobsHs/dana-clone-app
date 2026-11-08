package id.dana.clone.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import id.dana.clone.DanaCloneApplication
import id.dana.clone.R
import id.dana.clone.data.database.entities.TransactionEntity
import id.dana.clone.databinding.FragmentHomeBinding
import id.dana.clone.ui.MainActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val repository by lazy {
        (requireActivity().application as DanaCloneApplication).repository
    }

    private var isBalanceHidden = false
    private var currentBalance = 0L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUserAccountObserver()
        setupTopActions()
        setupServicesGrid()
        setupRecentTransactionsObserver()
    }

    private fun setupUserAccountObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            repository.userAccount.collectLatest { account ->
                account?.let {
                    currentBalance = it.balance
                    isBalanceHidden = it.isBalanceHidden
                    updateBalanceDisplay()

                    binding.tvPoints.text = "${formatNumber(it.danaPoints.toLong())} Poin"
                }
            }
        }

        binding.btnToggleBalance.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                repository.toggleBalanceVisibility(isBalanceHidden)
            }
        }
    }

    private fun updateBalanceDisplay() {
        if (isBalanceHidden) {
            binding.tvBalance.text = getString(R.string.rp_hidden)
            binding.btnToggleBalance.setImageResource(R.drawable.ic_eye_off)
        } else {
            binding.tvBalance.text = "Rp ${formatNumber(currentBalance)}"
            binding.btnToggleBalance.setImageResource(R.drawable.ic_eye)
        }
    }

    private fun setupTopActions() {
        val mainActivity = requireActivity() as MainActivity

        binding.btnActionPindai.setOnClickListener {
            mainActivity.navigateToPay()
        }

        binding.btnActionTopUp.setOnClickListener {
            showTopUpDialog()
        }

        binding.btnActionKirim.setOnClickListener {
            mainActivity.navigateToKirim()
        }

        binding.btnActionMinta.setOnClickListener {
            mainActivity.navigateToMinta()
        }

        binding.btnViewAllRecent.setOnClickListener {
            mainActivity.navigateToRiwayat()
        }

        binding.btnDanaPoints.setOnClickListener {
            Toast.makeText(requireContext(), "DANA Points: Ditukarkan untuk voucher diskon", Toast.LENGTH_SHORT).show()
        }

        binding.cardProtection.setOnClickListener {
            Toast.makeText(requireContext(), "DANA Protection aktif: Perlindungan saldo internal 100%", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showTopUpDialog() {
        val amounts = arrayOf("Rp 50.000", "Rp 100.000", "Rp 250.000", "Rp 500.000", "Rp 1.000.000")
        val values = longArrayOf(50000L, 100000L, 250000L, 500000L, 1000000L)

        AlertDialog.Builder(requireContext())
            .setTitle("Isi Saldo Internal")
            .setItems(amounts) { _, which ->
                val selectedAmount = values[which]
                viewLifecycleOwner.lifecycleScope.launch {
                    repository.topUpBalance(selectedAmount, "BCA OneKlik (Internal)")
                    Toast.makeText(
                        requireContext(),
                        "Berhasil menambah saldo internal: Rp ${formatNumber(selectedAmount)}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun setupServicesGrid() {
        val services = listOf(
            Pair("Pulsa & Data", R.drawable.ic_kirim),
            Pair("Listrik PLN", R.drawable.ic_topup),
            Pair("Air PDAM", R.drawable.ic_security),
            Pair("Games", R.drawable.ic_dana_logo),
            Pair("BPJS", R.drawable.ic_security),
            Pair("eMAS", R.drawable.ic_topup),
            Pair("DANA Deals", R.drawable.ic_kirim),
            Pair("Lihat Semua", R.drawable.ic_pindai)
        )

        binding.gridServices.removeAllViews()
        for (service in services) {
            val itemView = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_service_icon, binding.gridServices, false)

            val tvTitle = itemView.findViewById<TextView>(R.id.tvServiceTitle)
            val ivIcon = itemView.findViewById<ImageView>(R.id.ivServiceIcon)

            tvTitle.text = service.first
            ivIcon.setImageResource(service.second)

            itemView.setOnClickListener {
                Toast.makeText(requireContext(), "Layanan ${service.first} siap digunakan secara offline", Toast.LENGTH_SHORT).show()
            }

            binding.gridServices.addView(itemView)
        }
    }

    private fun setupRecentTransactionsObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            repository.recentTransactions.collectLatest { transactions ->
                binding.containerRecentTransactions.removeAllViews()

                if (transactions.isEmpty()) {
                    val emptyTv = TextView(requireContext()).apply {
                        text = "Belum ada transaksi terbaru di database internal"
                        textSize = 13f
                        setTextColor(requireContext().getColor(R.color.text_secondary))
                        setPadding(16, 24, 16, 24)
                    }
                    binding.containerRecentTransactions.addView(emptyTv)
                } else {
                    for (trx in transactions) {
                        val trxView = createTransactionItemView(trx)
                        binding.containerRecentTransactions.addView(trxView)
                    }
                }
            }
        }
    }

    private fun createTransactionItemView(trx: TransactionEntity): View {
        val itemView = LayoutInflater.from(requireContext())
            .inflate(R.layout.item_transaction, binding.containerRecentTransactions, false)

        val tvTitle = itemView.findViewById<TextView>(R.id.tvTrxTitle)
        val tvDate = itemView.findViewById<TextView>(R.id.tvTrxDate)
        val tvAmount = itemView.findViewById<TextView>(R.id.tvTrxAmount)
        val tvStatus = itemView.findViewById<TextView>(R.id.tvTrxStatus)
        val ivIcon = itemView.findViewById<ImageView>(R.id.ivTrxIcon)

        tvTitle.text = trx.title
        tvDate.text = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID")).format(Date(trx.timestamp)) + " WIB"

        if (trx.type == "ISI_SALDO") {
            tvAmount.text = "+Rp ${formatNumber(trx.amount)}"
            tvAmount.setTextColor(requireContext().getColor(R.color.dana_green))
            ivIcon.setImageResource(R.drawable.ic_topup)
        } else {
            tvAmount.text = "-Rp ${formatNumber(trx.amount)}"
            tvAmount.setTextColor(requireContext().getColor(R.color.text_primary))
            ivIcon.setImageResource(R.drawable.ic_kirim)
        }

        tvStatus.text = trx.status

        itemView.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Detail Transaksi Internal")
                .setMessage("ID Transaksi: ${trx.id}\nTipe: ${trx.type}\nNominal: Rp ${formatNumber(trx.amount)}\nPihak: ${trx.recipientOrSender}\nCatatan: ${trx.note}\nStatus: ${trx.status} (Room DB)")
                .setPositiveButton("Tutup", null)
                .show()
        }

        return itemView
    }

    private fun formatNumber(number: Long): String {
        return NumberFormat.getNumberInstance(Locale("id", "ID")).format(number)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
