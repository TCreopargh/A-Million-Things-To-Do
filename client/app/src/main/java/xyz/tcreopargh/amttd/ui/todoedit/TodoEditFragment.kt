package xyz.tcreopargh.amttd.ui.todoedit

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.gson.reflect.TypeToken
import okhttp3.Request
import xyz.tcreopargh.amttd.AMTTD
import xyz.tcreopargh.amttd.R
import xyz.tcreopargh.amttd.data.interactive.ITodoEntry
import xyz.tcreopargh.amttd.data.interactive.TodoEntryImpl
import xyz.tcreopargh.amttd.util.*
import java.util.*

class TodoEditFragment : Fragment() {

    companion object {
        fun newInstance() = TodoEditFragment()
    }

    private lateinit var viewModel: TodoEditViewModel

    private var entryId: UUID? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.todo_edit_fragment, container, false)
        viewModel.entry.observe(viewLifecycleOwner) { initView(view, it) }
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TodoEditViewModel::class.java)
        entryId = arguments?.get("entryId") as? UUID
        initializeItems()
    }

    private fun initializeItems() {

        Thread {
            val uuid = entryId ?: return@Thread
            val request = Request.Builder()
                .post(
                    jsonObjectOf(
                        "entryId" to uuid
                    ).toRequestBody()
                ).url(rootUrl.withPath("/todo-entry"))
                .build()
            val response = AMTTD.okHttpClient.newCall(request).execute()
            val body = response.body?.string()
            val entry: ITodoEntry? = try {
                gson.fromJson(body, object : TypeToken<TodoEntryImpl>() {}.type)
            } catch (e: RuntimeException) {
                Log.e(AMTTD.logTag, e.stackTraceToString())
                null
            }
            viewModel.entry.postValue(entry)
        }.start()
    }

    private fun initView(viewRoot: View, entry: ITodoEntry?) {
        if (entry == null) {
            Toast.makeText(context, R.string.unknown_error, Toast.LENGTH_SHORT).show()
            return
        }
        viewRoot.apply {
            findViewById<EditText>(R.id.todoEditTitleText).setText(
                entry.title,
                TextView.BufferType.EDITABLE
            )
            findViewById<EditText>(R.id.todoEditDescriptionText)?.setText(
                entry.description,
                TextView.BufferType.EDITABLE
            )
            findViewById<TextView>(R.id.todoEditStatusText)?.text = entry.status.getDisplayString()
            findViewById<TextView>(R.id.todoEditDeadlineText)?.text =
                entry.deadline?.format() ?: getString(R.string.deadline_not_set)
            findViewById<ImageView>(R.id.todoEditIconColor)?.setColorFilter(
                entry.status.color,
                android.graphics.PorterDuff.Mode.SRC
            )
        }
    }

}