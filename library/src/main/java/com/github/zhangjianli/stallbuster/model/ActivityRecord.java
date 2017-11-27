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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.List;

public class ActivityRecord implements Serializable {

    private static final long serialVersionUID = -988883054994949159L;

    // when this activity started
    @Expose
    public long when;
    // name of the activity started.
    @Expose
    public String activity_name;
    // name of the source from which this activity is started.
    // this may not ne an activity, a service or other context. w give the name when possible
    @Expose
    public String source;
    // time cost for this activity to start. in ms.
    @Expose
    public long cost;
    // records that record the detail in main thread.
    @Expose
    public List<Record> records;

    // file name of the record, don't do transfer
    public String file_name;

    public String toJsonString() {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(this);
    }

    public static ActivityRecord fromJsonString(String s) {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.fromJson(s, ActivityRecord.class);
    }

}
