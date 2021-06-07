package xyz.tcreopargh.amttd.ui.group_user

import androidx.lifecycle.MutableLiveData
import xyz.tcreopargh.amttd.api.data.IUser
import xyz.tcreopargh.amttd.api.data.IWorkGroup
import xyz.tcreopargh.amttd.ui.ViewModelBase

class GroupUserViewModel : ViewModelBase() {
    val dirty = MutableLiveData(false)
    val users = MutableLiveData<List<IUser>?>(null)
    val workGroup = MutableLiveData<IWorkGroup?>(null)
}