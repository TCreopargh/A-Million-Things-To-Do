package xyz.tcreopargh.amttd.ui.todoedit

import androidx.lifecycle.MutableLiveData
import xyz.tcreopargh.amttd.common.data.ITodoEntry
import xyz.tcreopargh.amttd.ui.ViewModelBase

class TodoEditViewModel : ViewModelBase() {
    val entry = MutableLiveData<ITodoEntry?>()

    val dirty = MutableLiveData<Boolean>().apply {
        value = false
    }
}