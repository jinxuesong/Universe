package com.stephenue.universe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;

import android.Manifest;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.stephenue.universe.Activity.SearchActivity;
import com.stephenue.universe.Adapter.MainViewPagerAdapter;
import com.stephenue.universe.db.DatabaseHelper;
import com.stephenue.universe.fragment.HomeFragment;
import com.stephenue.universe.fragment.NotesFragment;
import com.stephenue.universe.fragment.SortFragment;
import com.stephenue.universe.utils.SharedPerfHelper;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnTouchListener, ViewPager.OnPageChangeListener, BottomNavigationView.OnNavigationItemSelectedListener {

    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 127;
    @BindView(R.id.main_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.main_viewPager)
    ViewPager mMainViewPager;
    @BindView(R.id.main_bottom)
    BottomNavigationView mBottomNavigationView;
    @BindView(R.id.drawerLayout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.naviView)
    NavigationView mNavigationView;

    private MainViewPagerAdapter mMainViewPagerAdapter;
    private MenuItem menuItem;
    private final static int SEND_CAMERA_REQUEST_CODE = 18;
    private final int REQUEST_CODE_SCAN = 1001;
    DatabaseHelper dbHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化本地数据库
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext(), "test_note");
        db = dbHelper.getWritableDatabase();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public void initData() {
        List<Fragment> fragmentArrayList = getFragment();
        mMainViewPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager(), fragmentArrayList);
        mBottomNavigationView.setOnNavigationItemSelectedListener(this);
        mMainViewPager.addOnPageChangeListener(this);
        mMainViewPager.setAdapter(mMainViewPagerAdapter);
        mMainViewPager.setOnTouchListener(this);

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setCheckable (false);
                if (item.getItemId() == R.id.scan) {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 0x12);
                        return true;
                    }
                    Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                    startActivityForResult(intent,REQUEST_CODE_SCAN);
                } else if (item.getItemId() == R.id.me) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("id",1);
                    contentValues.put("title","aaa");
                    contentValues.put("content","bbb");
                    contentValues.put("time","2019");
                    db.insert("note", null, contentValues);
                } else if (item.getItemId() == R.id.setting) {
                    //创建游标对象
                    Cursor cursor = db.query("note", new String[]{"time"}, null, null, null, null, null);
                    //利用游标遍历所有数据对象
                    //为了显示全部，把所有对象连接起来，放到TextView中
                    String textview_data = "";
                    while(cursor.moveToNext()){
                        String content = cursor.getString(cursor.getColumnIndex("time"));
                        textview_data = textview_data + "\n" + content;
                    }
                    Toast.makeText(getApplicationContext(),textview_data,Toast.LENGTH_LONG).show();
                } else if (item.getItemId() == R.id.collect) {

                } else if (item.getItemId() == R.id.about) {

                } else if (item.getItemId() == R.id.color_app) {

                }
                return true;
            }
        });

        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true); //让导航按钮显示出来
            actionBar.setHomeAsUpIndicator(R.mipmap.toolbar_menu);//设置导航按钮图标

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_toolbar,menu);
        getMenuInflater().inflate(R.menu.menu_toolbar_search_view, menu);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SEND_CAMERA_REQUEST_CODE) {             //相机权限
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(this, "获取相机权限失败", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 扫描二维码/条码回传
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {
                //key值都约束好了
                String content = data.getStringExtra(Constant.CODED_CONTENT);
                Toast.makeText(getApplicationContext(),content,Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.menu_search_view:
                Intent intent=new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
//                Intent intent = new Intent();
//                intent.setAction("com.stephenue.universe.abcdefg");
//                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void initView() {
        mToolbar.setTitle("Universe");
    }

    @Override
    public void setUpComponent() {

    }

    @Override
    public int getLayoutID() {
        return R.layout.activity_main;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (menuItem != null) {
            menuItem.setChecked(false);
        } else {
            mBottomNavigationView.getMenu().getItem(0).setChecked(false);
        }
        menuItem = mBottomNavigationView.getMenu().getItem(position);
        menuItem.setChecked(true);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.news:
                mMainViewPager.setCurrentItem(0);
                break;
            case R.id.class_pic:
                mMainViewPager.setCurrentItem(1);
                break;
            case R.id.mine:
                mMainViewPager.setCurrentItem(2);
                break;
        }
        return false;
    }

    public List<Fragment> getFragment() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new HomeFragment());
        fragments.add(new SortFragment());
        fragments.add(new NotesFragment());
        return fragments;
    }
}
