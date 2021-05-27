package xyz.tcreopargh.amttd.ui.group_user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import xyz.tcreopargh.amttd.common.data.IUser
import xyz.tcreopargh.amttd.ui.ViewModelBase
import java.util.*

class GroupUserViewModel : ViewModelBase() {
    val dirty = MutableLiveData(false)
    val users = MutableLiveData(listOf<IUser>())
    var groupId = MutableLiveData<UUID?>(null)
    var isLeader = MutableLiveData(false)
}