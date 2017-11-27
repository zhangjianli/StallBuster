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

import android.util.Log;

public class Config {

    /**
     * threshold that decides whether the activity took to much time to start.  in ms
     */
    protected int threshold = 200;

    /**
     * threshold that decides whether one callback should be shown. in ms
     */
    protected int callback_threshold = 10;

    /**
     * threshold that decides whether one callback took to much time to execute. in ms
     */
    protected int callback_warning_threshold = 100;

    /**
     * max records count, when records count exceeds this limit, old ones will be deleted.
     */
    protected int max_records_count = 50;

    /**
     * whether to print log
     */
    protected boolean should_print_log = true;

    /**
     * Log Tag
     */
    protected String log_tag = StallBuster.class.getSimpleName();

    /**
     * Log Level, must be set according to android.util.Log
     */
    protected int log_level = Log.VERBOSE;

    /**
     * Whether print messages of main thread
     */
    protected boolean print_message = false;

    /**
     * Hook Instrumentation
     */
    protected boolean hook_instrumentation = true;

    /**
     * Whether has custom message logging. when
     */
    protected boolean has_custom_message_logger = false;

    protected Config() {

    }
}
