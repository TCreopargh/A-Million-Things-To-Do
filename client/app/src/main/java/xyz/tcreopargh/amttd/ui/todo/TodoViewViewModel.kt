package xyz.tcreopargh.amttd.ui.todo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import xyz.tcreopargh.amttd.api.data.ITodoEntry
import xyz.tcreopargh.amttd.api.data.IWorkGroup
import xyz.tcreopargh.amttd.ui.ViewModelBase
import java.util.*

class TodoViewViewModel : ViewModelBase() {
    private val _entries = MutableLiveData<List<ITodoEntry>?>(null)
    val entries: LiveData<List<ITodoEntry>?>
        get() = _entries
    val workGroup = MutableLiveData<IWorkGroup?>(null)

    val dirty = MutableLiveData(false)

    fun findEntryById(uuid: UUID): ITodoEntry? = entries.value?.find { it.entryId == uuid }

    fun setEntry(value: List<ITodoEntry>) {
        _entries.value = value
    }

    fun postEntry(value: List<ITodoEntry>) {
        _entries.postValue(value)
    }
}