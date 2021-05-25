package xyz.tcreopargh.amttd.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import xyz.tcreopargh.amttd.R

/**
 * @author TCreopargh
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
@SuppressLint("InflateParams")
class FabMenu(
    val context: Context,
    val fab: FloatingActionButton,
    val parent: LinearLayout
) {

    var isShowing = false

    private var isCreated = false

    val originalDrawable = fab.drawable

    val itemList = mutableListOf<FabMenuItem>()

    private var onClickListener: ((Int, FabMenuItem) -> Unit)? = null

    private var fabOnClickListener: ((View) -> Unit)? = null

    fun setOnItemClickListener(listener: ((Int, FabMenuItem) -> Unit)?) {
        onClickListener = listener
    }

    fun addItem(text: String, drawable: Drawable?) {
        val item = FabMenuItem(context, text, drawable, fab, parent, itemList.size, this)
        itemList.add(item)
    }

    fun addItem(
        @StringRes textId: Int,
        @DrawableRes drawableId: Int
    ) {
        val item = FabMenuItem(context, textId, drawableId, fab, parent, itemList.size, this)
        itemList.add(item)
    }

    fun addItem(item: FabMenuItem) {
        itemList.add(item)
    }

    fun setFabOnClickListener(listener: ((View) -> Unit)?) {
        fabOnClickListener = listener
    }

    fun create() {

        fab.setOnClickListener {
            val showing = isShowing
            fabOnClickListener?.invoke(it)
            if (showing) {
                fab.setImageDrawable(originalDrawable)
                close()
            }
        }
        for (item in itemList) {
            item.rootView.visibility = View.GONE
            parent.addView(item.rootView)
        }
        isCreated = true
    }

    fun show() {
        if (!isCreated) throw IllegalStateException("Must be created first")
        isShowing = true
        for (item in itemList) {
            item.rootView.animate().alpha(1.0f).setDuration(250L).start()
            item.rootView.visibility = View.VISIBLE
        }
        fab.setImageResource(R.drawable.ic_baseline_close_24)
    }

    fun close() {
        isShowing = false
        for (item in itemList) {
            item.rootView.animate().alpha(0.0f).setDuration(250L).start()
            item.rootView.visibility = View.GONE
        }
    }

    private fun onItemClick(index: Int) {
        onClickListener?.invoke(index, itemList[index])
    }

    class FabMenuItem(
        val context: Context,
        val text: String = "",
        val drawable: Drawable? = null,
        val fab: FloatingActionButton,
        val parent: LinearLayout,
        val index: Int,
        val menu: FabMenu
    ) {
        constructor(
            context: Context,
            @StringRes textId: Int,
            @DrawableRes drawableId: Int = R.drawable.ic_baseline_add_24,
            fab: FloatingActionButton,
            parent: LinearLayout,
            index: Int,
            menu: FabMenu
        ) : this(
            context,
            context.getString(textId),
            ContextCompat.getDrawable(context, drawableId),
            fab,
            parent,
            index,
            menu
        )

        lateinit var fabButton: FloatingActionButton
        var rootView: View
        lateinit var label: TextView

        init {
            rootView = LayoutInflater.from(context).inflate(R.layout.fab_menu_item, null)?.apply {
                label = findViewById(R.id.fabMenuText)
                fabButton = findViewById(R.id.fabMenuButton)
            }!!
            label.text = text
            fabButton.setImageDrawable(drawable)
            fabButton.setOnClickListener {
                menu.onItemClick(index)
            }
        }
    }
}