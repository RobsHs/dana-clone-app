package id.dana.clone.ui.kirim

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import id.dana.clone.DanaCloneApplication
import id.dana.clone.R
import id.dana.clone.data.database.entities.BeneficiaryEntity
import id.dana.clone.data.repository.TransactionResult
import id.dana.clone.databinding.FragmentKirimBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class KirimFragment : Fragment() {

    private var _binding: FragmentKirimBinding? = null
    private val binding get() = _binding!!

    private val repository by lazy {
        (requireActivity().application as DanaCloneApplication).repository
    }

    private var availableBalance = 0L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKirimBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBalanceObserver()
        setupNominalChips()
        setupBeneficiariesObserver()
        setupSubmitButton()

        binding.btnBackKirim.setOnClickListener {
            (requireActivity() as? id.dana.clone.ui.MainActivity)?.navigateToHome()
        }
    }

    private fun setupBalanceObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            repository.userAccount.collectLatest { account ->
                account?.let {
                    availableBalance = it.balance
                    binding.tvAvailableBalance.text = "Rp ${formatNumber(it.balance)}"
                }
            }
        }
    }

    private fun setupNominalChips() {
        binding.chip50k.setOnClickListener { binding.etAmount.setText("50000") }
        binding.chip100k.setOnClickListener { binding.etAmount.setText("100000") }
        binding.chip250k.setOnClickListener { binding.etAmount.setText("250000") }
        binding.chip500k.setOnClickListener { binding.etAmount.setText("500000") }
    }

    private fun setupBeneficiariesObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            repository.beneficiaries.collectLatest { list ->
                binding.containerBeneficiaries.removeAllViews()

                for (ben in list) {
                    val benView = LayoutInflater.from(requireContext())
                        .inflate(R.layout.item_beneficiary, binding.containerBeneficiaries, false)

                    val tvName = benView.findViewById<TextView>(R.id.tvBenName)
                    val tvDetails = benView.findViewById<TextView>(R.id.tvBenDetails)

                    tvName.text = ben.name
                    tvDetails.text = "${ben.bankOrProvider} - ${ben.accountNumber}"

                    benView.setOnClickListener {
                        binding.etRecipientName.setText(ben.name)
                        binding.etAccountOrPhone.setText(ben.accountNumber)
                        Toast.makeText(requireContext(), "Penerima ${ben.name} dipilih", Toast.LENGTH_SHORT).show()
                    }

                    binding.containerBeneficiaries.addView(benView)
                }
            }
        }
    }

    private fun setupSubmitButton() {
        binding.btnSubmitTransfer.setOnClickListener {
            val name = binding.etRecipientName.text?.toString()?.trim().orEmpty()
            val account = binding.etAccountOrPhone.text?.toString()?.trim().orEmpty()
            val amountStr = binding.etAmount.text?.toString()?.trim().orEmpty()
            val note = binding.etNote.text?.toString()?.trim().orEmpty()

            if (name.isBlank()) {
                binding.tilRecipientName.error = "Nama penerima harus diisi"
                return@setOnClickListener
            } else {
                binding.tilRecipientName.error = null
            }

            if (account.isBlank()) {
                binding.tilAccountOrPhone.error = "Nomor telepon / rekening harus diisi"
                return@setOnClickListener
            } else {
                binding.tilAccountOrPhone.error = null
            }

            val amount = amountStr.toLongOrNull() ?: 0L
            if (amount < 10000L) {
                binding.tilAmount.error = "Jumlah minimum transfer adalah Rp 10.000"
                return@setOnClickListener
            } else {
                binding.tilAmount.error = null
            }

            if (amount > availableBalance) {
                binding.tilAmount.error = "Saldo DANA tidak mencukupi (Tersedia: Rp ${formatNumber(availableBalance)})"
                return@setOnClickListener
            }

            // Execute local internal transaction
            viewLifecycleOwner.lifecycleScope.launch {
                when (val result = repository.sendMoney(name, account, amount, note)) {
                    is TransactionResult.Success -> {
                        showSuccessDialog(result)
                        clearForm()
                    }
                    is TransactionResult.Error -> {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun showSuccessDialog(result: TransactionResult.Success) {
        val trx = result.transaction
        AlertDialog.Builder(requireContext())
            .setTitle("Transfer Berhasil!")
            .setMessage(
                "ID Transaksi: ${trx.id}\n" +
                "Penerima: ${trx.recipientOrSender}\n" +
                "Nomor/Akun: ${trx.accountOrPhone}\n" +
                "Jumlah: Rp ${formatNumber(trx.amount)}\n" +
                "Biaya Transaksi: Rp 0 (Gratis)\n" +
                "Sisa Saldo Internal: Rp ${formatNumber(result.newBalance)}\n\n" +
                "Data tersimpan dalam database internal Room (/data/user/0/id.dana.clone/)"
            )
            .setPositiveButton("Selesai") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    private fun clearForm() {
        binding.etRecipientName.text?.clear()
        binding.etAccountOrPhone.text?.clear()
        binding.etAmount.text?.clear()
        binding.etNote.text?.clear()
    }

    private fun formatNumber(number: Long): String {
        return NumberFormat.getNumberInstance(Locale("id", "ID")).format(number)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
