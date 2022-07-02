package com.example.socialmediaintegration

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.socialmediaintegration.databinding.ActivityFbDetailsBinding
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.GraphResponse
import com.facebook.login.LoginManager
import org.json.JSONException
import org.json.JSONObject


class Fb_detailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFbDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFbDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        var accessToken = AccessToken.getCurrentAccessToken()

        // request for details
        val request = GraphRequest.newMeRequest(accessToken,
            object : GraphRequest.GraphJSONObjectCallback {
                override fun onCompleted(
                    `object`: JSONObject?,
                    response: GraphResponse?
                ) {
                    try {
                        var name: String? = `object`?.getString("name")
                        var email: String? = `object`?.optString("email")
                        binding.tvName.text = name
                        binding.tvEmail.text = email
                        var url = `object`?.getJSONObject("picture")?.getJSONObject("data")
                            ?.getString("url")
                        Glide.with(this@Fb_detailsActivity)
                            .load(url)
                            .into(binding.ivUserPhoto)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                }
            })

        val parameters = Bundle()
        parameters.putString("fields", "id,name,link,picture.type(large),email")
        request.parameters = parameters
        request.executeAsync()

        binding.btnLogout.setOnClickListener {
            LoginManager.getInstance().logOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}