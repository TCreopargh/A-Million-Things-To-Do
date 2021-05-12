package xyz.tcreopargh.amttd.ui.todoedit

import androidx.lifecycle.MutableLiveData
import xyz.tcreopargh.amttd.data.interactive.ITodoEntry
import xyz.tcreopargh.amttd.ui.ViewModelBase

class TodoEditViewModel : ViewModelBase() {
    val entry = MutableLiveData<ITodoEntry?>()
}