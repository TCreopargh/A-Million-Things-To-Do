package xyz.tcreopargh.amttd.ui

import androidx.fragment.app.Fragment
import xyz.tcreopargh.amttd.MainActivity

/**
 * @author TCreopargh
 */
abstract class FragmentOnMainActivityBase : Fragment() {

    override fun onStart() {
        super.onStart()
        (activity as? MainActivity)?.onFragmentStart(this)
    }

    override fun onStop() {
        super.onStop()
        (activity as? MainActivity)?.onFragmentStop(this)
    }
}