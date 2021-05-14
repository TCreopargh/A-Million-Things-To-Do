package xyz.tcreopargh.amttd.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import xyz.tcreopargh.amttd.data.exception.AmttdException

/**
 * @author TCreopargh
 */
abstract class ViewModelBase : ViewModel() {
    val exception = MutableLiveData<AmttdException?>().apply {
        value = null
    }
}
