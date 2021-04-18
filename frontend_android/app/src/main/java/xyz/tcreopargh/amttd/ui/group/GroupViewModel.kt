package xyz.tcreopargh.amttd.ui.group

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * @author TCreopargh
 */
class GroupViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Group View"
    }
    val text: LiveData<String> = _text
}