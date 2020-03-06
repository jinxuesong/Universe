package com.stephenue.universe.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import butterknife.BindView;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.stephenue.universe.BaseActivity;
import com.stephenue.universe.MainActivity;
import com.stephenue.universe.R;
import com.stephenue.universe.utils.OKManager;

public class SearchActivity extends BaseActivity implements View.OnClickListener, OKManager.IProgress{

    @BindView(R.id.search_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.progress)
    ProgressBar mProgressBar;
    @BindView(R.id.progress2)
    ProgressBar mProgressBar2;
    @BindView(R.id.startbbb)
    Button mStartButton;
    @BindView(R.id.stopaaa)
    Button mStopButton;
    @BindView(R.id.startnew)
    Button newStartButton;
    @BindView(R.id.stopnew)
    Button newStopButton;

    OKManager download;
    OKManager download1;
    private String path = "https://download.alicdn.com/wireless/taobao4android/latest/702757.apk";
    private String path_mp4 = "http://q6arc2seg.bkt.clouddn.com/2019_%E5%A4%A7%E6%BA%AA%E5%9C%B0%E8%AF%BA%E4%B8%BD%EF%BC%88%E4%B8%AD%E5%9B%BD%EF%BC%89%E9%A3%8E%E4%BA%91%E7%9B%9B%E4%BC%9A%E5%9B%9E%E9%A1%BE%E5%BD%B1%E7%89%87.mp4";
    private Vibrator vibrator;
    WindowManager.LayoutParams lps ;
    float now;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lps = getWindow().getAttributes();
        now = getAppBrightness(getApplicationContext());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(SearchActivity.this, MainActivity.class);
//                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void initData() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);//左侧添加一个默认的返回图标
            getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        }

        //设置返回键的监听
//        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });

        //沉浸式，状态栏与toolbar同色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
            window.setStatusBarColor(getApplication().getResources().getColor(R.color.colorPrimary));//强行设置状态栏颜色也toolbar一致
        }

        download = new OKManager(path,this);
        download1 = new OKManager(path_mp4, this);
    }

    /**
     * 获取当前页面亮度
     * @return
     */
    public float getAppBrightness(Context context)
    {
        float brightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
        if(context instanceof Activity)
        {
            Window window = ((Activity)context).getWindow();
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            brightness = layoutParams.screenBrightness;
        }
        if(brightness == WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE)
        {
            brightness = getSystemBrightness() / 255f;
        }
        return brightness;
    }

    /**
     * 获取系统亮度
     */
    public int getSystemBrightness()
    {
        ContentResolver resolver = getApplicationContext().getContentResolver();
        int a = 0;
        try {
            a = Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return a;
    }

    /**
     * 获取屏幕的亮度
     */
    public static int getScreenBrightness(Context context) {
        int nowBrightnessValue = 0;
        ContentResolver resolver = context.getContentResolver();
        try {
            nowBrightnessValue = android.provider.Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nowBrightnessValue;
    }
    /**
     * 设置当前Activity显示时的亮度
     * 屏幕亮度最大数值一般为255，各款手机有所不同
     * screenBrightness 的取值范围在[0,1]之间
     */
    public static void setBrightness(Activity activity, int brightness) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.screenBrightness = Float.valueOf(brightness) /** (1f / 255f)*/;
        activity.getWindow().setAttributes(lp);
    }

    public void setnewBrightness(float brightness) {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                // if (lp.screenBrightness <= 0.1) {
                // return;
                // }
//        System.out.println(lp.screenBrightness);
                if (lp.screenBrightness==-1) {
                    lp.screenBrightness = (float) 0.5;
                    System.out.println(lp.screenBrightness);
                }
                lp.screenBrightness = lp.screenBrightness + brightness / 255.0f;
                if (lp.screenBrightness > 1) {
                        lp.screenBrightness = 1;
//                        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                        long[] pattern = { 10, 200 }; // OFF/ON/OFF/ON...
//                        vibrator.vibrate(pattern, -1);
                    } else if (lp.screenBrightness < 0.1) {
                        lp.screenBrightness = (float) 0.1;
//                        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                        long[] pattern = { 10, 200 }; // OFF/ON/OFF/ON...
//                        vibrator.vibrate(pattern, -1);
                    }
                Log.e("LIGHT", "lp.screenBrightness= " + lp.screenBrightness);
                getWindow().setAttributes(lp);
            }

    public void saveBrightness(Context context, int brightness) {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
                        Uri.parse("package:" + context.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } else {
                //有了权限，你要做什么呢？具体的动作
                ContentResolver resolver = context.getContentResolver();
                Uri uri = android.provider.Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS);
                android.provider.Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS, brightness);
                resolver.notifyChange(uri, null);
            }
        }
    }

    @Override
    public void initView() {
        mProgressBar.setMax(100);
        mProgressBar2.setMax(100);
        mStartButton.setOnClickListener(this);
        mStopButton.setOnClickListener(this);
        newStartButton.setOnClickListener(this);
        newStopButton.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.startbbb:
                download.start();

//                setnewBrightness(20);
                break;
            case R.id.stopaaa:
//                setnewBrightness(-20);
                download.stop();
                break;
            case R.id.startnew:
                download = new OKManager(path_mp4, this);
                download.start();
//                float a = getSystemBrightness();
//                System.out.println(a);
//                setBrightness(this,a);
//                saveBrightness(getApplicationContext(),a);
                break;
            case R.id.stopnew:
                download = new OKManager(path_mp4, this);
                download.stop();
                break;
        }
    }

    @Override
    public void setUpComponent() {

    }

    @Override
    public int getLayoutID() {
//        return R.layout.activity_search;
        return R.layout.toolbar_search_in_bottom;
    }


    @Override
    public void onProgress(int progress) {
        mProgressBar.setProgress(progress);
        mProgressBar2.setProgress(progress);
    }
}
