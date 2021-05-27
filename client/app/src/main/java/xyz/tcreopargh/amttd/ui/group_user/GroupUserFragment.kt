package xyz.tcreopargh.amttd.ui.group_user

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import xyz.tcreopargh.amttd.AMTTD
import xyz.tcreopargh.amttd.R
import xyz.tcreopargh.amttd.ui.FragmentOnMainActivityBase
import java.util.*

class GroupUserFragment : FragmentOnMainActivityBase() {

    companion object {
        fun newInstance() = GroupUserFragment()
    }
    lateinit var viewModel: GroupUserViewModel
    private lateinit var swipeContainer: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.group_user_fragment, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.groupUserRecyclerView)

        swipeContainer = view.findViewById(R.id.groupUserSwipeContainer)
        val adapter = GroupUserAdapter(viewModel.users.value ?: listOf(), this)
        swipeContainer.setOnRefreshListener { initializeItems() }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        viewModel.users.observe(viewLifecycleOwner) {
            adapter.users = it ?: mutableListOf()
            adapter.notifyDataSetChanged()
            swipeContainer.isRefreshing = false
        }

        viewModel.exception.observe(viewLifecycleOwner) {
            it?.run {
                Toast.makeText(
                    context,
                    getString(R.string.error_occurred) + it.getLocalizedString(context),
                    Toast.LENGTH_SHORT
                ).show()
                swipeContainer.isRefreshing = false
                viewModel.exception.value = null
            }
        }
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = arguments?.deepCopy()
        viewModel = ViewModelProvider(this).get(GroupUserViewModel::class.java)
        viewModel.groupId.value = UUID.fromString(args?.getString("groupId"))
        viewModel.isLeader.value = args?.getBoolean("isLeader") ?: false
    }


    private fun initializeItems() {
        swipeContainer.isRefreshing = true
        Thread {
            // TODO: Send request to server
        }.start()
    }
}