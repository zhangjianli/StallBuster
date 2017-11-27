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
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;

import java.lang.reflect.Method;

public class InstrumentationProxy extends Instrumentation {

    public static final String TAG = InstrumentationProxy.class.getSimpleName();
    public static final String EXEC_START_ACTIVITY = "execStartActivity";

    public Instrumentation originalInstrumentation;

    public InstrumentationProxy(Instrumentation instrumentation, Handler handler) {
        originalInstrumentation = instrumentation;
    }

    public ActivityResult execStartActivity(Context who, IBinder contextThread, IBinder token, Activity target, Intent intent, int requestCode, Bundle options) {
        ComponentName cn = intent.getComponent();
        String activity_info = null;
        if (cn != null) {
            activity_info = cn.getClassName();
        }
        if (TextUtils.isEmpty(activity_info)) {
            activity_info = intent.getAction();
        }
        StallBuster.getInstance().sendStartActivityEvent(activity_info);

        try {
            Method execStartActivity = Instrumentation.class.getDeclaredMethod(
                    EXEC_START_ACTIVITY,
                    Context.class, IBinder.class, IBinder.class, Activity.class,
                    Intent.class, int.class, Bundle.class);
            execStartActivity.setAccessible(true);
            return (ActivityResult) execStartActivity.invoke(originalInstrumentation, who,
                    contextThread, token, target, intent, requestCode, options);
        } catch (Exception e) {
            throw new RuntimeException("hook failed");
        }
    }
}
