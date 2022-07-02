package com.example.socialmediaintegration

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.socialmediaintegration.databinding.ActivityMainBinding
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import java.util.*


class MainActivity : AppCompatActivity() {
    lateinit var callbackManager: CallbackManager
    private lateinit var binding: ActivityMainBinding

    lateinit var googleSignInOptions: GoogleSignInOptions
    lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )


        val windowInsetsController =
            ViewCompat.getWindowInsetsController(window.decorView) ?: return
        // Configure the behavior of the hidden system bars
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        // Hide both the status bar and the navigation bar
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())


        callbackManager = CallbackManager.Factory.create()


        var accessToken = AccessToken.getCurrentAccessToken()
        // if already login in Facebook then directly open details Activity
        if (accessToken != null && !accessToken.isExpired) {
            startActivity(Intent(this@MainActivity, Fb_detailsActivity::class.java))
            finish()
        }

        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null) {
            navigateToGoogleProfileDetails()
        }

        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {

                override fun onSuccess(result: LoginResult) {
                    startActivity(Intent(this@MainActivity, Fb_detailsActivity::class.java))
                    finish()
                }

                override fun onCancel() {
                    Toast.makeText(this@MainActivity, "Oops, You just cancelled login process", Toast.LENGTH_SHORT).show()
                }

                override fun onError(exception: FacebookException) {
                    Toast.makeText(this@MainActivity, exception.toString(), Toast.LENGTH_SHORT).show()
                }

            })

        // Login from Facebook
        binding.fbLoginButton.setOnClickListener {
            LoginManager.getInstance()
                .logInWithReadPermissions(this, listOf("email", "public_profile"))
        }


        googleSignInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        // Login from Google
        binding.googleLoginButton.setOnClickListener {
            signIn()
        }

    }

    private fun signIn() {
        var signIntIntent: Intent = googleSignInClient.signInIntent
        startActivityForResult(signIntIntent, 1000)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1000) {
            var task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                task.getResult(ApiException::class.java)
                navigateToGoogleProfileDetails()
            } catch (e: ApiException) {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun navigateToGoogleProfileDetails() {
        startActivity(Intent(this, GoogleDetailsActivity::class.java))
        finish()
    }

}