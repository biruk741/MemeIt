package com.innov8.memeit;
import android.app.Application;
import android.preference.PreferenceManager;

import com.cloudinary.android.MediaManager;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.decoder.SimpleProgressiveJpegConfig;
import com.memeit.backend.MemeItClient;

import java.util.HashMap;
import java.util.Map;

public class MemeItApp extends Application{
    private static final String LOCAL_SERVER_URL="http://127.0.0.1:5000/api/";
    private static final String SERVER_URL="https://safe-beyond-33046.herokuapp.com/api/";
    @Override
    public void onCreate() {
        super.onCreate();
        MemeItClient.init(getApplicationContext(),LOCAL_SERVER_URL);
        Map config = new HashMap();
        config.put("cloud_name", "innov8");
        config.put("api_key", "591249199742556");
        config.put("api_secret", "yT2mxv0vQrEWjzsPrmyD6xu5a-Y");
        MediaManager.init(this, config);
        ImagePipelineConfig configf= ImagePipelineConfig.newBuilder(this)
                .setProgressiveJpegConfig(new SimpleProgressiveJpegConfig())
                .setResizeAndRotateEnabledForNetwork(true)
                .setDownsampleEnabled(true)
                .build();
        Fresco.initialize(this,configf);
        PreferenceManager.setDefaultValues(this,R.xml.preferences,false);
    }
}
