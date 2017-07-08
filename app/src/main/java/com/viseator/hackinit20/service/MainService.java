package com.viseator.hackinit20.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.viseator.hackinit20.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by viseator on 7/8/17.
 *
 * @author Wu Di
 *         Email: viseator@gmail.com
 */

public class MainService extends Service implements View.OnTouchListener {
    private static final String TAG = "@vir MainService";
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;
    private int initX = 0;
    private int initY = 0;
    private int lastX = 0;
    private int lastY = 0;
    private boolean isDragging = false;
    View mContentView;
    @BindView(R.id.test)
    ImageView mImageView;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mContentView = LayoutInflater.from(getApplicationContext()).inflate(R.layout
                .bubble_layout, null, false);
        ButterKnife.bind(this, mContentView);
        mLayoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        mLayoutParams.gravity = Gravity.TOP | Gravity.START;

        mContentView.setOnTouchListener(this);

        mWindowManager.addView(mContentView, mLayoutParams);
        mImageView.setImageDrawable(getDrawable(R.drawable.star));
        Log.d(TAG, String.valueOf("add View"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mContentView != null) {
            mWindowManager.removeView(mContentView);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int rawX = (int) event.getRawX();
        int rawY = (int) event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isDragging = true;
                initX = rawX;
                initY = rawY;
                lastX = rawX;
                lastY = rawY;
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isDragging) {
                    break;
                }
                mLayoutParams.x = (mLayoutParams.x + rawX - lastX);
                mLayoutParams.y = (mLayoutParams.y + rawY - lastY);
                mWindowManager.updateViewLayout(mContentView, mLayoutParams);
                lastX = rawX;
                lastY = rawY;
                break;
        }
        return true;
    }

}
