package id.dana.clone.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import id.dana.clone.DanaCloneApplication
import id.dana.clone.R
import id.dana.clone.databinding.FragmentHomeBinding
import id.dana.clone.ui.MainActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val repository by lazy {
        (requireActivity().application as DanaCloneApplication).repository
    }

    private var isBalanceHidden = false
    private var currentBalance = 6832L

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
        setupBannersAndServices()
    }

    private fun setupUserAccountObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            repository.userAccount.collectLatest { account ->
                account?.let {
                    currentBalance = it.balance
                    isBalanceHidden = it.isBalanceHidden
                    updateBalanceDisplay()
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

        binding.btnActionTopUp.setOnClickListener {
            showTopUpDialog()
        }

        binding.btnActionSend.setOnClickListener {
            mainActivity.navigateToKirim()
        }

        binding.btnActionRequest.setOnClickListener {
            mainActivity.navigateToWallet()
        }

        binding.btnActionInbox.setOnClickListener {
            Toast.makeText(requireContext(), "Inbox: Kamu tidak memiliki pesan baru", Toast.LENGTH_SHORT).show()
        }

        binding.btnCicilHeader.setOnClickListener {
            Toast.makeText(requireContext(), "DANA CICIL: Limit tersedia Rp 7.500.000", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupBannersAndServices() {
        binding.ivBannerMlbb.setOnClickListener {
            Toast.makeText(requireContext(), "Event MLBB: Mystery Box 5 Juta Diamond!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showTopUpDialog() {
        val options = arrayOf("BCA Virtual Account", "Mandiri Virtual Account", "BRI Virtual Account", "Indomaret / Alfamart")
        AlertDialog.Builder(requireContext())
            .setTitle("Pilih Metode Isi Saldo")
            .setItems(options) { _, which ->
                val method = options[which]
                viewLifecycleOwner.lifecycleScope.launch {
                    val res = repository.topUpBalance(20000L, method)
                    Toast.makeText(requireContext(), "Top Up Rp 20.000 via $method Berhasil!", Toast.LENGTH_LONG).show()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun formatNumber(number: Long): String {
        return NumberFormat.getNumberInstance(Locale("id", "ID")).format(number)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
