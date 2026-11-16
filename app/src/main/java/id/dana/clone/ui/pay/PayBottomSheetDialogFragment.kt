package id.dana.clone.ui.pay

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import id.dana.clone.R
import id.dana.clone.databinding.FragmentPayModalBinding

class PayBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentPayModalBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPayModalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let { sheet ->
                val behavior = BottomSheetBehavior.from(sheet)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
                sheet.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            }
        }
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnClosePayModal.setOnClickListener {
            dismiss()
        }

        binding.cameraViewfinder.setOnClickListener {
            val intent = android.content.Intent(requireContext(), PaymentSuccessActivity::class.java).apply {
                putExtra(PaymentSuccessActivity.EXTRA_MERCHANT, "NADDD, Toko HP, Pulsa &\nI")
                putExtra(PaymentSuccessActivity.EXTRA_AMOUNT, 8000L)
                putExtra(PaymentSuccessActivity.EXTRA_FORMATTED_AMOUNT, "Rp8.000")
                putExtra(PaymentSuccessActivity.EXTRA_BALANCE, "Rp 9.498")
            }
            startActivity(intent)
            dismiss()
        }

        binding.btnPayGallery.setOnClickListener {
            val intent = android.content.Intent(requireContext(), PaymentSuccessActivity::class.java).apply {
                putExtra(PaymentSuccessActivity.EXTRA_MERCHANT, "NADDD, Toko HP, Pulsa &\nI")
                putExtra(PaymentSuccessActivity.EXTRA_AMOUNT, 8000L)
                putExtra(PaymentSuccessActivity.EXTRA_FORMATTED_AMOUNT, "Rp8.000")
                putExtra(PaymentSuccessActivity.EXTRA_BALANCE, "Rp 9.498")
            }
            startActivity(intent)
            dismiss()
        }

        binding.tabScan.setOnClickListener {
            binding.tabScan.setBackgroundResource(R.drawable.bg_rounded_dana_blue_bar)
            binding.tabScan.setTextColor(requireContext().getColor(R.color.white))
            binding.tabQrPay.background = null
            binding.tabQrPay.setTextColor(requireContext().getColor(R.color.text_muted))
        }

        binding.tabQrPay.setOnClickListener {
            binding.tabQrPay.setBackgroundResource(R.drawable.bg_rounded_dana_blue_bar)
            binding.tabQrPay.setTextColor(requireContext().getColor(R.color.white))
            binding.tabScan.background = null
            binding.tabScan.setTextColor(requireContext().getColor(R.color.text_muted))
            Toast.makeText(requireContext(), "Beralih ke QR PAY Saya", Toast.LENGTH_SHORT).show()
        }

        binding.switchSmartpayPay.setOnCheckedChangeListener { _, isChecked ->
            val status = if (isChecked) "Aktif" else "Nonaktif"
            Toast.makeText(requireContext(), "SMARTPAY DANA Balance: $status", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "PayBottomSheetDialogFragment"
        fun newInstance() = PayBottomSheetDialogFragment()
    }
}

