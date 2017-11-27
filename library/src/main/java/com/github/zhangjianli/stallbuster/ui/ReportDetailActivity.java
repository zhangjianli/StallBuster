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
package com.github.zhangjianli.stallbuster.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.github.zhangjianli.R;
import com.github.zhangjianli.stallbuster.Constants;
import com.github.zhangjianli.stallbuster.StallBuster;
import com.github.zhangjianli.stallbuster.model.ActivityRecord;
import com.github.zhangjianli.stallbuster.model.Record;
import com.github.zhangjianli.stallbuster.ui.adapter.SimpleDecoration;
import com.github.zhangjianli.stallbuster.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReportDetailActivity extends AppCompatActivity {

    private static final String TAG = ReportDetailActivity.class.getSimpleName();

    private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    private CheckBox mFilterCb;

    private ActivityRecord mActivityRecord;

    private ReportDetailAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_detail);

        mActivityRecord = (ActivityRecord) getIntent().getSerializableExtra("activity_record");

        if (mActivityRecord == null) {
            finish();
            return;
        }

        ((TextView) findViewById(R.id.time)).setText(Utils.DATE_FORMAT.format(new Date(mActivityRecord.when)));
        String title = getString(R.string.record_cost, mActivityRecord.cost, mActivityRecord.activity_name);
        SpannableString ss = new SpannableString(title);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(mActivityRecord.cost >= StallBuster.getInstance().getThreshold() ? Constants.COLOR_WARNING : Constants.COLOR_NORMAL);
        ss.setSpan(colorSpan, 0, title.indexOf(" "), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        ((TextView) findViewById(R.id.title)).setText(ss);
        mFilterCb = (CheckBox) findViewById(R.id.filter);
        mFilterCb.setText(getString(R.string.cb_hide_system_callbacks_executed_less_than_some_ms, StallBuster.getInstance().getCallbackThreshold()));
        mFilterCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mAdapter.setData(filteredRecords());
            }
        });

        mAdapter = new ReportDetailAdapter();
        RecyclerView container = (RecyclerView) findViewById(R.id.container);
        LinearLayoutManager lm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        container.setLayoutManager(lm);
        container.addItemDecoration(new SimpleDecoration());
        mAdapter.setData(filteredRecords());
        container.setAdapter(mAdapter);
    }

    private List<Record> filteredRecords() {
        if (mFilterCb.isChecked()) {
            List<Record> filtered = new ArrayList<>();
            for (Record r : mActivityRecord.records) {
                if ((!r.target.startsWith("(android") && !r.target.startsWith("(com.android"))|| r.cost >= StallBuster.getInstance().getCallbackThreshold()) {
                    filtered.add(r);
                }
            }
            return filtered;
        } else {
            return mActivityRecord.records;
        }
    }


    private class ReportDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<Record> mData = new ArrayList<>();

        public void setData(List<Record> data) {
            if (mData != null) {
                mData.clear();
            }
            mData.addAll(data);
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.detail_list_item, parent, false);
            return new ReportDetailHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            final Record r = mData.get(position);
            ReportDetailHolder h = (ReportDetailHolder) holder;
            h.time.setText(DATE_FORMAT.format(new Date(r.when)));
            h.cost.setText(r.cost+"ms");
            if (r.cost >= StallBuster.getInstance().getCallbackWarningThreshold()) {
                h.cost.setTextColor(Constants.COLOR_WARNING);
            } else {
                h.cost.setTextColor(Constants.COLOR_INFO);
            }
            h.target.setText(r.target);
            if (TextUtils.isEmpty(r.callback)) {
                h.msg_callback_title.setText(R.string.message);
                h.msg_callback.setText(getMsgString(r));
            } else {
                h.msg_callback_title.setText(R.string.callback);
                h.msg_callback.setText(r.callback);
            }
            if (!r.target.startsWith("(android") && !r.target.startsWith("(com.android")) {
                h.target.setTextColor(Constants.COLOR_WARNING);
                h.msg_callback.setTextColor(Constants.COLOR_WARNING);
            } else {
                h.target.setTextColor(Constants.COLOR_INFO);
                h.msg_callback.setTextColor(Constants.COLOR_INFO);
            }
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }
    }

    private String getMsgString(Record r) {
        if ("(android.view.ViewRootImpl$ViewRootHandler)".equals(r.target)) {
            return ""+r.what+" ["+Constants.sViewRootImplMsgMap.get(r.what)+"]";
        } else if ("(android.app.ActivityThread$H)".equals(r.target)) {
            return ""+r.what+" ["+Constants.sActivityThreadMsgMap.get(r.what)+"]";
        } else {
            return ""+r.what;
        }
    }

    private class ReportDetailHolder extends RecyclerView.ViewHolder {

        TextView time;
        TextView cost;
        TextView target;
        TextView msg_callback_title;
        TextView msg_callback;

        public ReportDetailHolder(View itemView) {
            super(itemView);
            time = (TextView) itemView.findViewById(R.id.time);
            cost = (TextView) itemView.findViewById(R.id.cost);
            target = (TextView) itemView.findViewById(R.id.target);
            msg_callback = (TextView) itemView.findViewById(R.id.msg_callback);
            msg_callback_title = (TextView) itemView.findViewById(R.id.msg_callback_title);
        }
    }
}
