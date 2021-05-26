package xyz.tcreopargh.amttd.ui.share

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import xyz.tcreopargh.amttd.BaseActivity
import xyz.tcreopargh.amttd.R
import java.util.*

class WorkGroupShareActivity : BaseActivity() {

    private lateinit var viewModel: WorkGroupShareViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_work_group_share)
        viewModel = ViewModelProvider(this).get(WorkGroupShareViewModel::class.java)
        try {
            val groupId = UUID.fromString(intent.getStringExtra("groupId"))
            val userId = UUID.fromString(intent.getStringExtra("userId"))
        } catch (e: Exception) {
            Toast.makeText(this, e.localizedMessage, Toast.LENGTH_LONG).show()
        }
    }
}