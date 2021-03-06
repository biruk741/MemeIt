package com.innov8.memeit.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceActivity
import android.preference.PreferenceManager
import com.afollestad.materialdialogs.MaterialDialog
import com.innov8.memeit.R
import com.memeit.backend.MemeItClient
import com.memeit.backend.models.MyUser

class SettingsActivity : PreferenceActivity() {


    lateinit var user: MyUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user = MemeItClient.myUser!!
        addPreferencesFromResource(R.xml.main_pref)
        preferenceScreen.findPreference(PREF_KEY_LOGOUT).setOnPreferenceClickListener {
            MaterialDialog.Builder(this)
                    .title("Are you sure you want to logout?")
                    .content("All ongoing uploads will be cancelled")
                    .positiveText("Logout")
                    .negativeText("Cancel")
                    .onPositive { _, _ ->
                        MemeItClient.Auth.signOut(this)
                        startActivity(Intent(this, AuthActivity::class.java))
                        finish()
                    }.show()

            true
        }
        preferenceScreen.findPreference(PREF_KEY_USERNAME).setOnPreferenceClickListener {
            startActivity(Intent(this, UsernameSettingsActivity::class.java))
            true
        }

        preferenceScreen.findPreference(PREF_KEY_PASSWORD).setOnPreferenceClickListener {
            startActivity(Intent(this, PasswordSettingsActivity::class.java))
            true
        }

        preferenceScreen.findPreference(PREF_KEY_PROFILE).setOnPreferenceClickListener {
            startActivity(Intent(this, ProfileSettingsActivity::class.java))
            true
        }
    }

    companion object {
        private const val PREF_KEY_IMAGE_QUALITY = "pref_image_quality"
        private const val PREF_KEY_ENABLE_NOTIF = "pref_enable_notif"
        private const val PREF_KEY_ENABLE_NOTIF_FOLLOWED = "pref_enable_notif_followed"
        private const val PREF_KEY_ENABLE_NOTIF_REACTION = "pref_enable_notif_reaction"
        private const val PREF_KEY_ENABLE_NOTIF_COMMENT = "pref_enable_notif_comment"
        private const val PREF_KEY_LOGOUT = "pref_logout"
        private const val PREF_KEY_USERNAME = "pref_username"
        private const val PREF_KEY_PASSWORD = "pref_password"
        private const val PREF_KEY_PROFILE = "pref_profile"
        private const val PREF_KEY_AUTO_LOAD_GIFS = "pref_load_gif"
        val quality = listOf(10, 25, 50, 75, 100)
        val factor = listOf(.4f, .6f, .8f, .9f, 1f)

        fun getImageQualityLevel(context: Context): Int {
            val pref = PreferenceManager.getDefaultSharedPreferences(context)
            val values = context.resources.getStringArray(R.array.pref_image_quality)
            val value = pref.getString(PREF_KEY_IMAGE_QUALITY, "")
            return values.indexOf(value)
        }


        fun isNotifEnabled(context: Context): Boolean {
            val pref = PreferenceManager.getDefaultSharedPreferences(context)
            return pref.getBoolean(PREF_KEY_ENABLE_NOTIF, true)
        }

        fun autoLoadGifs(context: Context): Boolean {
            val pref = PreferenceManager.getDefaultSharedPreferences(context)
            return pref.getBoolean(PREF_KEY_AUTO_LOAD_GIFS, true)
        }

        fun isFollowedNotifEnabled(context: Context): Boolean {
            val pref = PreferenceManager.getDefaultSharedPreferences(context)
            return pref.getBoolean(PREF_KEY_ENABLE_NOTIF_FOLLOWED, true)
        }

        fun isReactionNotifEnabled(context: Context): Boolean {
            val pref = PreferenceManager.getDefaultSharedPreferences(context)
            return pref.getBoolean(PREF_KEY_ENABLE_NOTIF_REACTION, true)
        }

        fun isCommentNotifEnabled(context: Context): Boolean {
            val pref = PreferenceManager.getDefaultSharedPreferences(context)
            return pref.getBoolean(PREF_KEY_ENABLE_NOTIF_COMMENT, true)
        }
    }
}

