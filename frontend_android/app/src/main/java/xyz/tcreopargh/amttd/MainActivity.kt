package xyz.tcreopargh.amttd

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import xyz.tcreopargh.amttd.data.login.LoginDataSource
import xyz.tcreopargh.amttd.data.login.LoginRepository
import xyz.tcreopargh.amttd.data.login.LoginResult
import xyz.tcreopargh.amttd.ui.login.LoginActivity
import xyz.tcreopargh.amttd.user.LocalUser
import xyz.tcreopargh.amttd.util.CODE_LOGIN
import xyz.tcreopargh.amttd.util.PACKAGE_NAME_DOT
import java.util.*

/**
 * @author TCreopargh
 */
class MainActivity : AppCompatActivity() {

    private lateinit var loginRepo: LoginRepository

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ActivityManager.addActivity(this)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        loginRepo = LoginRepository(LoginDataSource())

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Restore instance state
        if (savedInstanceState?.containsKey("User") == true) {
            loginRepo.loggedInUser = savedInstanceState.getParcelable("User") as? LocalUser
        }
        attemptLoginWithLocalCache()
        if (loginRepo.loggedInUser == null) {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivityForResult(loginIntent, CODE_LOGIN)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityManager.removeActivity(this)
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putParcelable("User", loginRepo.loggedInUser)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                CODE_LOGIN -> {
                    loginRepo.loggedInUser =
                        data?.getParcelableExtra(PACKAGE_NAME_DOT + "User") as? LocalUser
                    cacheUserLoginInfo()
                    updateSidebarHeader()
                }
            }
        }
    }

    private fun cacheUserLoginInfo() {
        val prefs: SharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE)
        prefs.edit().apply {
            putString("authToken", loginRepo.loggedInUser?.authToken)
            putString("userUUID", loginRepo.loggedInUser?.uuid?.toString())
            putString("userName", loginRepo.loggedInUser?.userName)
            apply()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
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
            val result = loginRepo.loginWithAuthToken(uuid, token)
            if (result is LoginResult.Success) {
                this.loginRepo.loggedInUser = result.data
                updateSidebarHeader()
            }
        }
    }

    private fun updateSidebarHeader() {
        val headerView = findViewById<NavigationView>(R.id.nav_view).getHeaderView(0)
        val user = loginRepo.loggedInUser
        if (user != null) {
            headerView.findViewById<TextView>(R.id.headerUsername).apply {
                text = user.userName
            }
            headerView.findViewById<TextView>(R.id.headerSubtitle).apply {
                text = user.uuid.toString()
            }
        }
    }
}