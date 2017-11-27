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

import com.google.gson.annotations.Expose;

import java.io.Serializable;

public class Record implements Serializable {

    private static final long serialVersionUID = -5597677381752175585L;

    // when this event is happened;
    @Expose
    public long when;

    //cost of this record, in ms
    @Expose
    public long cost;

    // content of this event, might be null;
    @Expose
    public String content;

    // target, same as Message.target
    @Expose
    public String target;

    // what, same as Message.what
    @Expose
    public int what;

    // callback, same as Message.callback
    @Expose
    public String callback;
}
