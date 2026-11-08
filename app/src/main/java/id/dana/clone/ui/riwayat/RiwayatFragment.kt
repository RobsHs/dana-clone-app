package id.dana.clone.ui.riwayat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import id.dana.clone.DanaCloneApplication
import id.dana.clone.R
import id.dana.clone.data.database.entities.TransactionEntity
import id.dana.clone.databinding.FragmentRiwayatBinding
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
    private var currentFilter: String = "SEMUA" // "SEMUA", "MASUK", "KELUAR"

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
        setupTabs()
        observeTransactions()
    }

    private fun setupRecyclerView() {
        adapter = TransactionAdapter()
        binding.rvTransactions.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTransactions.adapter = adapter
    }

    private fun setupTabs() {
        binding.tabSemua.setOnClickListener {
            currentFilter = "SEMUA"
            updateTabStyles()
            applyFilter()
        }

        binding.tabMasuk.setOnClickListener {
            currentFilter = "MASUK"
            updateTabStyles()
            applyFilter()
        }

        binding.tabKeluar.setOnClickListener {
            currentFilter = "KELUAR"
            updateTabStyles()
            applyFilter()
        }
    }

    private fun updateTabStyles() {
        val activeColor = requireContext().getColor(R.color.dana_blue)
        val inactiveColor = requireContext().getColor(R.color.text_secondary)
        val white = requireContext().getColor(R.color.white)

        when (currentFilter) {
            "SEMUA" -> {
                binding.tabSemua.setBackgroundColor(activeColor)
                binding.tabSemua.setTextColor(white)
                binding.tabMasuk.setBackgroundColor(white)
                binding.tabMasuk.setTextColor(inactiveColor)
                binding.tabKeluar.setBackgroundColor(white)
                binding.tabKeluar.setTextColor(inactiveColor)
            }
            "MASUK" -> {
                binding.tabSemua.setBackgroundColor(white)
                binding.tabSemua.setTextColor(inactiveColor)
                binding.tabMasuk.setBackgroundColor(activeColor)
                binding.tabMasuk.setTextColor(white)
                binding.tabKeluar.setBackgroundColor(white)
                binding.tabKeluar.setTextColor(inactiveColor)
            }
            "KELUAR" -> {
                binding.tabSemua.setBackgroundColor(white)
                binding.tabSemua.setTextColor(inactiveColor)
                binding.tabMasuk.setBackgroundColor(white)
                binding.tabMasuk.setTextColor(inactiveColor)
                binding.tabKeluar.setBackgroundColor(activeColor)
                binding.tabKeluar.setTextColor(white)
            }
        }
    }

    private fun observeTransactions() {
        // Show Loading state first
        showLoadingState()

        viewLifecycleOwner.lifecycleScope.launch {
            repository.allTransactions.collectLatest { list ->
                allTransactionsList = list
                applyFilter()
            }
        }
    }

    private fun applyFilter() {
        val filtered = when (currentFilter) {
            "MASUK" -> allTransactionsList.filter { it.type == "ISI_SALDO" }
            "KELUAR" -> allTransactionsList.filter { it.type != "ISI_SALDO" }
            else -> allTransactionsList
        }

        if (filtered.isEmpty()) {
            showEmptyState()
        } else {
            showContentState(filtered)
        }
    }

    private fun showLoadingState() {
        binding.stateLoading.visibility = View.VISIBLE
        binding.stateEmpty.visibility = View.GONE
        binding.rvTransactions.visibility = View.GONE
    }

    private fun showEmptyState() {
        binding.stateLoading.visibility = View.GONE
        binding.stateEmpty.visibility = View.VISIBLE
        binding.rvTransactions.visibility = View.GONE
    }

    private fun showContentState(list: List<TransactionEntity>) {
        binding.stateLoading.visibility = View.GONE
        binding.stateEmpty.visibility = View.GONE
        binding.rvTransactions.visibility = View.VISIBLE
        adapter.submitList(list)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
