package xyz.tcreopargh.amttd.ui.group_user

import androidx.lifecycle.MutableLiveData
import xyz.tcreopargh.amttd.common.data.IUser
import xyz.tcreopargh.amttd.common.data.IWorkGroup
import xyz.tcreopargh.amttd.ui.ViewModelBase

class GroupUserViewModel : ViewModelBase() {
    val dirty = MutableLiveData(false)
    val users = MutableLiveData(listOf<IUser>())
    val workGroup = MutableLiveData<IWorkGroup?>(null)
}