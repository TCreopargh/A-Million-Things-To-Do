package xyz.tcreopargh.amttd

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.*
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.app_bar_main.view.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import xyz.tcreopargh.amttd.common.bean.request.ActionCrudRequest
import xyz.tcreopargh.amttd.common.bean.response.ActionCrudResponse
import xyz.tcreopargh.amttd.common.data.CrudType
import xyz.tcreopargh.amttd.common.data.action.ActionGeneric
import xyz.tcreopargh.amttd.common.data.action.ActionType
import xyz.tcreopargh.amttd.common.exception.AmttdException
import xyz.tcreopargh.amttd.data.login.LoginResult
import xyz.tcreopargh.amttd.data.user.LocalUser
import xyz.tcreopargh.amttd.ui.group.GroupViewFragment
import xyz.tcreopargh.amttd.ui.group_user.GroupUserFragment
import xyz.tcreopargh.amttd.ui.login.LoginActivity
import xyz.tcreopargh.amttd.ui.settings.SettingsFragment
import xyz.tcreopargh.amttd.ui.share.WorkGroupShareActivity
import xyz.tcreopargh.amttd.ui.todo.TodoViewFragment
import xyz.tcreopargh.amttd.ui.todoedit.TodoEditFragment
import xyz.tcreopargh.amttd.util.*
import java.lang.ref.WeakReference
import java.util.*


/**
 * @author TCreopargh
 */
class MainActivity : BaseActivity() {

    lateinit var viewModel: MainViewModel

    private lateinit var drawerToggle: ActionBarDrawerToggle

    private lateinit var navView: NavigationView

    private val handler: LoginHandler = LoginHandler(this)

    private var exception: Exception? = null

    private lateinit var fab: FloatingActionButton
    private lateinit var fabLayout: LinearLayout
    private lateinit var fabMenu: FabMenu

    private var invitationCodeExpirationTime = 1

    val loggedInUser
        get() = viewModel.getUser()

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        fab = findViewById(R.id.fab)
        fabLayout = findViewById(R.id.fabMenuLayout)
        fabMenu = FabMenu(this, fab, fabLayout).apply {
            addItem(R.string.create_group, R.drawable.ic_baseline_group_add_24)
            addItem(R.string.join_group, R.drawable.ic_baseline_group_24)
            setOnItemClickListener { i, fabMenuItem ->
                fabMenu.close()
                when (i) {
                    0 -> {
                        (getCurrentlyDisplayedFragment() as? GroupViewFragment)?.addWorkGroup()
                    }
                    1 -> {
                        (getCurrentlyDisplayedFragment() as? GroupViewFragment)?.joinWorkGroup()
                    }
                }
            }
        }
        fabMenu.create()
        fabMenu.setFabOnClickListener { view ->
            when (val currentFragment = getCurrentlyDisplayedFragment()) {
                is SettingsFragment  -> {
                    // Do nothing
                }
                is TodoEditFragment  -> {
                    // Add action
                    currentFragment.viewModel.entryId.value?.let {
                        addComment(it, currentFragment)
                    }
                }
                is TodoViewFragment  -> {
                }
                is GroupViewFragment -> {
                    if (!fabMenu.isShowing) {
                        fabMenu.show()
                    }
                }
                is GroupUserFragment -> {
                    AlertDialog.Builder(this).apply {
                        val rootView =
                            layoutInflater.inflate(R.layout.share_workgroup_dialog, null)?.apply {
                                val expirationTimeText =
                                    findViewById<TextView>(R.id.expirationTimeText)
                                findViewById<SeekBar>(R.id.expirationTimeSeekBar)?.apply {
                                    val values: IntArray =
                                        context.resources.getIntArray(R.array.expiration_time_values)
                                    max = values.size - 1
                                    setOnSeekBarChangeListener(object :
                                        SeekBar.OnSeekBarChangeListener {
                                        @SuppressLint("SetTextI18n")
                                        override fun onProgressChanged(
                                            seekBar: SeekBar?,
                                            progress: Int,
                                            fromUser: Boolean
                                        ) {
                                            invitationCodeExpirationTime = values[progress]
                                            val quantifier =
                                                if (invitationCodeExpirationTime <= 1) getString(R.string.day) else getString(
                                                    R.string.days
                                                )
                                            expirationTimeText.text =
                                                "$invitationCodeExpirationTime$quantifier"
                                        }

                                        override fun onStartTrackingTouch(seekBar: SeekBar?) {
                                        }

                                        override fun onStopTrackingTouch(seekBar: SeekBar?) {
                                        }

                                    })
                                }
                            }
                        setView(rootView)
                        setPositiveButton(R.string.confirm) { dialog, _ ->
                            val intent = Intent(context, WorkGroupShareActivity::class.java).apply {
                                putExtra(
                                    "workGroup",
                                    currentFragment.viewModel.workGroup.value
                                )
                                putExtra("loggedInUser", loggedInUser)
                                putExtra("expirationTimeInDays", invitationCodeExpirationTime)
                            }
                            startActivity(intent)
                            dialog.dismiss()
                        }
                        setNegativeButton(R.string.cancel) { dialog, _ -> dialog.cancel() }
                    }.create().show()
                }
            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        viewModel.exception.observe(this) {
            it?.run {
                Toast.makeText(
                    this@MainActivity,
                    getString(R.string.error_occurred) + it.getLocalizedString(this@MainActivity),
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.exception.value = null
            }
        }

        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_group_view -> {
                    if (mainProgressBar.visibility == View.VISIBLE) {
                        return@setNavigationItemSelectedListener false
                    }
                    selectGroup()
                    true
                }
                R.id.nav_logout     -> {
                    drawerLayout.closeDrawer(navView)
                    logoutAndRestart()
                    false
                }
                R.id.nav_settings   -> {
                    supportFragmentManager.beginTransaction().apply {
                        val targetFragment = SettingsFragment.newInstance()
                        replace(
                            R.id.main_fragment_parent,
                            targetFragment,
                            targetFragment::class.simpleName
                        )
                        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        addToBackStack(null)
                        commit()
                    }
                    drawerLayout.closeDrawer(navView)
                    false
                }
                else                -> {
                    false
                }
            }
        }

        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        drawerToggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.open_drawer,
            R.string.close_drawer
        )

        drawerLayout.addDrawerListener(drawerToggle)

        // Restore instance state
        if (savedInstanceState?.containsKey("User") == true) {
            viewModel.setUser(savedInstanceState.getSerializable("User") as? LocalUser)
        }
        attemptLoginWithLocalCache()
    }

    private fun addComment(entryId: UUID, fragment: TodoEditFragment) {
        AlertDialog.Builder(this).apply {
            @SuppressLint("InflateParams")
            val viewRoot = layoutInflater.inflate(R.layout.add_comment_layout, null)
            val commentText = viewRoot.findViewById<EditText>(R.id.addCommentText)
            setView(viewRoot)
            setPositiveButton(R.string.confirm) { dialog, _ ->
                Thread {
                    try {
                        val userId = loggedInUser?.uuid ?: return@Thread
                        val request = okHttpRequest("/action")
                            .post(
                                ActionCrudRequest(
                                    operation = CrudType.CREATE,
                                    userId = userId,
                                    entity = ActionGeneric(
                                        actionType = ActionType.COMMENT,
                                        stringExtra = commentText.text.toString()
                                    ),
                                    entryId = entryId
                                ).toJsonRequest()
                            )
                            .build()
                        val response = AMTTD.okHttpClient.newCall(request).execute()
                        val body = response.body?.string()
                        // Don't simplify this
                        val result: ActionCrudResponse =
                            gson.fromJson(body, object : TypeToken<ActionCrudResponse>() {}.type)
                        if (result.success != true) {
                            throw AmttdException.getFromErrorCode(result.error)
                        }
                        fragment.viewModel.dirty.postValue(true)
                    } catch (e: Exception) {
                        Log.e(AMTTD.logTag, e.stackTraceToString())
                        fragment.viewModel.exception.postValue(AmttdException.getFromException(e))
                    }
                }.start()
                dialog.cancel()
            }
            setNegativeButton(R.string.cancel) { dialog, _ -> dialog.cancel() }
        }.create().show()
    }

    private fun selectGroup() {
        val fragmentManager = supportFragmentManager
        val targetFragment = GroupViewFragment.newInstance()
        fragmentManager.beginTransaction()
            .replace(
                R.id.main_fragment_parent,
                targetFragment,
                targetFragment::class.simpleName
            )
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
        navView.menu.findItem(R.id.nav_group_view).isChecked = true
    }

    fun getCurrentlyDisplayedFragment(): Fragment? {
        val todoEdit =
            supportFragmentManager.findFragmentByTag(TodoEditFragment::class.simpleName) as? TodoEditFragment
        val todoView =
            supportFragmentManager.findFragmentByTag(TodoViewFragment::class.simpleName) as? TodoViewFragment
        val groupView =
            supportFragmentManager.findFragmentByTag(GroupViewFragment::class.simpleName) as? GroupViewFragment
        val groupUser =
            supportFragmentManager.findFragmentByTag(GroupUserFragment::class.simpleName) as? GroupUserFragment
        val settings =
            supportFragmentManager.findFragmentByTag(SettingsFragment::class.simpleName) as? SettingsFragment
        return settings?.takeIf { it.isVisible }
            ?: todoEdit?.takeIf { it.isVisible }
            ?: groupUser?.takeIf { it.isVisible }
            ?: todoView?.takeIf { it.isVisible }
            ?: groupView?.takeIf { it.isVisible }
    }

    @SuppressLint("ApplySharedPref")
    private fun logoutAndRestart() {
        Log.i(AMTTD.logTag, "Restarting!")
        val prefs: SharedPreferences =
            getSharedPreferences("user_data", MODE_PRIVATE)
        prefs.edit().clear().commit()
        doRestart(this)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        drawerToggle.syncState()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putSerializable("User", viewModel.getUser())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                ResultCode.CODE_LOGIN.code -> {
                    viewModel.setUser(
                        data?.getSerializableExtra(PACKAGE_NAME_DOT + "User") as? LocalUser
                    )
                    cacheUserLoginInfo()
                    updateSidebarHeader()
                    selectGroup()
                }
            }
        }
    }

    private fun onFragmentChanged() {
        when (getCurrentlyDisplayedFragment()) {
            is SettingsFragment  -> {
                navView.menu.findItem(R.id.nav_group_view).isChecked = false
                navView.menu.findItem(R.id.nav_settings).isChecked = true
                fab.hide()
                fabMenu.close()

            }
            is TodoEditFragment  -> {
                navView.menu.findItem(R.id.nav_group_view).isChecked = true
                navView.menu.findItem(R.id.nav_settings).isChecked = false
                if (!fab.isVisible) {
                    fab.show()
                }
                fabMenu.close()
                fab.setImageResource(R.drawable.ic_baseline_add_comment_24)

            }
            is GroupUserFragment -> {
                navView.menu.findItem(R.id.nav_group_view).isChecked = true
                navView.menu.findItem(R.id.nav_settings).isChecked = false
                if (!fab.isVisible) {
                    fab.show()
                }
                fabMenu.close()
                fab.setImageResource(R.drawable.ic_baseline_share_24)
            }
            is TodoViewFragment  -> {
                navView.menu.findItem(R.id.nav_group_view).isChecked = true
                navView.menu.findItem(R.id.nav_settings).isChecked = false
                if (!fab.isVisible) {
                    fab.show()
                }
                fabMenu.close()
                fab.setImageResource(R.drawable.ic_baseline_add_24)
            }
            is GroupViewFragment -> {
                navView.menu.findItem(R.id.nav_group_view).isChecked = true
                navView.menu.findItem(R.id.nav_settings).isChecked = false
                if (!fab.isVisible) {
                    fab.show()
                }
                fabMenu.close()
                fab.setImageResource(R.drawable.ic_baseline_add_24)

            }
        }
    }

    fun onFragmentStart(fragment: Fragment) {
        onFragmentChanged()
    }

    fun onFragmentStop(fragment: Fragment) {
        onFragmentChanged()
    }

    private fun cacheUserLoginInfo() {
        val prefs: SharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE)
        prefs.edit().apply {
            putString("authToken", viewModel.getUser()?.authToken)
            putString("userUUID", viewModel.getUser()?.uuid?.toString())
            putString("userName", viewModel.getUser()?.username)
            apply()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        return false
    }

    private fun attemptLoginWithLocalCache() {
        val prefs: SharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE)
        val token = prefs.getString("authToken", null)
        val uuidString = prefs.getString("userUUID", null)
        val uuid: UUID? = try {
            UUID.fromString(uuidString ?: "")
        } catch (e: IllegalArgumentException) {
            null
        }
        if (token != null && uuid != null) {
            loginWithToken(uuid, token)
        } else {
            handler.sendMessage(Message().apply {
                what = LOGIN_FAILED
            })
        }
    }

    private fun loginWithToken(uuid: UUID, authToken: String) {
        mainProgressBar?.visibility = View.VISIBLE
        Thread {
            when (val result =
                viewModel.loginRepository.value?.loginWithAuthToken(uuid, authToken)) {
                is LoginResult.Success -> {
                    viewModel.setUser(result.data)
                    handler.sendMessage(Message().apply {
                        what = LOGIN_SUCCESS
                    })
                }
                is LoginResult.Error   -> {
                    exception = AmttdException.getFromErrorCode(result.errorCode)
                    Log.w(AMTTD.logTag, "Login with token failed with exception: ", exception)
                    handler.sendMessage(Message().apply {
                        what = LOGIN_FAILED
                    })
                }
            }
        }.start()
    }

    private fun updateSidebarHeader() {
        val headerView = findViewById<NavigationView>(R.id.nav_view).getHeaderView(0)
        val user = viewModel.getUser()
        if (user != null) {
            headerView.findViewById<TextView>(R.id.headerUsername).apply {
                text = user.username
            }
            headerView.findViewById<TextView>(R.id.headerSubtitle).apply {
                text = user.email
            }
        }
    }

    class LoginHandler(activity: MainActivity) : Handler(Looper.getMainLooper()) {
        private val activityRef: WeakReference<MainActivity> = WeakReference(activity)
        private val activity: MainActivity?
            get() = activityRef.get()

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                LOGIN_SUCCESS -> {
                    activity?.selectGroup()
                    activity?.updateSidebarHeader()
                }
                LOGIN_FAILED  -> {
                    val loginIntent = Intent(activity, LoginActivity::class.java)
                    activity?.startActivityForResult(loginIntent, ResultCode.CODE_LOGIN.code)
                    if (activity?.exception != null) {
                        Toast.makeText(
                            activity,
                            AmttdException.getFromException(activity?.exception)
                                .getLocalizedString(activity),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            activity?.mainProgressBar?.visibility = View.GONE
        }
    }

    override fun onBackPressed() {
        if (getCurrentlyDisplayedFragment() is GroupViewFragment) {
            if (System.currentTimeMillis() - lastBackPressed <= 3000) {
                ActivityManager.finishAll()
            } else {
                Toast.makeText(this, R.string.press_back_again, Toast.LENGTH_SHORT).show()
                lastBackPressed = System.currentTimeMillis()
            }
        } else {
            super.onBackPressed()
        }
    }

    private var lastBackPressed: Long = 0

    companion object {
        const val LOGIN_SUCCESS = 0
        const val LOGIN_FAILED = 1
    }
}