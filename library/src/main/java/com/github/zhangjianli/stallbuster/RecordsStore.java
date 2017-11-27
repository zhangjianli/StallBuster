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

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;

import com.github.zhangjianli.R;
import com.github.zhangjianli.stallbuster.model.ActivityRecord;
import com.github.zhangjianli.stallbuster.ui.ReportListActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

public final class RecordsStore {

    private static final Object sLock = new Object();

    private static final String DIR_NAME = "stall_buster";

    private static final String SUFFIX = ".recd";

    private static final int NOTIFICATION_ID = 1001;

    private static class RecordFileFilter implements FilenameFilter {

        RecordFileFilter() {
        }

        @Override
        public boolean accept(File dir, String filename) {
            return filename.endsWith(SUFFIX);
        }
    }

    private static FilenameFilter sFilter = new RecordFileFilter();

    /**
     * save one activity record
     * @param activityRecord record to save
     * @return the file path this record is saved
     */
    public static boolean saveOneRecord(ActivityRecord activityRecord) {
        boolean ret = saveOneRecord(activityRecord.toJsonString());
        if (ret) {
            Application app = StallBuster.getInstance().getApp();
            Intent intent = new Intent(StallBuster.getInstance().getApp(), ReportListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(StallBuster.getInstance().getApp(), 1, intent, FLAG_UPDATE_CURRENT);
            NotificationManager nm = (NotificationManager)app.getSystemService(Context.NOTIFICATION_SERVICE);
            nm.cancel(NOTIFICATION_ID);
            Notification notification = new NotificationCompat.Builder(app)
                    .setAutoCancel(true)
                    .setContentTitle(app.getString(R.string.notification_title, activityRecord.cost, activityRecord.activity_name))
                    .setContentText(app.getString(R.string.notification_text))
                    .setSmallIcon(android.R.drawable.sym_def_app_icon)
                    .setContentIntent(pendingIntent)
                    .build();
            nm.notify(NOTIFICATION_ID, notification);
        }
        return ret;
    }

    /**
     * save one activity record
     * @param record_string to save
     * @return the file path this record is saved
     */
    public static boolean saveOneRecord(String record_string) {
        BufferedWriter writer = null;
        synchronized (sLock) {
            try {
                String path = getRecordDir().getAbsolutePath() + "/"+ System.currentTimeMillis() + SUFFIX;
                OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(path, true), "UTF-8");
                writer = new BufferedWriter(out);
                writer.write(record_string);
                writer.flush();
                writer.close();
                writer = null;
                // check if we have more than max_records_count files and delete them now
                StallBuster.getInstance().getFileHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        cleanupLogs();
                    }
                });
                return true;

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (writer != null) {
                        writer.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    private static void cleanupLogs() {
        synchronized (sLock) {
            try {
                List<File> files = getAllRecords();
                if (files != null && files.size() > StallBuster.getInstance().mConfig.max_records_count) {
                    List<File> to_delete = files.subList(StallBuster.getInstance().mConfig.max_records_count, files.size());
                    for (File f : to_delete) {
                        f.delete();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * get dir of the record file
     * @return File represents record dir
     */
    public static File getRecordDir() {
        File dir = new File(getRecordDirPath());
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    /** Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /** Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    /**
     * name of the directory host all records files
     * @return the path
     */
    public static String getRecordDirPath() {
        if (isExternalStorageWritable()) {
            return StallBuster.getInstance().getApp().getExternalFilesDir(null) + "/" + DIR_NAME;
        }
        return StallBuster.getInstance().getApp().getFilesDir() + "/" + DIR_NAME;
    }

    /**
     * get all record files
     * @return record files
     */
    public static List<File> getAllRecords() {
        File f = getRecordDir();
        if (f.exists() && f.isDirectory()) {
            // order by time desc
            List<File> f_list = Arrays.asList(f.listFiles(sFilter));
            Collections.sort(f_list, new Comparator<File>() {
                @Override
                public int compare(File left, File right) {
                    return (left.lastModified() < right.lastModified()) ? 1 : ((left.lastModified() == right.lastModified()) ? 0 : -1);
                }
            });
            return f_list;
        }
        return null;
    }

    /**
     * delete all record files
     */
    public static void deleteAll() {
        synchronized (sLock) {
            try {
                List<File> files = getAllRecords();
                if (files != null) {
                    for (File file : files) {
                        file.delete();
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * delete one record
     */
    public static boolean deleteOneRecordSync(String name) {
        synchronized (sLock) {
            return deleteOneRecord(name);
        }
    }

    public static boolean deleteOneRecord(String name) {
        if (TextUtils.isEmpty(name)) {
            return false;
        }
        try {
            List<File> files = getAllRecords();
            if (files != null) {
                for (File file : files) {
                    if (name.equals(file.getName())) {
                        return file.delete();
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * load all records
     */
    public static List<ActivityRecord> loadAllRecords() {
        List<ActivityRecord> records = new ArrayList<>();
        synchronized (sLock) {
            try {
                List<File> files = getAllRecords();
                for (File f : files) {
                    ActivityRecord record = readOneRecordFromFile(f);
                    if (record != null) {
                        record.file_name = f.getName();
                        records.add(record);
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return records;
    }

    /**
     * load one records
     */
    private static ActivityRecord readOneRecordFromFile(File f) {
        ActivityRecord r = null;
        BufferedReader reader = null;
        try {
            InputStreamReader in = new InputStreamReader(new FileInputStream(f), "UTF-8");
            reader = new BufferedReader(in);
            String s = reader.readLine();
            r = ActivityRecord.fromJsonString(s);
            reader.close();
            reader = null;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return r;
    }

    private RecordsStore() {

    }
}
