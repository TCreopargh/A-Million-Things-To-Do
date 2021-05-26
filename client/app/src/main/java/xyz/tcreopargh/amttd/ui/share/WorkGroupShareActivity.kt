package xyz.tcreopargh.amttd.ui.share

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import xyz.tcreopargh.amttd.BaseActivity
import xyz.tcreopargh.amttd.R

class WorkGroupShareActivity : BaseActivity() {

    private lateinit var viewModel: WorkGroupShareViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_work_group_share)
        viewModel = ViewModelProvider(this).get(WorkGroupShareViewModel::class.java)
    }
}