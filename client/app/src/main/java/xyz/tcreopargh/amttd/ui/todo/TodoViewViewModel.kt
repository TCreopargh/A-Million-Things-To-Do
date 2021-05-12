package xyz.tcreopargh.amttd.ui.todo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import xyz.tcreopargh.amttd.data.interactive.ITodoEntry
import xyz.tcreopargh.amttd.ui.ViewModelBase
import java.util.*

class TodoViewViewModel : ViewModelBase() {
    private val _entries = MutableLiveData<MutableList<ITodoEntry>>().apply {
        value = mutableListOf()
    }
    val entries: LiveData<MutableList<ITodoEntry>>
        get() = _entries

    fun findEntryById(uuid: UUID): ITodoEntry? = entries.value?.find { it.entryId == uuid }

    fun setEntry(value: MutableList<ITodoEntry>) {
        _entries.value = value
    }

    fun postEntry(value: MutableList<ITodoEntry>) {
        _entries.postValue(value)
    }
}