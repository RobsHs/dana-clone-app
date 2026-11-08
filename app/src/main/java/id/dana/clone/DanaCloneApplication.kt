package id.dana.clone

import android.app.Application
import id.dana.clone.data.database.DanaDatabase
import id.dana.clone.data.preferences.DanaInternalPreferences
import id.dana.clone.data.repository.DanaInternalRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class DanaCloneApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { DanaDatabase.getInstance(this, applicationScope) }
    val repository by lazy { DanaInternalRepository(database) }
    val preferences by lazy { DanaInternalPreferences(this) }

    override fun onCreate() {
        super.onCreate()
    }
}
