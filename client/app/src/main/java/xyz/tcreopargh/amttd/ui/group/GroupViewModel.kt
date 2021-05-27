package xyz.tcreopargh.amttd.ui.group

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import xyz.tcreopargh.amttd.common.data.IWorkGroup
import xyz.tcreopargh.amttd.ui.ViewModelBase
import java.util.*

/**
 * @author TCreopargh
 */
class GroupViewModel : ViewModelBase() {

    private val _groups = MutableLiveData<List<IWorkGroup>>(listOf())
    val groups: LiveData<List<IWorkGroup>> = _groups

    val groupToEdit = MutableLiveData<IWorkGroup?>(null)

    val dirty = MutableLiveData(false)

    fun findGroupById(uuid: UUID): IWorkGroup? = groups.value?.find { it.groupId == uuid }

    fun setGroup(value: List<IWorkGroup>) {
        _groups.value = value
    }

    fun postGroup(value: List<IWorkGroup>) {
        _groups.postValue(value)
    }
}
