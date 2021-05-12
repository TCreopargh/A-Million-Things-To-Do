package xyz.tcreopargh.amttd.ui.group

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.group_view_fragment.*
import okhttp3.Request
import xyz.tcreopargh.amttd.AMTTD
import xyz.tcreopargh.amttd.MainActivity
import xyz.tcreopargh.amttd.R
import xyz.tcreopargh.amttd.data.interactive.IWorkGroup
import xyz.tcreopargh.amttd.data.interactive.WorkGroupImpl
import xyz.tcreopargh.amttd.ui.FragmentOnMainActivityBase
import xyz.tcreopargh.amttd.util.*
import java.io.IOException
import java.util.*

/**
 * @author TCreopargh
 */
class GroupViewFragment : FragmentOnMainActivityBase() {

    companion object {
        fun newInstance() = GroupViewFragment()
    }

    private lateinit var viewModel: GroupViewModel

    private lateinit var groupSwipeContainer: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.group_view_fragment, container, false)
        val groupRecyclerView = view.findViewById<RecyclerView>(R.id.groupRecyclerView)
        groupSwipeContainer = view.findViewById(R.id.groupSwipeContainer)
        val adapter = GroupViewAdapter(viewModel.groups.value ?: mutableListOf(), activity)
        groupRecyclerView.adapter = adapter
        groupRecyclerView.layoutManager = LinearLayoutManager(context)
        groupSwipeContainer.setOnRefreshListener { initializeItems() }
        viewModel.groups.observe(viewLifecycleOwner) {
            adapter.workGroups = it ?: mutableListOf()
            adapter.notifyDataSetChanged()
            groupSwipeContainer.isRefreshing = false
        }

        viewModel.exception.observe(viewLifecycleOwner) {
            it?.run {
                Toast.makeText(
                    context,
                    getString(R.string.error_occured) + it.message,
                    Toast.LENGTH_SHORT
                ).show()
                groupSwipeContainer.isRefreshing = false
            }
        }

        val mainActivity = activity as? MainActivity
        mainActivity?.viewModel?.loginRepository?.observe(mainActivity) {
            initializeItems()
        }
        initializeItems()
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel =
            ViewModelProvider(this).get(GroupViewModel::class.java)
        setHasOptionsMenu(true)
    }


    private fun initializeItems() {
        groupSwipeContainer.isRefreshing = true
        Thread {
            val workGroups: List<IWorkGroup> = try {
                val uuid = (activity as? MainActivity)?.loggedInUser?.uuid ?: return@Thread
                val request = Request.Builder()
                    .post(
                        jsonObjectOf(
                            "uuid" to uuid
                        ).toRequestBody()
                    ).url(rootUrl.withPath("/workgroups"))
                    .build()
                val response = AMTTD.okHttpClient.newCall(request).execute()
                val body = response.body?.string()
                // Don't simplify this
                val result: List<IWorkGroup> = try {
                    gson.fromJson(body, object : TypeToken<List<WorkGroupImpl>>() {}.type)
                } catch (e: RuntimeException) {
                    throw IOException(e)
                }
                result
            } catch (e: IOException) {
                Log.e(AMTTD.logTag, e.stackTraceToString())
                viewModel.exception.postValue(e)
                listOf()
            }
            viewModel.postGroup(workGroups.toMutableList())
        }.start()
    }

}