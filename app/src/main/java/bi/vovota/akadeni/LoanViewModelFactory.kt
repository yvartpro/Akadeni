package bi.vovota.akadeni

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import bi.vovota.akadeni.data.local.AppPrefsManager
import bi.vovota.akadeni.data.repo.LoanRepo

/**
 * Factory for [LoanViewModel].
 *
 * Required because [LoanViewModel] extends [AndroidViewModel] and takes
 * custom constructor parameters ([LoanRepo], [AppPrefsManager]).
 * Using this factory instead of [ViewModelProvider.AndroidViewModelFactory]
 * lets us inject our own dependencies cleanly without Hilt/Koin.
 */
class LoanViewModelFactory(
    private val application: Application,
    private val repo: LoanRepo,
    private val appPrefs: AppPrefsManager,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoanViewModel::class.java)) {
            return LoanViewModel(application, repo, appPrefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
