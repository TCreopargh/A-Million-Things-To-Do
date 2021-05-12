package xyz.tcreopargh.amttd.ui.group

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import xyz.tcreopargh.amttd.data.interactive.IWorkGroup
import xyz.tcreopargh.amttd.ui.ViewModelBase
import java.util.*

/**
 * @author TCreopargh
 */
class GroupViewModel : ViewModelBase() {

    private val _groups = MutableLiveData<MutableList<IWorkGroup>>().apply {
        value = mutableListOf()
    }
    val groups: LiveData<MutableList<IWorkGroup>> = _groups

    fun findGroupById(uuid: UUID): IWorkGroup? = groups.value?.find { it.groupId == uuid }

    fun setGroup(value: MutableList<IWorkGroup>) {
        _groups.value = value
    }

    fun postGroup(value: MutableList<IWorkGroup>) {
        _groups.postValue(value)
    }
}
