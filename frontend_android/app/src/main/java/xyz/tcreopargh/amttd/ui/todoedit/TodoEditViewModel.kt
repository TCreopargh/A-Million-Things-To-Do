package xyz.tcreopargh.amttd.ui.todoedit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import xyz.tcreopargh.amttd.data.todo.TodoEntry

class TodoEditViewModel : ViewModel() {
    val entry = MutableLiveData<TodoEntry>()
}