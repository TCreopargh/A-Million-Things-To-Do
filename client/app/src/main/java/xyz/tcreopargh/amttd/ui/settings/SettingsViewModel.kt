package xyz.tcreopargh.amttd.ui.settings

import androidx.lifecycle.MutableLiveData
import xyz.tcreopargh.amttd.ui.ViewModelBase

/**
 * @author TCreopargh
 */
class SettingsViewModel : ViewModelBase() {
    val actualUsername = MutableLiveData("")
    val actualEmail = MutableLiveData("")
    val isPasswordChanged = MutableLiveData(false)
    val isAvatarChanged = MutableLiveData(false)
}