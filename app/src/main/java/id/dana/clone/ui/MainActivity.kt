package id.dana.clone.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import id.dana.clone.R
import id.dana.clone.databinding.ActivityMainBinding
import id.dana.clone.ui.home.HomeFragment
import id.dana.clone.ui.kirim.KirimFragment
import id.dana.clone.ui.pay.PayBottomSheetDialogFragment
import id.dana.clone.ui.riwayat.RiwayatFragment
import id.dana.clone.ui.saya.SayaFragment
import id.dana.clone.ui.wallet.WalletFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val homeFragment = HomeFragment()
    private val riwayatFragment = RiwayatFragment()
    private val walletFragment = WalletFragment()
    private val sayaFragment = SayaFragment()
    private val kirimFragment = KirimFragment()

    private var currentTab = "HOME"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            navigateToHome()
        }

        setupBottomBar()
    }

    private fun setupBottomBar() {
        binding.btnNavHome.setOnClickListener {
            navigateToHome()
        }

        binding.btnNavActivity.setOnClickListener {
            navigateToActivity()
        }

        binding.btnNavPayCenter.setOnClickListener {
            navigateToPay()
        }

        binding.btnNavWallet.setOnClickListener {
            navigateToWallet()
        }

        binding.btnNavMe.setOnClickListener {
            navigateToMe()
        }
    }

    fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    fun navigateToHome() {
        currentTab = "HOME"
        updateTabSelection()
        loadFragment(homeFragment)
    }

    fun navigateToActivity() {
        currentTab = "ACTIVITY"
        updateTabSelection()
        loadFragment(riwayatFragment)
    }

    fun navigateToWallet() {
        currentTab = "WALLET"
        updateTabSelection()
        loadFragment(walletFragment)
    }

    fun navigateToMe() {
        currentTab = "ME"
        updateTabSelection()
        loadFragment(sayaFragment)
    }

    fun navigateToPay() {
        PayBottomSheetDialogFragment.newInstance()
            .show(supportFragmentManager, PayBottomSheetDialogFragment.TAG)
    }

    fun navigateToKirim() {
        loadFragment(kirimFragment)
    }

    private fun updateTabSelection() {
        val activeColor = getColor(R.color.text_primary)
        val inactiveColor = getColor(R.color.text_secondary)

        // Reset all
        binding.tvNavHome.setTextColor(if (currentTab == "HOME") activeColor else inactiveColor)
        binding.ivNavHome.setColorFilter(if (currentTab == "HOME") activeColor else inactiveColor)

        binding.tvNavActivity.setTextColor(if (currentTab == "ACTIVITY") activeColor else inactiveColor)

        binding.tvNavWallet.setTextColor(if (currentTab == "WALLET") activeColor else inactiveColor)
        binding.ivNavWallet.setColorFilter(if (currentTab == "WALLET") activeColor else inactiveColor)

        binding.tvNavMe.setTextColor(if (currentTab == "ME") activeColor else inactiveColor)
    }
}
