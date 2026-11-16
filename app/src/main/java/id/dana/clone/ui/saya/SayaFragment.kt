package id.dana.clone.ui.saya

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
import id.dana.clone.databinding.FragmentSayaBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class SayaFragment : Fragment() {

    private var _binding: FragmentSayaBinding? = null
    private val binding get() = _binding!!

    private val repository by lazy {
        (requireActivity().application as DanaCloneApplication).repository
    }

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

        setupUserAccountObserver()
        setupTabs()
        setupMenuActions()
    }

    private fun setupUserAccountObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            repository.userAccount.collectLatest { account ->
                account?.let {
                    val formatted = NumberFormat.getNumberInstance(Locale("id", "ID")).format(it.balance)
                    binding.tvMeBalance.text = "Rp$formatted"
                    binding.tvProfilePhone.text = it.phoneNumber.ifBlank { "08••••••••24 >" }
                }
            }
        }
    }

    private fun setupTabs() {
        binding.tabPersonal.setOnClickListener {
            binding.tabPersonal.setBackgroundResource(R.drawable.bg_tab_active_pill)
            binding.tabPersonal.setTextColor(requireContext().getColor(R.color.dana_blue))
            binding.tabBisnis.background = null
            binding.tabBisnis.setTextColor(requireContext().getColor(R.color.white))
        }

        binding.tabBisnis.setOnClickListener {
            binding.tabBisnis.setBackgroundResource(R.drawable.bg_tab_active_pill)
            binding.tabBisnis.setTextColor(requireContext().getColor(R.color.dana_blue))
            binding.tabPersonal.background = null
            binding.tabPersonal.setTextColor(requireContext().getColor(R.color.white))
            Toast.makeText(requireContext(), "Beralih ke Profil Bisnis", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupMenuActions() {
        binding.btnMyQr.setOnClickListener {
            showQrDialog()
        }

        binding.btnXtraProtection.setOnClickListener {
            Toast.makeText(requireContext(), "Xtra Protection: 100% Proteksi Saldo Aktif", Toast.LENGTH_SHORT).show()
        }

        binding.ivPremiumFailedBanner.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Status DANA Premium")
                .setMessage("Verifikasi DANA Premium belum berhasil. Silakan unggah foto e-KTP dan selfie ulang untuk menikmati limit transaksi lebih tinggi.")
                .setPositiveButton("Coba Lagi", null)
                .setNegativeButton("Tutup", null)
                .show()
        }

        binding.menuMyBills.setOnClickListener {
            Toast.makeText(requireContext(), "My Bills: Semua tagihan sudah lunas", Toast.LENGTH_SHORT).show()
        }

        binding.menuSkinCollection.setOnClickListener {
            Toast.makeText(requireContext(), "Skin Collection: 3 Skin Pokémon Aktif!", Toast.LENGTH_SHORT).show()
        }

        binding.menuVoucherPromo.setOnClickListener {
            Toast.makeText(requireContext(), "Voucher Promo: TEMPO Rp100.000 & Leonian Rp192.000", Toast.LENGTH_SHORT).show()
        }

        binding.menuSettings.setOnClickListener {
            Toast.makeText(requireContext(), "Pengaturan Keamanan & Akun DANA", Toast.LENGTH_SHORT).show()
        }

        binding.menuGeneralInfo.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("General Info DANA")
                .setMessage(
                    "App Version: 2.151.0\n" +
                    "PT Espay Debit Indonesia Koe\n" +
                    "Terdaftar serta diawasi oleh Bank Indonesia dan Komdigi"
                )
                .setPositiveButton("OK", null)
                .show()
        }

        binding.menuDianaHelp.setOnClickListener {
            Toast.makeText(requireContext(), "Menghubungi DIANA Customer Support...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showQrDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("MY QR CODE")
            .setMessage("Tunjukkan QR ini ke kasir atau teman untuk menerima saldo DANA secara instan.\n\nNomor Akun: 08••••••••24")
            .setPositiveButton("Tutup", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
