package id.dana.clone.data.preferences

import android.content.Context
import android.content.SharedPreferences

/**
 * Encapsulation for application settings and states stored strictly inside the private
 * app internal storage sandbox:
 * /data/user/0/id.dana.clone/shared_prefs/dana_internal_prefs.xml
 */
class DanaInternalPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "dana_internal_prefs",
        Context.MODE_PRIVATE
    )

    var isBiometricEnabled: Boolean
        get() = prefs.getBoolean(KEY_BIOMETRIC_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_BIOMETRIC_ENABLED, value).apply()

    var isNotificationSoundEnabled: Boolean
        get() = prefs.getBoolean(KEY_NOTIFICATION_SOUND, true)
        set(value) = prefs.edit().putBoolean(KEY_NOTIFICATION_SOUND, value).apply()

    var isDanaProtectionActive: Boolean
        get() = prefs.getBoolean(KEY_DANA_PROTECTION, true)
        set(value) = prefs.edit().putBoolean(KEY_DANA_PROTECTION, value).apply()

    var lastLoginTimestamp: Long
        get() = prefs.getLong(KEY_LAST_LOGIN, System.currentTimeMillis())
        set(value) = prefs.edit().putLong(KEY_LAST_LOGIN, value).apply()

    companion object {
        private const val KEY_BIOMETRIC_ENABLED = "key_biometric_enabled"
        private const val KEY_NOTIFICATION_SOUND = "key_notification_sound"
        private const val KEY_DANA_PROTECTION = "key_dana_protection"
        private const val KEY_LAST_LOGIN = "key_last_login"
    }
}
