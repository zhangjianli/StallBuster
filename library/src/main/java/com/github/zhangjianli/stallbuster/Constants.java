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

import android.graphics.Color;
import android.util.SparseArray;

public final class Constants {

    public static final int MSG_START_ACTIVITY = 1;
    public static final int MSG_LIFECYCLE = 2;
    public static final int MSG_MESSAGE = 3;

    public static final int LIFECYCLE_CREATED = 1;
    public static final int LIFECYCLE_STARTED = 2;
    public static final int LIFECYCLE_RESUMED = 3;
    public static final int LIFECYCLE_PAUSED = 4;
    public static final int LIFECYCLE_STOPPED = 5;
    public static final int LIFECYCLE_DESTROYED = 6;
    public static final int LIFECYCLE_STATE_SAVED = 7;

    public static final int COLOR_NORMAL = Color.parseColor("#27C47A");
    public static final int COLOR_WARNING = Color.parseColor("#ED4337");
    public static final int COLOR_INFO= Color.parseColor("#222222");

    public static final SparseArray<String> sActivityThreadMsgMap = new SparseArray<>();

    static {
        sActivityThreadMsgMap.put(100, "LAUNCH_ACTIVITY");
        sActivityThreadMsgMap.put(101, "PAUSE_ACTIVITY");
        sActivityThreadMsgMap.put(102, "PAUSE_ACTIVITY_FINISHING");
        sActivityThreadMsgMap.put(103, "STOP_ACTIVITY_SHOW");
        sActivityThreadMsgMap.put(104, "STOP_ACTIVITY_HIDE");
        sActivityThreadMsgMap.put(105, "SHOW_WINDOW");
        sActivityThreadMsgMap.put(106, "HIDE_WINDOW");
        sActivityThreadMsgMap.put(107, "RESUME_ACTIVITY");
        sActivityThreadMsgMap.put(108, "SEND_RESULT");
        sActivityThreadMsgMap.put(109, "DESTROY_ACTIVITY");
        sActivityThreadMsgMap.put(110, "BIND_APPLICATION");
        sActivityThreadMsgMap.put(111, "EXIT_APPLICATION");
        sActivityThreadMsgMap.put(112, "NEW_INTENT");
        sActivityThreadMsgMap.put(113, "RECEIVER");
        sActivityThreadMsgMap.put(114, "CREATE_SERVICE");
        sActivityThreadMsgMap.put(115, "SERVICE_ARGS");
        sActivityThreadMsgMap.put(116, "STOP_SERVICE");
        sActivityThreadMsgMap.put(118, "CONFIGURATION_CHANGED");
        sActivityThreadMsgMap.put(119, "CLEAN_UP_CONTEXT");
        sActivityThreadMsgMap.put(120, "GC_WHEN_IDLE");
        sActivityThreadMsgMap.put(121, "BIND_SERVICE");
        sActivityThreadMsgMap.put(122, "UNBIND_SERVICE");
        sActivityThreadMsgMap.put(123, "DUMP_SERVICE");
        sActivityThreadMsgMap.put(124, "LOW_MEMORY");
        sActivityThreadMsgMap.put(125, "ACTIVITY_CONFIGURATION_CHANGED");
        sActivityThreadMsgMap.put(126, "RELAUNCH_ACTIVITY");
        sActivityThreadMsgMap.put(127, "PROFILER_CONTROL");
        sActivityThreadMsgMap.put(128, "CREATE_BACKUP_AGENT");
        sActivityThreadMsgMap.put(129, "DESTROY_BACKUP_AGENT");
        sActivityThreadMsgMap.put(130, "SUICIDE");
        sActivityThreadMsgMap.put(131, "REMOVE_PROVIDER");
        sActivityThreadMsgMap.put(132, "ENABLE_JIT");
        sActivityThreadMsgMap.put(133, "DISPATCH_PACKAGE_BROADCAST");
        sActivityThreadMsgMap.put(134, "SCHEDULE_CRASH");
        sActivityThreadMsgMap.put(135, "DUMP_HEAP");
        sActivityThreadMsgMap.put(136, "DUMP_ACTIVITY");
        sActivityThreadMsgMap.put(137, "SLEEPING");
        sActivityThreadMsgMap.put(138, "SET_CORE_SETTINGS");
        sActivityThreadMsgMap.put(139, "UPDATE_PACKAGE_COMPATIBILITY_INFO");
        sActivityThreadMsgMap.put(140, "TRIM_MEMORY");
        sActivityThreadMsgMap.put(141, "DUMP_PROVIDER");
        sActivityThreadMsgMap.put(142, "UNSTABLE_PROVIDER_DIED");
        sActivityThreadMsgMap.put(143, "REQUEST_ASSIST_CONTEXT_EXTRAS");
        sActivityThreadMsgMap.put(144, "TRANSLUCENT_CONVERSION_COMPLETE");
        sActivityThreadMsgMap.put(145, "INSTALL_PROVIDER");
        sActivityThreadMsgMap.put(146, "ON_NEW_ACTIVITY_OPTIONS");
        sActivityThreadMsgMap.put(147, "CANCEL_VISIBLE_BEHIND");
        sActivityThreadMsgMap.put(148, "BACKGROUND_VISIBLE_BEHIND_CHANGED");
        sActivityThreadMsgMap.put(149, "ENTER_ANIMATION_COMPLETE");
        sActivityThreadMsgMap.put(150, "START_BINDER_TRACKING");
        sActivityThreadMsgMap.put(151, "STOP_BINDER_TRACKING_AND_DUMP");
        sActivityThreadMsgMap.put(152, "MULTI_WINDOW_MODE_CHANGED");
        sActivityThreadMsgMap.put(153, "PICTURE_IN_PICTURE_MODE_CHANGED");
        sActivityThreadMsgMap.put(154, "LOCAL_VOICE_INTERACTION_STARTED");
        sActivityThreadMsgMap.put(155, "ATTACH_AGENT");
        sActivityThreadMsgMap.put(156, "APPLICATION_INFO_CHANGED");
        sActivityThreadMsgMap.put(157, "ACTIVITY_MOVED_TO_DISPLAY");

    }

    public static final SparseArray<String> sViewRootImplMsgMap = new SparseArray<>();

    static {
        sViewRootImplMsgMap.put(1, "MSG_INVALIDATE");
        sViewRootImplMsgMap.put(2, "MSG_INVALIDATE_RECT");
        sViewRootImplMsgMap.put(3, "MSG_DIE");
        sViewRootImplMsgMap.put(4, "MSG_RESIZED");
        sViewRootImplMsgMap.put(5, "MSG_RESIZED_REPORT");
        sViewRootImplMsgMap.put(6, "MSG_WINDOW_FOCUS_CHANGED");
        sViewRootImplMsgMap.put(7, "MSG_DISPATCH_INPUT_EVENT");
        sViewRootImplMsgMap.put(8, "MSG_DISPATCH_APP_VISIBILITY");
        sViewRootImplMsgMap.put(9, "MSG_DISPATCH_GET_NEW_SURFACE");
        sViewRootImplMsgMap.put(11, "MSG_DISPATCH_KEY_FROM_IME");
        sViewRootImplMsgMap.put(12, "MSG_FINISH_INPUT_CONNECTION");
        sViewRootImplMsgMap.put(13, "MSG_CHECK_FOCUS");
        sViewRootImplMsgMap.put(14, "MSG_CLOSE_SYSTEM_DIALOGS");
        sViewRootImplMsgMap.put(15, "MSG_DISPATCH_DRAG_EVENT");
        sViewRootImplMsgMap.put(16, "MSG_DISPATCH_DRAG_LOCATION_EVENT");
        sViewRootImplMsgMap.put(17, "MSG_DISPATCH_SYSTEM_UI_VISIBILITY");
        sViewRootImplMsgMap.put(18, "MSG_UPDATE_CONFIGURATION");
        sViewRootImplMsgMap.put(19, "MSG_PROCESS_INPUT_EVENTS");
        sViewRootImplMsgMap.put(21, "MSG_CLEAR_ACCESSIBILITY_FOCUS_HOST");
        sViewRootImplMsgMap.put(22, "MSG_INVALIDATE_WORLD");
        sViewRootImplMsgMap.put(23, "MSG_WINDOW_MOVED");
        sViewRootImplMsgMap.put(24, "MSG_SYNTHESIZE_INPUT_EVENT");
        sViewRootImplMsgMap.put(25, "MSG_DISPATCH_WINDOW_SHOWN");
        sViewRootImplMsgMap.put(26, "MSG_DISPATCH_WINDOW_ANIMATION_STOPPED");
        sViewRootImplMsgMap.put(27, "MSG_DISPATCH_WINDOW_ANIMATION_STARTED");
    }

    private Constants() {
    }
}
