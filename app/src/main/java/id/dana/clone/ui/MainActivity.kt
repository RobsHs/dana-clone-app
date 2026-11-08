package id.dana.clone.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import id.dana.clone.R
import id.dana.clone.databinding.ActivityMainBinding
import id.dana.clone.ui.home.HomeFragment
import id.dana.clone.ui.kirim.KirimFragment
import id.dana.clone.ui.minta.MintaFragment
import id.dana.clone.ui.riwayat.RiwayatFragment
import id.dana.clone.ui.saya.SayaFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.nav_riwayat -> {
                    loadFragment(RiwayatFragment())
                    true
                }
                R.id.nav_pay -> {
                    // Pay (QRIS) action
                    navigateToPay()
                    true
                }
                R.id.nav_minta -> {
                    loadFragment(MintaFragment())
                    true
                }
                R.id.nav_saya -> {
                    loadFragment(SayaFragment())
                    true
                }
                else -> false
            }
        }
    }

    fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    fun navigateToKirim() {
        loadFragment(KirimFragment())
    }

    fun navigateToMinta() {
        binding.bottomNav.selectedItemId = R.id.nav_minta
    }

    fun navigateToRiwayat() {
        binding.bottomNav.selectedItemId = R.id.nav_riwayat
    }

    fun navigateToPay() {
        // Open QRIS scanner simulation
        loadFragment(MintaFragment())
    }
}
