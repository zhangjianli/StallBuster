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
package com.github.zhangjianli.stallbuster;

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Printer;

import com.github.zhangjianli.stallbuster.model.Event;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class StallBuster {

    private static final String TAG = StallBuster.class.getSimpleName();

    private static StallBuster sInstance = new StallBuster();

    private HandlerThread mHandlerThread;
    private StallBusterHandler mHandler;

    private HandlerThread mFileHandlerThread;
    private Handler mFileHandler;

    private Application mApplication;

    protected Config mConfig = new Config();

    /**
     * Acquire the singleton of StallBuster.
     * @return the singleton of StallBuster.
     */
    public static StallBuster getInstance() {
        return sInstance;
    }

    private StallBuster() {

    }

    /**
     * init StallBuster
     * @param application of the app
     */
    public void init(Application application) {
        // start a handler thread.
        mHandlerThread = new HandlerThread(TAG);
        mHandlerThread.start();
        mHandler = new StallBusterHandler(mHandlerThread.getLooper(), new EventProcessor());

        // handler that handles access to record storage
        mFileHandlerThread = new HandlerThread("file_thread");
        mFileHandlerThread.start();
        mFileHandler = new Handler(mFileHandlerThread.getLooper());

        // hook startActivity. StallBuster needs to hook Instrumentation in order to work properly.
        // if your app also hooked Instrumentation. You must set Config.hook_instrumentation to false and
        // call StallBuster.getInstance().sendStartActivityEvent() in when start an activity in your own Instrumentation.
        if (mConfig.hook_instrumentation) {
            hookStartActivity();
        }
        // register Activity lifecycle callback
        mApplication = application;
        mApplication.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {
                Logger.log("onActivityCreated " + activity.getClass().getSimpleName());
                Event event = Event.obtain(Constants.MSG_LIFECYCLE, activity.getClass().getSimpleName(), Constants.LIFECYCLE_CREATED);
                Message msg = mHandler.obtainMessage(Constants.MSG_LIFECYCLE, event);
                mHandler.sendMessage(msg);
            }

            @Override
            public void onActivityStarted(Activity activity) {
                Logger.log("onActivityStarted " + activity.getClass().getSimpleName());
                Event event = Event.obtain(Constants.MSG_LIFECYCLE, activity.getClass().getSimpleName(), Constants.LIFECYCLE_STARTED);
                Message msg = mHandler.obtainMessage(Constants.MSG_LIFECYCLE, event);
                mHandler.sendMessage(msg);
            }

            @Override
            public void onActivityResumed(Activity activity) {
                Logger.log("onActivityResumed " + activity.getClass().getSimpleName());
                Event event = Event.obtain(Constants.MSG_LIFECYCLE, activity.getClass().getSimpleName(), Constants.LIFECYCLE_RESUMED);
                Message msg = mHandler.obtainMessage(Constants.MSG_LIFECYCLE, event);
                mHandler.sendMessage(msg);
            }

            @Override
            public void onActivityPaused(Activity activity) {
                Logger.log("onActivityPaused " + activity.getClass().getSimpleName());
                Event event = Event.obtain(Constants.MSG_LIFECYCLE, activity.getClass().getSimpleName(), Constants.LIFECYCLE_PAUSED);
                Message msg = mHandler.obtainMessage(Constants.MSG_LIFECYCLE, event);
                mHandler.sendMessage(msg);
            }

            @Override
            public void onActivityStopped(Activity activity) {
                Logger.log("onActivityStopped " + activity.getClass().getSimpleName());
                Event event = Event.obtain(Constants.MSG_LIFECYCLE, activity.getClass().getSimpleName(), Constants.LIFECYCLE_STOPPED);
                Message msg = mHandler.obtainMessage(Constants.MSG_LIFECYCLE, event);
                mHandler.sendMessage(msg);
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
                Logger.log("onActivitySaveInstanceState " + activity.getClass().getSimpleName());
                Event event = Event.obtain(Constants.MSG_LIFECYCLE, activity.getClass().getSimpleName(), Constants.LIFECYCLE_STATE_SAVED);
                Message msg = mHandler.obtainMessage(Constants.MSG_LIFECYCLE, event);
                mHandler.sendMessage(msg);
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                Logger.log("onActivityDestroyed " + activity.getClass().getSimpleName());
                Event event = Event.obtain(Constants.MSG_LIFECYCLE, activity.getClass().getSimpleName(), Constants.LIFECYCLE_DESTROYED);
                Message msg = mHandler.obtainMessage(Constants.MSG_LIFECYCLE, event);
                mHandler.sendMessage(msg);
            }
        });

        // setup log printer for main thread
        // if your app also called  Looper.getMainLooper().setMessageLogging. this will conflict with StallBuster. You need to add set Config.has_custom_message_logger to true and then add StallBuster.getInstance().sendMessageEvent() in your own MessageLogging
        //
        if (!mConfig.has_custom_message_logger) {
            Looper.getMainLooper().setMessageLogging(new Printer() {
                @Override
                public void println(String s) {
                    // key point of StallBuster. s should be like one of below two lines, represents beginning or finishing of a msg executed in main thread.
                    // ">>>>> Dispatching to Handler (android.view.Choreographer$FrameHandler) {1a8bcaf} android.view.Choreographer$FrameDisplayEventReceiver@cafd8bc: 0"
                    // "<<<<< Finished to Handler (android.view.Choreographer$FrameHandler) {1a8bcaf} android.view.Choreographer$FrameDisplayEventReceiver@cafd8bc"
                    // if your android platform outputs msg different than these two line, StallBuster might not work. If so please contact us.
                    // if you'd like to monitor what's going on in your main thread, uncomment below line of code then checkout you abd logcat output.
                    if (mConfig.print_message) {
                        Logger.log(s);
                    }
                    sendMessageEvent(s);
                }
            });
        }

        // ready to go

    }

    private static class StallBusterHandler extends Handler {

        public StallBusterHandler(Looper looper, Handler.Callback callback) {
            super(looper, callback);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MSG_LIFECYCLE:
                    handleLifeCycle(msg);
                    break;
                case Constants.MSG_START_ACTIVITY:
                    handleStartActivity(msg);
                    break;
                case Constants.MSG_MESSAGE:
                    handleMsg(msg);
                    break;
                default:
                    break;
            }
        }

        private void handleMsg(Message msg) {
            Event event = (Event)msg.obj;
            if (event == null) {
                return;
            }
            event.recycle();
        }

        private void handleStartActivity(Message msg) {
            Event event = (Event)msg.obj;
            if (event == null) {
                return;
            }
            event.recycle();
        }

        private void handleLifeCycle(Message msg) {
            Event event = (Event)msg.obj;
            if (event == null) {
                return;
            }
            event.recycle();
        }
    }

    private void hookStartActivity() {
        try {
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            Method currentActivityThreadMethod = activityThreadClass.getDeclaredMethod("currentActivityThread");
            currentActivityThreadMethod.setAccessible(true);
            Object currentActivityThread = currentActivityThreadMethod.invoke(null);

            Field mInstrumentationField = activityThreadClass.getDeclaredField("mInstrumentation");
            mInstrumentationField.setAccessible(true);
            Instrumentation mInstrumentation = (Instrumentation) mInstrumentationField.get(currentActivityThread);
            Instrumentation myInstrumentation = new InstrumentationProxy(mInstrumentation, mHandler);
            mInstrumentationField.set(currentActivityThread, myInstrumentation);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * send a start activity event to StallBuster. you only need to call this
     * function when you have hooked Instrumentation in your app. Otherwise, please
     * don't call this function.
     * @param activity_name, the name of the activity to be started
     */
    public void sendStartActivityEvent(String activity_name) {
        Event event = Event.obtain(Constants.MSG_START_ACTIVITY, activity_name);
        Message msg = mHandler.obtainMessage(Constants.MSG_START_ACTIVITY, event);
        mHandler.sendMessage(msg);
    }

    /**
     * send a start activity event to StallBuster. please don't call this function unless you have
     * called Looper.getMainLooper().setMessageLogging() and set a custom message logger.
     * @param message the message log from main looper.
     */
    public void sendMessageEvent(String message) {
        Event event = Event.obtain(Constants.MSG_MESSAGE, message);
        Message msg = mHandler.obtainMessage(Constants.MSG_MESSAGE, event);
        mHandler.sendMessage(msg);
    }

    /**
     * a handler handles file operations, please don't call this function in your app.
     * @return
     * @hide
     */
    public Handler getFileHandler() {
        return mFileHandler;
    }

    /**
     * get the Application cached by StallBuster.
     * @return
     */
    public Application getApp() {
        return mApplication;
    }

    /**
     * set the threshold that decides whether start an activity took too much time, in ms.
     * the Report activity will display green or red color when time cost more than or less than
     * this threshold.
     * default is 200ms.
     * @param threshold
     * @return
     */
    public StallBuster setThreshold(int threshold) {
        mConfig.threshold = threshold;
        return this;
    }

    /**
     * set the threshold that decides whether or not it took too little time to handle a message
     * in main thread, in ms. this threshold will affect the messages to be shown in RecordDetailActivity
     * default is 10ms
     * @param callbackThreshold
     * @return
     */
    public StallBuster setCallbackThreshold(int callbackThreshold) {
        mConfig.callback_threshold = callbackThreshold;
        return this;
    }

    /**
     * set the threshold that decides whether or not it took too much time to handle a message
     * in main thread, in ms. this threshold will affect the messages to be shown in RecordDetailActivity
     * default is 100ms
     * @param callbackWarningThreshold
     * @return
     */
    public StallBuster setCallbackWarningThreshold(int callbackWarningThreshold) {
        mConfig.callback_warning_threshold = callbackWarningThreshold;
        return this;
    }

    /**
     * set max records can be saved. If there are more records than this limit. old ones will be deleted.
     * default is 50
     * @param maxRecordsCount
     * @return
     */
    public StallBuster setMaxRecordsCount(int maxRecordsCount) {
        mConfig.max_records_count = maxRecordsCount;
        return this;
    }

    /**
     * set whether StallBuster should print logs in logcat.
     * default is true.
     * @param shouldPrintLog
     * @return
     */
    public StallBuster setShouldPrintLog(boolean shouldPrintLog) {
        mConfig.should_print_log = shouldPrintLog;
        return this;
    }

    /**
     * set the tag for StallBuster to print log.
     * @param logTag
     * @return
     */
    public StallBuster setLogTag(String logTag) {
        mConfig.log_tag = logTag;
        return this;
    }

    /**
     * set the level of the log to be printed, level must between Log.VERBOSE and Log.ASSERT
     * default is Log.VERBOSE
     * @param level
     * @return
     */
    public StallBuster setLogLevel(int level) {
        mConfig.log_level = level;
        return this;
    }

    /**
     * set whether to print the raw message from main looper. it is useful if you are interested
     * what's going on in your main thread.
     * default is false
     * @param printMessage
     * @return
     */
    public StallBuster setPrintMessage(boolean printMessage) {
        mConfig.print_message = printMessage;
        return this;
    }

    /**
     * set whether should StallBuster hook Instrumentation. please don't set this to false unless
     * you know what you are doing.
     * this is only for developers who has also hooked Instrumentation in their app
     * default is false
     * @param hookInstrumentation
     * @return
     */
    public StallBuster setHookInstrumentation(boolean hookInstrumentation) {
        mConfig.hook_instrumentation = hookInstrumentation;
        return this;
    }

    /**
     * set whether should StallBuster call Looper.getMainLooper().setMessageLogging(). set to false
     * only when you have call Looper.getMainLooper().setMessageLogging() by yourself.
     * default is false
     * @param hasCustomMessageLogger
     * @return
     */
    public StallBuster setHasCustomMessageLogger(boolean hasCustomMessageLogger) {
        mConfig.has_custom_message_logger = hasCustomMessageLogger;
        return this;
    }

    /**
     * @hide
     */
    public int getCallbackThreshold() {
        return mConfig.callback_threshold;
    }

    /**
     * @hide
     * @return
     */
    public int getCallbackWarningThreshold() {
        return mConfig.callback_warning_threshold;
    }

    /**
     * @hide
     */
    public int getThreshold() {
        return mConfig.threshold;
    }
}
