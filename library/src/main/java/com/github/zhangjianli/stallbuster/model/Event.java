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
package com.github.zhangjianli.stallbuster.model;

import android.support.v4.util.Pools;

public class Event {

    private static final Pools.SynchronizedPool<Event> sPool = new Pools.SynchronizedPool<>(50);

    // time stamp when event is happened;
    public long when;
    // content of the event;
    public String content;
    // same as message what;
    public int what;
    // type, only for lifecycle type;
    public int type;

    public static Event obtain(int what, String content) {
        Event e = obtain();
        e.what = what;
        e.content = content;
        return e;
    }

    public static Event obtain(int what, String content, int type) {
        Event e = obtain();
        e.what = what;
        e.type = type;
        e.content = content;
        return e;
    }

    public static Event obtain() {
        Event event = sPool.acquire();
        if (event == null) {
            event = new Event();
        }
        event.when = System.currentTimeMillis();
        return event;
    }

    public void recycle() {
        // Clear state if needed.
        when = 0;
        content = null;
        type = 0;
        what = 0;
        try {
            sPool.release(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Event clone(Event src) {
        Event event = new Event();
        event.what = src.what;
        event.when = src.when;
        event.content = src.content;
        event.type = src.type;
        return event;
    }

}
