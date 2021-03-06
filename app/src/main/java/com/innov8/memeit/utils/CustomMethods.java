package com.innov8.memeit.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ImageView;

import com.innov8.memeit.R;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;

/*
 * Created by Biruk on 5/11/2018.
 */

public class CustomMethods {

    // Set window content to draw behind the status and navigation bars.
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void setFullScreen(Activity a) {
        a.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
    }

    /**
     * method is used for checking valid email id format.
     *
     * @param email
     * @return boolean true for valid false for invalid
     */
    public static boolean isEmailValid(String email) {
        if(TextUtils.isEmpty(email))return false;
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    public static boolean isEmailValidOptional(String email) {
        if(TextUtils.isEmpty(email))return true;
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean isUsernameValid(String username) {
        //todo check if user name is valid (it should exclude such characters)
        return username != null && username.length() >= 1;//todo change back to 5
    }

    public static Bitmap getBitmapFromUri(Uri imageUri, Context c) {
        final InputStream imageStream;
        Bitmap selectedImage = BitmapFactory.decodeResource(c.getResources(), R.drawable.filled_button);
        try {
            imageStream = c.getContentResolver().openInputStream(imageUri);
            selectedImage = BitmapFactory.decodeStream(imageStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return selectedImage;
    }

    /**
     * This method makes the activity draw its content behind the status bar giving more space
     *
     * @param a
     */
    public static void makeActivityDrawBehindStatusBar(Activity a) {
        a.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            a.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * This method gives the login activity that scrolling effect
     *
     * @param first,second : The consecutive imageviews that follow each other and loop
     *                     to addColorView an animation.
     */

    public static void makeBackgroundScrollAnimate(Activity a, @IdRes int first, @IdRes int second) {
        final ImageView backgroundOne = (ImageView) a.findViewById(first);
        final ImageView backgroundTwo = (ImageView) a.findViewById(second);

        final ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(20000L);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float progress = (float) animation.getAnimatedValue();
                final float width = backgroundOne.getWidth();
                final float translationX = width * progress;
                backgroundOne.setTranslationX(translationX);
                backgroundTwo.setTranslationX(translationX - width);
            }
        });
        animator.start();
    }

    public static void makeWindowSeamless(Activity a) {
        a.requestWindowFeature(Window.FEATURE_NO_TITLE);
        a.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        a.getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = a.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setNavigationBarColor(Color.parseColor("#F84C09"));
            Log.w("change","changed");
        }
    }

    public static void makeWindowTransparent(Activity a) {
        a.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            a.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * This method gives the login activity that scrolling effect
     *
     * @param edittexts : The edittexts that need to be font-ized.
     */
    public static void makeEditTextsAvenir(Activity a, View v, int... edittexts) {
        final String asset = "fonts/avenir.ttf";
        for (int res : edittexts) {
            ((EditText) v.findViewById(res))
                    .setTypeface(
                            Typeface.createFromAsset(a.getAssets(), asset)
                    );
        }
    }

    public static float convertDPtoPX(Context context, float dp) {
        return dp * context.getResources().getDisplayMetrics().density;

    }

    public static float convertPXtoDP(Context context, float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }


    public static String formatNumber(int num) {
        if (num < 1000) {
            return String.valueOf(num);
        } else {
            float d = num / 1000.0f;
            return String.format("%.2fk", d);
        }
    }

    public static String formatNumber(int num, String suffix,String suffixPlural) {
        if (num >= 1000000) {
            float d = num / 1000000.0f;
            return String.format("%.2fm %s", d, suffixPlural);
        } else if(num>=1000){
            float d = num / 1000.0f;
            return String.format("%.2fk %s", d, suffixPlural);
        }else if(num>1){
            return String.format("%d %s", num, suffixPlural);
        }else if(num==1){
            return String.format("%d %s", num, suffix);
        }else{
            return String.format("no %s", num, suffix);
       }

    }

    public static boolean deviceIsConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)
                        context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean b = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        Log.w("Internet", b + "");
        return b;
    }

    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static void toggleVisibilityAndAnimate(final View view) {
        view.animate().setDuration(200).alpha(view.getVisibility() == View.VISIBLE ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(view.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
            }
        });
    }

    public static String[] toArray(@NonNull List<String> list) {
        String sA[] = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            sA[i] = list.get(i);
        }
        return sA;
    }


    public static void changeImageBackgroundWithAnim(Context c, final ImageView v, final Bitmap new_image) {
        final Animation anim_out = AnimationUtils.loadAnimation(c, android.R.anim.fade_out);
        final Animation anim_in  = AnimationUtils.loadAnimation(c, android.R.anim.fade_in);
        anim_out.setAnimationListener(new Animation.AnimationListener()
        {
            @Override public void onAnimationStart(Animation animation) {}
            @Override public void onAnimationRepeat(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                v.setImageBitmap(new_image);
                anim_in.setAnimationListener(new Animation.AnimationListener() {
                    @Override public void onAnimationStart(Animation animation) {}
                    @Override public void onAnimationRepeat(Animation animation) {}
                    @Override public void onAnimationEnd(Animation animation) {}
                });
                v.startAnimation(anim_in);
            }
        });
        v.startAnimation(anim_out);
    }
}


