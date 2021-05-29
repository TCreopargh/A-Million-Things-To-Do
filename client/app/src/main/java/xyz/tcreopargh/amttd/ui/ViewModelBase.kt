package xyz.tcreopargh.amttd.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import xyz.tcreopargh.amttd.common.exception.AmttdException

/**
 * @author TCreopargh
 *
 * Base class for all view models, because all fragments should handle and display any possible error.
 */
abstract class ViewModelBase : ViewModel() {
    val exception = MutableLiveData<AmttdException?>().apply {
        value = null
    }
}
