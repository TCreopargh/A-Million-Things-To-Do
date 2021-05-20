package xyz.tcreopargh.amttd.ui.settings

import androidx.lifecycle.MutableLiveData
import xyz.tcreopargh.amttd.ui.ViewModelBase

/**
 * @author TCreopargh
 */
class SettingsViewModel : ViewModelBase() {
    val actualUsername = MutableLiveData<String>().apply {
        value = ""
    }
    val actualEmail = MutableLiveData<String>().apply {
        value = ""
    }
    val isPasswordChanged = MutableLiveData<Boolean>().apply {
        value = false
    }
}