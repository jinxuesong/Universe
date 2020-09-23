package com.stephenue.universe.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.stephenue.universe.MainActivity;

public class SharedPerfHelper {
    private static final String P_NAME = "eos_sysytem_config_persistent_cache";
    private static SharedPreferences sp;
    private static SharedPerfHelper ph;

    private SharedPerfHelper() {
    }

    public static void deleteData(Context context, String name) {
        if (ph == null || sp == null) {
            ph = new SharedPerfHelper();
            sp = context.getSharedPreferences("eos_sysytem_config_persistent_cache", 0);
        }

        SharedPreferences var1 = sp;
        synchronized (sp) {
            Editor editor = sp.edit();
            editor.remove(name);
            editor.commit();
        }
    }

    public static SharedPerfHelper getPerferences(Context a) {
        if (ph == null || sp == null) {
            ph = new SharedPerfHelper();
            sp = a.getSharedPreferences("eos_sysytem_config_persistent_cache", 0);
        }

        return ph;
    }

    public static SharedPerfHelper getPerferences() {
        return ph;
    }

    public static void setInfo(Context context, String name, String data) {
        if (ph == null || sp == null) {
            ph = new SharedPerfHelper();
            sp = context.getSharedPreferences("eos_sysytem_config_persistent_cache", 0);
        }

        SharedPreferences var2 = sp;
        synchronized (sp) {
            Editor e = sp.edit().putString(name, data);
            e.commit();
        }
    }

    public static void setInfo(Context context, String name, int data) {
        if (ph == null || sp == null) {
            ph = new SharedPerfHelper();
            sp = context.getSharedPreferences("eos_sysytem_config_persistent_cache", 0);
        }

        SharedPreferences var2 = sp;
        synchronized (sp) {
            Editor e = sp.edit().putInt(name, data);
            e.commit();
        }
    }

    public static void setInfo(Context context, String name, boolean data) {
        if (ph == null || sp == null) {
            ph = new SharedPerfHelper();
            sp = context.getSharedPreferences("eos_sysytem_config_persistent_cache", 0);
        }

        SharedPreferences var2 = sp;
        synchronized (sp) {
            Editor e = sp.edit().putBoolean(name, data);
            e.commit();
        }
    }

    public static int getIntData(Context context, String name) {
        if (ph == null || sp == null) {
            ph = new SharedPerfHelper();
            sp = context.getSharedPreferences("eos_sysytem_config_persistent_cache", 0);
        }

        return sp.getInt(name, 0);
    }

    public static String getStringData(Context context, String name) {
        if (ph == null || sp == null) {
            ph = new SharedPerfHelper();
            sp = context.getSharedPreferences("eos_sysytem_config_persistent_cache", 0);
        }

        return sp.getString(name, "");
    }

    public static boolean getBooleanData(Context context, String name) {
        if (ph == null || sp == null) {
            ph = new SharedPerfHelper();
            sp = context.getSharedPreferences("eos_sysytem_config_persistent_cache", 0);
        }

        return sp.getBoolean(name, false);
    }

    public static void setInfo(Context context, String name, long data) {
        if (ph == null || sp == null) {
            ph = new SharedPerfHelper();
            sp = context.getSharedPreferences("eos_sysytem_config_persistent_cache", 0);
        }

        SharedPreferences var3 = sp;
        synchronized (sp) {
            Editor e = sp.edit().putLong(name, data);
            e.commit();
        }
    }

    public static long getLongData(Context context, String name) {
        if (ph == null || sp == null) {
            ph = new SharedPerfHelper();
            sp = context.getSharedPreferences("eos_sysytem_config_persistent_cache", 0);
        }

        return sp.getLong(name, 0L);
    }
}
