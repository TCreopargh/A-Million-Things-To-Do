package xyz.tcreopargh.amttd.ui.todo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import xyz.tcreopargh.amttd.data.group.WorkGroup
import xyz.tcreopargh.amttd.data.todo.TodoEntry
import java.util.*

class TodoViewViewModel : ViewModel() {
    private val _entries = MutableLiveData<MutableList<TodoEntry>>().apply {
        value = mutableListOf()
    }
    val entries: LiveData<MutableList<TodoEntry>>
        get() = _entries


    fun findEntryById(uuid: UUID): TodoEntry? = entries.value?.find { it.uuid == uuid}
}