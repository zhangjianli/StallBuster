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

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.zhangjianli.R;
import com.github.zhangjianli.stallbuster.Constants;
import com.github.zhangjianli.stallbuster.RecordsStore;
import com.github.zhangjianli.stallbuster.StallBuster;
import com.github.zhangjianli.stallbuster.model.ActivityRecord;
import com.github.zhangjianli.stallbuster.ui.adapter.SimpleDecoration;
import com.github.zhangjianli.stallbuster.utils.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReportListActivity extends AppCompatActivity {

    private static final String TAG = ReportListActivity.class.getSimpleName();

    private View mLoadingView;
    private View mEmptyView;
    private RecyclerView mContainer;
    private ReportListAdapter mAdapter;

    private List<ActivityRecord> mRecords = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_list);
        mLoadingView = findViewById(R.id.loading_view);
        mEmptyView = findViewById(R.id.empty_view);
        mContainer = (RecyclerView) findViewById(R.id.container);
        findViewById(R.id.delete_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(ReportListActivity.this)
                        .setMessage(R.string.delete_all_record)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mRecords.clear();
                                mAdapter.notifyDataSetChanged();
                                if (mRecords.size() == 0) {
                                    mEmptyView.setVisibility(View.VISIBLE);
                                }
                                StallBuster.getInstance().getFileHandler().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        RecordsStore.deleteAll();
                                    }
                                });
                            }
                        })
                        .setNegativeButton(R.string.no, null)
                        .show();
            }
        });

        mAdapter = new ReportListAdapter();
        LinearLayoutManager lm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mContainer.setLayoutManager(lm);
        mContainer.addItemDecoration(new SimpleDecoration());
        mContainer.setAdapter(mAdapter);

        loadRecords();
    }

    private void loadRecords() {
        new LoadRecordsTask().execute();
    }

    private void updateViews() {
        if (mRecords.size() == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
            mAdapter.notifyDataSetChanged();
        }
    }

    private class LoadRecordsTask extends AsyncTask<Void, Void, List<ActivityRecord>> {

        @Override
        protected void onPreExecute() {
            mLoadingView.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<ActivityRecord> doInBackground(Void... voids) {
            return RecordsStore.loadAllRecords();
        }

        @Override
        protected void onPostExecute(List<ActivityRecord> result) {
            mLoadingView.setVisibility(View.GONE);
            mRecords = result;
            updateViews();
        }
    }

    private class ReportListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.simple_list_item, parent, false);
            return new ReportListHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            final ActivityRecord r = mRecords.get(position);
            ReportListHolder h = (ReportListHolder) holder;
            String title = getString(R.string.record_cost, r.cost, r.activity_name);
            SpannableString ss = new SpannableString(title);
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(r.cost >= StallBuster.getInstance().getThreshold() ? Constants.COLOR_WARNING : Constants.COLOR_NORMAL);
            ss.setSpan(colorSpan, 0, title.indexOf(" "), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            h.title.setText(ss);
            h.sub_title.setText(Utils.DATE_FORMAT.format(new Date(r.when)));
            h.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(ReportListActivity.this, ReportDetailActivity.class);
                    i.putExtra("activity_record", r);
                    startActivity(i);
                }
            });
            h.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    new AlertDialog.Builder(ReportListActivity.this)
                            .setMessage(R.string.delete_this)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    mRecords.remove(r);
                                    mAdapter.notifyDataSetChanged();
                                    if (mRecords.size() == 0) {
                                        mEmptyView.setVisibility(View.VISIBLE);
                                    }
                                    StallBuster.getInstance().getFileHandler().post(new Runnable() {
                                        @Override
                                        public void run() {
                                            RecordsStore.deleteOneRecordSync(r.file_name);
                                        }
                                    });
                                }
                            })
                            .setNegativeButton(R.string.no, null)
                            .show();
                    return true;
                }
            });
        }

        @Override
        public int getItemCount() {
            return mRecords.size();
        }
    }

    private class ReportListHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView sub_title;

        public ReportListHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            sub_title = (TextView) itemView.findViewById(R.id.sub_title);
        }
    }
}
