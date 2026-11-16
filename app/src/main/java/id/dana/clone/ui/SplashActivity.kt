package id.dana.clone.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import id.dana.clone.databinding.ActivitySplashBinding

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = getColor(id.dana.clone.R.color.dana_blue)
        window.navigationBarColor = getColor(id.dana.clone.R.color.dana_blue)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Seamless transition to MainActivity after splash display
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }, 1800)
    }
}

