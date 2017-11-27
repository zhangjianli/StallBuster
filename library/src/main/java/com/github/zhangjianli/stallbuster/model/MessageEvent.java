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

public class MessageEvent {

    public static final String DISPATCH = ">";
    public static final String FINISH = "<";
    public static final int DISPATCH_HEAD_INDEX = 29; // >>>>> Dispatching to Handler
    public static final int FINISH_HEAD_INDEX = 26; // <<<<< Finished to Handler


    public long when;
    public String target;
    public String callback;
    // same as Message.what
    public int what;

    /**
     *
     * @param event
     * @param event_pair
     * @return true, has a message finished
     */
    public static boolean breakDown(Event event, MessageEvent[] event_pair) {
        String content = event.content;
        if (content.startsWith(DISPATCH)) {
            MessageEvent me = event_pair[0];
            String dispatch = content.substring(DISPATCH_HEAD_INDEX);
            String[] fragment = dispatch.split(" ");
            me.target = fragment[0];
            int index = fragment[2].indexOf("@");
            if (index > 0) {
                me.callback = fragment[2].substring(0, index);
            } else {
                me.callback = fragment[2];
            }
            me.what = Integer.parseInt(fragment[3]);
            me.when = event.when;
        } else if (content.startsWith(FINISH)) {
            MessageEvent me = event_pair[1];
            String finish = content.substring(FINISH_HEAD_INDEX);
            String[] fragment = finish.split(" ");
            me.target = fragment[0];
            int index = fragment[2].indexOf("@");
            if (index > 0) {
                me.callback = fragment[2].substring(0, index);
            } else {
                me.callback = fragment[2];
            }
            me.when = event.when;
            if (event_pair[0].target != null) {
                return true;
            }

        }
        return false;
    }
}
