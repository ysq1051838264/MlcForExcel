package com.hisino.ScreenRotation;

import java.io.File;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.content.pm.ActivityInfo;
//import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.IBinder;
import android.view.View;
import android.view.WindowManager;
//import android.view.WindowManager.LayoutParams;

public class ScreenRotation extends Service {
    private MyReciver receiver;
    private View view;
    private boolean view_update = false;
    private WindowManager wm;
    private WindowManager.LayoutParams params;
    private boolean flag = false;

    private int g_pid = -1;
    private boolean stopThread = false;

    private final IBinder binder = new MyBinder();
    public class MyBinder extends Binder {
        ScreenRotation getService() {
            return ScreenRotation.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    //private static final boolean DISPLAY = true;
    @Override
    public void onCreate() {
        view = new View(this);
        view_update = false;
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        g_pid = -1;

        int dimension = 0;
        int pixelFormat = PixelFormat.TRANSLUCENT;

        params = new WindowManager.LayoutParams(
                dimension, dimension,
                WindowManager.LayoutParams.TYPE_TOAST,//TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                pixelFormat);


	    /* broadcast stub */
        receiver=new MyReciver();
        IntentFilter filter=new IntentFilter();
        filter.addAction("com.hisino.ScreenRotation");
        registerReceiver(receiver, filter);

        new Thread(new MyThread()).start();
        stopThread = false;

    }

    void rotation(boolean landscape)
    {
		/*
	    int dimension = 0;
	    int pixelFormat = PixelFormat.TRANSLUCENT;

	    if (display == true) {
	        view.setBackgroundColor(Color.argb(128, 255, 0, 0));
	        dimension = LayoutParams.MATCH_PARENT;
	        pixelFormat = PixelFormat.RGBA_8888;
	    }

	    final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
	            dimension, dimension,
	            WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
	            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
	                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
	            pixelFormat);
	    */

        if( g_pid < 0 ) {
            return;
        }

        flag = landscape;
        if( landscape ) {
            params.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        }
        else {
            params.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        }

        if( view_update ) {
            wm.updateViewLayout(view, params);
        }
        else {
            wm.addView(view, params);
            view_update = true;
        }
    }

    @Override
    public void onDestroy() {
        stopThread = true;
        rotation(false);
        if( view_update ) {
            wm.removeView(view);
        }
        //unregisterReceiver(detachReceiver);
        super.onDestroy(); // ÂèØ‰ª•‰∏çÁî®
    }

    private class MyReciver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            int ret = intent.getIntExtra("do", -1);
            if( ret == 0 ) {
                rotation(false);
            }
            else if( ret == 1 ) {
                rotation(true);
            }
            else if( ret == 2 ) {
                rotation(!flag);
            }
            else if( ret == -2 ) {
                if( view_update ) {
                    wm.removeView(view);
                    view_update = false;
                }
            }
            else {
                int pid = intent.getIntExtra("pid", -1);
                if( pid > 0 ) {
                    g_pid = pid;
                }
            }

        }
    }

    public class MyThread implements Runnable {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            while (stopThread == false) {
                try {
                    Thread.sleep(500);// 500 ms
                    if( g_pid > 0) {
                        String file_name = String.format("/proc/%s", g_pid);
                        if( fileIsExists(file_name ) == false ) {
                            g_pid = -1;
                            if( view_update ) {
                                wm.removeView(view);
                                view_update = false;
                                //∑¢ÀÕπ„≤•
                                Intent intent=new Intent();
                                intent.putExtra("do", -2);
                                intent.setAction("com.hisino.ScreenRotation");
                                sendBroadcast(intent);
                            }
                            continue;
                        }
                    }
                    else {
                        Thread.sleep(1000);// 1000 ms
                    }

                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean fileIsExists(String strFile)
    {
        try
        {
            File f=new File(strFile);
            if(!f.exists())
            {
                return false;
            }
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }
}

