package xyz.tcreopargh.amttd.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * @author TCreopargh
 */
abstract class ViewModelBase : ViewModel() {
    val exception = MutableLiveData<Exception?>().apply {
        value = null
    }
}
