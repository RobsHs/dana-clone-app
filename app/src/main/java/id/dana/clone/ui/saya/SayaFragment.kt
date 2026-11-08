package id.dana.clone.ui.saya

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import id.dana.clone.DanaCloneApplication
import id.dana.clone.databinding.FragmentSayaBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.text.NumberFormat
import java.util.Locale

class SayaFragment : Fragment() {

    private var _binding: FragmentSayaBinding? = null
    private val binding get() = _binding!!

    private val app by lazy {
        requireActivity().application as DanaCloneApplication
    }
    private val repository by lazy { app.repository }
    private val preferences by lazy { app.preferences }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSayaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupProfileObserver()
        setupInternalStorageInfo()
        setupSimulationActions()
        setupSecuritySwitches()
    }

    private fun setupProfileObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            repository.userAccount.collectLatest { account ->
                account?.let {
                    binding.tvProfileName.text = it.fullName
                    binding.tvProfilePhone.text = it.phoneNumber
                }
            }
        }
    }

    private fun setupInternalStorageInfo() {
        val dbFile = requireContext().getDatabasePath("dana_internal.db")
        val dbSizeKb = if (dbFile.exists()) dbFile.length() / 1024 else 0
        binding.tvDbPath.text = "${dbFile.absolutePath} (${dbSizeKb} KB)"
    }

    private fun setupSimulationActions() {
        binding.btnQuickTopUp100k.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                repository.topUpBalance(100000L, "Simulasi Saldo Internal")
                Toast.makeText(requireContext(), "Berhasil menambah saldo internal: Rp 100.000", Toast.LENGTH_SHORT).show()
                setupInternalStorageInfo()
            }
        }

        binding.btnQuickTopUp500k.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                repository.topUpBalance(500000L, "Simulasi Saldo Internal")
                Toast.makeText(requireContext(), "Berhasil menambah saldo internal: Rp 500.000", Toast.LENGTH_SHORT).show()
                setupInternalStorageInfo()
            }
        }
    }

    private fun setupSecuritySwitches() {
        binding.switchBiometric.isChecked = preferences.isBiometricEnabled
        binding.switchProtection.isChecked = preferences.isDanaProtectionActive

        binding.switchBiometric.setOnCheckedChangeListener { _, isChecked ->
            preferences.isBiometricEnabled = isChecked
            val status = if (isChecked) "diaktifkan" else "dinonaktifkan"
            Toast.makeText(requireContext(), "Otentikasi biometrik lokal $status", Toast.LENGTH_SHORT).show()
        }

        binding.switchProtection.setOnCheckedChangeListener { _, isChecked ->
            preferences.isDanaProtectionActive = isChecked
            val status = if (isChecked) "diaktifkan" else "dinonaktifkan"
            Toast.makeText(requireContext(), "DANA Protection $status", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
