package xyz.tcreopargh.amttd.ui.share

import androidx.lifecycle.MutableLiveData
import xyz.tcreopargh.amttd.ui.ViewModelBase

/**
 * @author TCreopargh
 */
class WorkGroupShareViewModel : ViewModelBase() {
    val invitationCode = MutableLiveData<String?>().apply {
        value = null
    }
}