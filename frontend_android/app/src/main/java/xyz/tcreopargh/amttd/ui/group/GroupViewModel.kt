package xyz.tcreopargh.amttd.ui.group

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import xyz.tcreopargh.amttd.data.interactive.IWorkGroup
import java.util.*

/**
 * @author TCreopargh
 */
class GroupViewModel() : ViewModel() {

    private val _groups = MutableLiveData<MutableList<IWorkGroup>>().apply {
        value = mutableListOf()
    }
    val groups: LiveData<MutableList<IWorkGroup>> = _groups

    fun findGroupById(uuid: UUID): IWorkGroup? = groups.value?.find { it.groupId == uuid }
}
