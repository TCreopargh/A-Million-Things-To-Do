package xyz.tcreopargh.amttd.ui

import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import xyz.tcreopargh.amttd.MainActivity
import xyz.tcreopargh.amttd.R

/**
 * @author TCreopargh
 */
abstract class FragmentOnMainActivityBase(@StringRes val titleId: Int? = null) : Fragment() {

    val mainActivity
        get() = activity as MainActivity

    val loggedInUser
        get() = mainActivity.loggedInUser

    override fun onStart() {
        super.onStart()
        mainActivity.onFragmentStart(this)
        if (titleId != null) {
            activity?.setTitle(titleId)
        } else {
            activity?.setTitle(R.string.app_name)
        }
    }

    override fun onStop() {
        super.onStop()
        mainActivity.onFragmentStop(this)
    }
}