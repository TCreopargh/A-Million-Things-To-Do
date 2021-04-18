package xyz.tcreopargh.amttd

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import xyz.tcreopargh.amttd.data.login.LoginResult
import xyz.tcreopargh.amttd.ui.group.GroupViewFragment
import xyz.tcreopargh.amttd.ui.login.LoginActivity
import xyz.tcreopargh.amttd.user.LocalUser
import xyz.tcreopargh.amttd.util.CODE_LOGIN
import xyz.tcreopargh.amttd.util.PACKAGE_NAME_DOT
import xyz.tcreopargh.amttd.util.doRestart
import java.util.*


/**
 * @author TCreopargh
 */
class MainActivity : BaseActivity() {

    private lateinit var viewModel: MainViewModel

    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var drawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_group_view -> {
                    Log.i(AMTTD.logTag, "Selected group view")
                    val fragmentManager = supportFragmentManager
                    fragmentManager.beginTransaction()
                        .replace(R.id.main_fragment_parent, GroupViewFragment::class.java, null)
                        .addToBackStack(null)
                        .commit()
                    true
                }
                R.id.nav_logout     -> {
                    drawerLayout.closeDrawer(navView)
                    logoutAndRestart()
                    false
                }
                else            -> {
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
            viewModel.setUser(savedInstanceState.getParcelable("User") as? LocalUser)
        }
        attemptLoginWithLocalCache()
        if (viewModel.getUser() == null) {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivityForResult(loginIntent, CODE_LOGIN)
        }
        navView.menu.findItem(R.id.nav_group_view).isChecked = true
    }

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
        outState.putParcelable("User", viewModel.getUser())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                CODE_LOGIN -> {
                    viewModel.setUser(
                        data?.getParcelableExtra(PACKAGE_NAME_DOT + "User") as? LocalUser
                    )
                    cacheUserLoginInfo()
                    updateSidebarHeader()
                }
            }
        }
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
        menuInflater.inflate(R.menu.main, menu)
        return true
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
            val result = viewModel.loginRepo.value?.loginWithAuthToken(uuid, token)
            if (result is LoginResult.Success) {
                viewModel.setUser(result.data)
                updateSidebarHeader()
            }
        }
    }

    private fun updateSidebarHeader() {
        val headerView = findViewById<NavigationView>(R.id.nav_view).getHeaderView(0)
        val user = viewModel.getUser()
        if (user != null) {
            headerView.findViewById<TextView>(R.id.headerUsername).apply {
                text = user.username
            }
            headerView.findViewById<TextView>(R.id.headerSubtitle).apply {
                text = user.uuid.toString()
            }
        }
    }
}