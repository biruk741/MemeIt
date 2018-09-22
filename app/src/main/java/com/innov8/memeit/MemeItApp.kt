package com.innov8.memeit

import android.app.Application
import android.preference.PreferenceManager
import com.cloudinary.android.MediaManager
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.core.ImagePipelineConfig
import com.memeit.backend.MemeItClient

class MemeItApp : Application() {
    companion object {
        lateinit var instanxe:Application
        private const val LOCAL_SERVER_URL = "http://127.0.0.1:5000/api/"
        private const val SERVER_URL = "https://safe-beyond-33046.herokuapp.com/api/"

    }

    override fun onCreate() {
        super.onCreate()
        instanxe=this
        MemeItClient.init(applicationContext, SERVER_URL)
        val config = mapOf(
                "cloud_name" to "innov8",
                "api_key" to "591249199742556",
                "api_secret" to "yT2mxv0vQrEWjzsPrmyD6xu5a-Y"
        )
        MediaManager.init(this, config)
        val configf = ImagePipelineConfig.newBuilder(this)
                .setResizeAndRotateEnabledForNetwork(true)
                .setDownsampleEnabled(true)
                .build()
        Fresco.initialize(this, configf)
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)
    }
}