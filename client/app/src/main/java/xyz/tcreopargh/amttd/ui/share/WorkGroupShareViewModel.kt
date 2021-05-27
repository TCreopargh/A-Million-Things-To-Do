package xyz.tcreopargh.amttd.ui.share

import androidx.lifecycle.MutableLiveData
import xyz.tcreopargh.amttd.common.data.IWorkGroup
import xyz.tcreopargh.amttd.data.user.LocalUser
import xyz.tcreopargh.amttd.ui.ViewModelBase

/**
 * @author TCreopargh
 */
class WorkGroupShareViewModel : ViewModelBase() {
    val invitationCode = MutableLiveData<String?>(null)
    val workGroup = MutableLiveData<IWorkGroup?>(null)
    val loggedInUser = MutableLiveData<LocalUser?>(null)
    val expirationTimeInDays = MutableLiveData(1)
}