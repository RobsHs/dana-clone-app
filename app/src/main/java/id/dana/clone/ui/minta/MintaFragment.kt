package id.dana.clone.ui.minta

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import id.dana.clone.DanaCloneApplication
import id.dana.clone.R
import id.dana.clone.databinding.FragmentMintaBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class MintaFragment : Fragment() {

    private var _binding: FragmentMintaBinding? = null
    private val binding get() = _binding!!

    private val repository by lazy {
        (requireActivity().application as DanaCloneApplication).repository
    }

    private var requestedAmount = 0L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMintaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUserObserver()
        setupButtons()

        binding.btnBackMinta.setOnClickListener {
            (requireActivity() as? id.dana.clone.ui.MainActivity)?.navigateToHome()
        }
    }

    private fun setupUserObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            repository.userAccount.collectLatest { account ->
                account?.let {
                    binding.tvMintaUserName.text = it.fullName
                    binding.tvMintaUserPhone.text = it.phoneNumber
                }
            }
        }
    }

    private fun setupButtons() {
        binding.btnSetNominal.setOnClickListener {
            showSetNominalDialog()
        }

        binding.btnShareQr.setOnClickListener {
            val amountText = if (requestedAmount > 0) "sebesar Rp ${formatNumber(requestedAmount)}" else ""
            val shareText = "Halo, mohon transfer uang ke akun DANA saya (${binding.tvMintaUserName.text}) di nomor ${binding.tvMintaUserPhone.text} $amountText via DANA: danaclone://transfer?target=${binding.tvMintaUserPhone.text}"

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
            startActivity(Intent.createChooser(intent, "Bagikan Permintaan Saldo DANA"))
        }
    }

    private fun showSetNominalDialog() {
        val input = EditText(requireContext()).apply {
            hint = "Contoh: 50000"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            setPadding(40, 30, 40, 30)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Atur Jumlah Permintaan")
            .setMessage("Masukkan nominal uang yang ingin kamu minta:")
            .setView(input)
            .setPositiveButton("Simpan") { _, _ ->
                val amount = input.text.toString().trim().toLongOrNull() ?: 0L
                if (amount > 0) {
                    requestedAmount = amount
                    binding.tvMintaAmount.text = "Jumlah diminta: Rp ${formatNumber(amount)}"
                    Toast.makeText(requireContext(), "Nominal permintaan berhasil diatur: Rp ${formatNumber(amount)}", Toast.LENGTH_SHORT).show()
                } else {
                    requestedAmount = 0L
                    binding.tvMintaAmount.text = "Belum ada nominal yang diatur"
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
