package id.dana.clone.ui.riwayat

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import id.dana.clone.DanaCloneApplication
import id.dana.clone.R
import id.dana.clone.data.database.entities.TransactionEntity
import id.dana.clone.databinding.FragmentRiwayatBinding
import id.dana.clone.ui.MainActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RiwayatFragment : Fragment() {

    private var _binding: FragmentRiwayatBinding? = null
    private val binding get() = _binding!!

    private val repository by lazy {
        (requireActivity().application as DanaCloneApplication).repository
    }

    private lateinit var adapter: TransactionAdapter
    private var allTransactionsList: List<TransactionEntity> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRiwayatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupOctoberItem()
        setupHeaderActions()
        observeTransactions()
    }

    private fun setupRecyclerView() {
        adapter = TransactionAdapter()
        binding.rvTransactions.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTransactions.adapter = adapter
    }

    private fun setupOctoberItem() {
        binding.itemOctoberTiktok.tvTxTitle.text = "TikTok"
        binding.itemOctoberTiktok.tvTxTimestamp.text = "18 Oct 2026 • 08:47"
        binding.itemOctoberTiktok.tvTxAmount.text = "-Rp894"
        binding.itemOctoberTiktok.ivTxLogo.setImageResource(R.drawable.ic_tx_tiktok)
        binding.itemOctoberTiktok.ivTxBadge.setImageResource(R.drawable.ic_dana_logo)

        binding.itemOctoberTiktok.root.setOnClickListener {
            val intent = android.content.Intent(requireContext(), TransactionDetailActivity::class.java).apply {
                putExtra(TransactionDetailActivity.EXTRA_TITLE, "TikTok")
                putExtra(TransactionDetailActivity.EXTRA_AMOUNT, 894L)
                putExtra(TransactionDetailActivity.EXTRA_FORMATTED_AMOUNT, "Rp894")
                putExtra(TransactionDetailActivity.EXTRA_DATE, "18 Oct 2026 • 08:47")
                putExtra(TransactionDetailActivity.EXTRA_DANA_ID, "DANA ID 0881•••5924")
                putExtra(TransactionDetailActivity.EXTRA_METHOD, "DANA Balance\n(SmartPay)")
            }
            startActivity(intent)
        }
    }

    private fun setupHeaderActions() {
        binding.btnBackActivity.setOnClickListener {
            (requireActivity() as? MainActivity)?.navigateToHome()
        }

        binding.btnExportActivity.setOnClickListener {
            Toast.makeText(requireContext(), "Statement downloaded successfully", Toast.LENGTH_SHORT).show()
        }

        binding.etSearchActivity.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s?.toString()?.trim() ?: ""
                val filtered = if (query.isEmpty()) {
                    allTransactionsList
                } else {
                    allTransactionsList.filter {
                        it.title.contains(query, ignoreCase = true) ||
                        it.type.contains(query, ignoreCase = true) ||
                        it.amount.toString().contains(query)
                    }
                }
                adapter.submitList(filtered)
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun observeTransactions() {
        viewLifecycleOwner.lifecycleScope.launch {
            repository.allTransactions.collectLatest { transactions ->
                allTransactionsList = transactions
                adapter.submitList(transactions)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
