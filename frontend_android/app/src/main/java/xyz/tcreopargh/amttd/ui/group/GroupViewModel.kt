package xyz.tcreopargh.amttd.ui.group

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import xyz.tcreopargh.amttd.data.group.WorkGroup

/**
 * @author TCreopargh
 */
class GroupViewModel : ViewModel() {

    private val _groups = MutableLiveData<MutableList<WorkGroup>>().apply {
        value = mutableListOf()
    }
    val groups: LiveData<MutableList<WorkGroup>> = _groups
}
