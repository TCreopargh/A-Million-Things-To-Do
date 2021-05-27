package xyz.tcreopargh.amttd.ui.todoedit

import androidx.lifecycle.MutableLiveData
import xyz.tcreopargh.amttd.common.data.ITodoEntry
import xyz.tcreopargh.amttd.ui.ViewModelBase
import java.util.*

class TodoEditViewModel : ViewModelBase() {
    val entry = MutableLiveData<ITodoEntry?>(null)

    val dirty = MutableLiveData(false)
    var entryId = MutableLiveData<UUID?>(null)
}