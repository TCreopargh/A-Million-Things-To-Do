package xyz.tcreopargh.amttd.ui

import androidx.fragment.app.Fragment
import xyz.tcreopargh.amttd.MainActivity

/**
 * @author TCreopargh
 */
abstract class FragmentOnMainActivityBase : Fragment() {

    val mainActivity
        get() = activity as MainActivity

    val loggedInUser
        get() = mainActivity.loggedInUser

    override fun onStart() {
        super.onStart()
        mainActivity.onFragmentStart(this)
    }

    override fun onStop() {
        super.onStop()
        mainActivity.onFragmentStop(this)
    }
}