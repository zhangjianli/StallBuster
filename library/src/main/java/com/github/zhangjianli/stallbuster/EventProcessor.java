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

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.github.zhangjianli.stallbuster.model.ActivityRecord;
import com.github.zhangjianli.stallbuster.model.Event;
import com.github.zhangjianli.stallbuster.model.MessageEvent;
import com.github.zhangjianli.stallbuster.model.Record;

import java.util.ArrayList;

public class EventProcessor implements Handler.Callback {

    private static final String TAG = EventProcessor.class.getSimpleName();

    private MessageEvent[] mMessageEvents =  {new MessageEvent(), new MessageEvent()};

    private ActivityRecord mActivityRecord;

    public EventProcessor() {

    }

    @Override
    public boolean handleMessage(Message msg) {
        // TODO uncomment below code when release
        /*if (Debug.isDebuggerConnected()) {
            return true;
        }*/
        Event event = (Event) msg.obj;
        if (event != null) {
            try {
                processEvent(event);
            } catch (Exception e) {
                e.printStackTrace();
            }
            event.recycle();
        }
        // return true, so handler won't process this msg again, since we have event recycled here
        return true;
    }

    private void processEvent(Event event) {
        switch (event.what) {
            case Constants.MSG_LIFECYCLE:
                processLifecycle(event);
                break;
            case Constants.MSG_START_ACTIVITY:
                processStartActivity(event);
                break;
            case Constants.MSG_MESSAGE:
                processMessage(event);
                break;
            default:
                break;
        }
    }

    private void processMessage(Event event) {
        if (MessageEvent.breakDown(event, mMessageEvents)) {
            Record record = new Record();
            long time = mMessageEvents[1].when - mMessageEvents[0].when;
            record.when = mMessageEvents[0].when;
            record.what = mMessageEvents[0].what;
            record.target = mMessageEvents[0].target;
            record.cost = time;
            String action;
            if ("null:".equals(mMessageEvents[0].callback)) {
                action = ""+ mMessageEvents[0].what;
            } else {
                action = mMessageEvents[0].callback;
                record.callback = action;
            }
            Logger.log(time+" ms in " +  mMessageEvents[0].target+" with "+ action);
            enlistRecord(record);
            // 8: ViewRootImpl.MSG_DISPATCH_APP_VISIBILITY.
            if (8 == mMessageEvents[0].what && "(android.view.ViewRootImpl$ViewRootHandler)".equals(mMessageEvents[0].target) ) {
                if (mActivityRecord != null) {
                    long cost = mMessageEvents[0].when - mActivityRecord.when;
                    Logger.log("====== " + mActivityRecord.activity_name + " cost " + cost + "ms ======");
                    // An activity's starting process is finished, wrap it up
                    if (mActivityRecord != null) {
                        mActivityRecord.cost = cost;
                        final ActivityRecord activityRecord = mActivityRecord;
                        StallBuster.getInstance().getFileHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                RecordsStore.saveOneRecord(activityRecord);
                            }
                        });
                    }
                    // do some clean up
                    mActivityRecord = null;
                }
            }

        }
    }

    private void processStartActivity(Event event) {
        // if the activity to be started belongs to StallBuster, don't record it.
        if ("com.github.zhangjianli.stallbuster.ui.ReportListActivity".equals(event.content) || "com.github.zhangjianli.stallbuster.ui.ReportDetailActivity".equals(event.content)) {
            return;
        }
        Logger.log("------ Starting "+event.content+" ------");
        mActivityRecord = new ActivityRecord();
        mActivityRecord.when = event.when;
        if (!TextUtils.isEmpty(event.content)) {
            mActivityRecord.activity_name = event.content;
        }
    }

    private void processLifecycle(Event event) {
        switch (event.type) {
            case Constants.LIFECYCLE_CREATED:
                if (mActivityRecord != null && TextUtils.isEmpty(mActivityRecord.activity_name)) {
                    mActivityRecord.activity_name = event.content;
                }
                break;
            case Constants.LIFECYCLE_STARTED:
                break;
            case Constants.LIFECYCLE_RESUMED:
                break;
            case Constants.LIFECYCLE_PAUSED:
                break;
            case Constants.LIFECYCLE_STOPPED:
                break;
            case Constants.LIFECYCLE_DESTROYED:
                break;
            case Constants.LIFECYCLE_STATE_SAVED:
                break;
            default:
                break;
        }
    }

    private void enlistRecord(Record record) {
        if (record == null) {
            return;
        }
        if (mActivityRecord == null) {
            return;
        }
        if (mActivityRecord.records == null) {
            mActivityRecord.records = new ArrayList<>();
        }
        mActivityRecord.records.add(record);
    }
}
