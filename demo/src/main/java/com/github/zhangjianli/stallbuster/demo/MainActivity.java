/*
 * Copyright (C) 2017 ZhangJianli
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.zhangjianli.stallbuster.demo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private CheckBox mStallCb;

    private Handler mStallHandler = new StallHandler(this);

    private static class StallHandler extends Handler {

        WeakReference<MainActivity> mActivityRef;

        public StallHandler(MainActivity activity) {
            mActivityRef = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mActivityRef.get();
            if (activity == null) {
                return;
            }
            if (activity.isStalling()) {
                // do stalling
                sendEmptyMessageDelayed(0, 100);
                try {
                    int time = Utils.rollDice(50, 100);
                    Thread.sleep(time);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                removeCallbacksAndMessages(null);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_start_subactivity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setClass(MainActivity.this, SubActivity.class);
                startActivity(i);
            }
        });

        mStallCb = (CheckBox) findViewById(R.id.stalling_cb);
        mStallCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (mStallCb.isChecked()) {
                    mStallCb.setText(R.string.stalling_enabled);
                    mStallHandler.sendEmptyMessage(0);
                } else {
                    mStallCb.setText(R.string.stalling_disabled);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //showFloater();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mStallCb.setChecked(false);
        mStallHandler.removeCallbacksAndMessages(null);
    }

    public boolean isStalling () {
        return mStallCb.isChecked();
    }

    boolean isShown = false;

    private void showFloater() {

        if (isShown) {
            return;
        }

        isShown = true;

        TextView floater = new TextView(this);
        floater.setBackgroundColor(Color.BLACK);
        floater.setText(TAG);
        floater.setTextColor(Color.WHITE);

        floater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, TAG, Toast.LENGTH_LONG).show();
            }
        });

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        WindowManager.LayoutParams lph = new WindowManager.LayoutParams();
        lph.type = WindowManager.LayoutParams.TYPE_TOAST;
        lph.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        lph.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        lph.flags |= WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
        lph.width = WindowManager.LayoutParams.MATCH_PARENT;
        lph.height = 100;
        lph.format = PixelFormat.RGBA_8888;
        lph.gravity = Gravity.TOP;

        wm.addView(floater, lph);
    }

}
