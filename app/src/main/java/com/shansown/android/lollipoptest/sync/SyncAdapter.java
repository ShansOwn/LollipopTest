/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.shansown.android.lollipoptest.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;

import com.shansown.android.lollipoptest.provider.LollipopTestContract;
import com.shansown.android.lollipoptest.provider.LollipopTestContract.Entry;
import com.shansown.android.lollipoptest.synctest.FeedParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import static com.shansown.android.lollipoptest.util.LogUtils.LOGE;
import static com.shansown.android.lollipoptest.util.LogUtils.LOGI;
import static com.shansown.android.lollipoptest.util.LogUtils.makeLogTag;

/**
 * Define a sync adapter for the app.
 *
 * <p>This class is instantiated in {@link SyncService}, which also binds SyncAdapter to the system.
 * SyncAdapter should only be initialized in SyncService, never anywhere else.
 *
 * <p>The system calls onPerformSync() via an RPC call through the IBinder object supplied by
 * SyncService.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String TAG = makeLogTag(SyncAdapter.class);

    private static final Pattern sSanitizeAccountNamePattern = Pattern.compile("(.).*?(.?)@");

    /**
     * URL to fetch content from during a sync.
     *
     * <p>This points to the Android Developers Blog.
     */
    private static final String FEED_URL = "http://android-developers.blogspot.com/atom.xml";

    /**
     * Network connection timeout, in milliseconds.
     */
    private static final int NET_CONNECT_TIMEOUT_MILLIS = 15000;  // 15 seconds

    /**
     * Network read timeout, in milliseconds.
     */
    private static final int NET_READ_TIMEOUT_MILLIS = 10000;  // 10 seconds

    private final Context mContext;

    /**
     * Content resolver, for performing database operations.
     */
    private final ContentResolver mContentResolver;

    /**
     * Project used when querying content provider. Returns all known fields.
     */
    private static final String[] PROJECTION = new String[] {
            LollipopTestContract.Entry.ENTRY_ID,
            LollipopTestContract.Entry.ENTRY_TITLE,
            LollipopTestContract.Entry.ENTRY_LINK,
            LollipopTestContract.Entry.ENTRY_PUBLISHED};

    // Constants representing column positions from PROJECTION.
    public static final int COLUMN_ENTRY_ID = 0;
    public static final int COLUMN_TITLE = 1;
    public static final int COLUMN_LINK = 2;
    public static final int COLUMN_PUBLISHED = 3;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContext = context;
        mContentResolver = context.getContentResolver();
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContext = context;
        mContentResolver = context.getContentResolver();
    }

    /**
     * Called by the Android system in response to a request to run the sync adapter. The work
     * required to read data from the network, parse it, and store it in the content provider is
     * done here. Extending AbstractThreadedSyncAdapter ensures that all methods within SyncAdapter
     * run on a background thread. For this reason, blocking I/O and other long-running tasks can be
     * run <em>in situ</em>, and you don't have to set up a separate thread for them.
     .
     *
     * <p>This is where we actually perform any work required to perform a sync.
     * {@link AbstractThreadedSyncAdapter} guarantees that this will be called on a non-UI thread,
     * so it is safe to peform blocking I/O here.
     *
     * <p>The syncResult argument allows you to pass information back to the method that triggered
     * the sync.
     */
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        final boolean uploadOnly = extras.getBoolean(ContentResolver.SYNC_EXTRAS_UPLOAD, false);
        final boolean manualSync = extras.getBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, false);
        final boolean initialize = extras.getBoolean(ContentResolver.SYNC_EXTRAS_INITIALIZE, false);

        final String logSanitizedAccountName = sSanitizeAccountNamePattern
                .matcher(account.name).replaceAll("$1...$2@");

        LOGI(TAG, "Beginning sync for account " + logSanitizedAccountName + "," +
                " uploadOnly=" + uploadOnly +
                " manualSync=" + manualSync +
                " initialize=" + initialize);

        try {
            final URL dataUrl = new URL(FEED_URL);
            InputStream stream = null;

            try {
                LOGI(TAG, "Streaming data from network: " + dataUrl);
                stream = downloadUrl(dataUrl);
                updateLocalFeedData(stream, syncResult);
            } finally {
                if (stream != null) {
                    stream.close();
                }
            }

        } catch (MalformedURLException e) {
            LOGE(TAG, "Feed URL is malformed", e);
            syncResult.stats.numParseExceptions++;
            return;
        } catch (IOException e) {
            LOGE(TAG, "Error reading from network: " + e.toString());
            syncResult.stats.numIoExceptions++;
            return;
        } catch (XmlPullParserException | ParseException e) {
            LOGE(TAG, "Error parsing feed: " + e.toString());
            syncResult.stats.numParseExceptions++;
            return;
        } catch (RemoteException | OperationApplicationException e) {
            LOGE(TAG, "Error updating database: " + e.toString());
            syncResult.databaseError = true;
            return;
        }
        LOGI(TAG, "Network synchronization complete");
    }

    /**
     * Read XML from an input stream, storing it into the content provider.
     *
     * <p>This is where incoming data is persisted, committing the results of a sync. In order to
     * minimize (expensive) disk operations, we compare incoming data with what's already in our
     * database, and compute a merge. Only changes (insert/update/delete) will result in a database
     * write.
     *
     * <p>As an additional optimization, we use a batch operation to perform all database writes at
     * once.
     *
     * <p>Merge strategy:
     * 1. Get cursor to all items in feed<br/>
     * 2. For each item, check if it's in the incoming data.<br/>
     *    a. YES: Remove from "incoming" list. Check if data has mutated, if so, perform
     *            database UPDATE.<br/>
     *    b. NO: Schedule DELETE from database.<br/>
     * (At this point, incoming database only contains missing items.)<br/>
     * 3. For any items remaining in incoming list, ADD to database.
     */
    public void updateLocalFeedData(final InputStream stream, final SyncResult syncResult)
            throws IOException, XmlPullParserException, RemoteException,
            OperationApplicationException, ParseException {
        LOGI(TAG, "updateLocalFeedData!");
        final FeedParser feedParser = new FeedParser();
        final ContentResolver contentResolver = getContext().getContentResolver();

        LOGI(TAG, "Parsing stream as Atom feed");
        final List<FeedParser.Entry> entries = feedParser.parse(stream);
        LOGI(TAG, "Parsing complete. Found " + entries.size() + " entries");


        ArrayList<ContentProviderOperation> batch = new ArrayList<>();

        // Build hash table of incoming entries
        HashMap<String, FeedParser.Entry> entryMap = new HashMap<>();
        for (FeedParser.Entry e : entries) {
            entryMap.put(e.id, e);
        }

        // Get list of all items
        LOGI(TAG, "Fetching local entries for merge");
        Uri uri = Entry.CONTENT_URI; // Get all entries
        Cursor c = contentResolver.query(uri, PROJECTION, null, null, null);
        assert c != null;
        LOGI(TAG, "Found " + c.getCount() + " local entries. Computing merge solution...");

        // Find stale data
        String entryId;
        String title;
        String link;
        long published;
        while (c.moveToNext()) {
            syncResult.stats.numEntries++;
            entryId = c.getString(COLUMN_ENTRY_ID);
            title = c.getString(COLUMN_TITLE);
            link = c.getString(COLUMN_LINK);
            published = c.getLong(COLUMN_PUBLISHED);
            FeedParser.Entry match = entryMap.get(entryId);
            if (match != null) {
                // Entry exists. Remove from entry map to prevent insert later.
                entryMap.remove(entryId);
                // Check to see if the entry needs to be updated
                Uri existingUri = Entry.buildEntryUri(entryId);
                if ((match.title != null && !match.title.equals(title)) ||
                        (match.link != null && !match.link.equals(link)) ||
                        (match.published != published)) {
                    // Update existing record
                    LOGI(TAG, "Scheduling update: " + existingUri);
                    batch.add(ContentProviderOperation.newUpdate(existingUri)
                            .withValue(Entry.ENTRY_TITLE, title)
                            .withValue(Entry.ENTRY_LINK, link)
                            .withValue(Entry.ENTRY_PUBLISHED, published)
                            .build());
                    syncResult.stats.numUpdates++;
                } else {
                    LOGI(TAG, "No action: " + existingUri);
                }
            } else {
                // Entry doesn't exist. Remove it from the database.
                Uri deleteUri = Entry.buildEntryUri(entryId);
                LOGI(TAG, "Scheduling delete: " + deleteUri);
                batch.add(ContentProviderOperation.newDelete(deleteUri).build());
                syncResult.stats.numDeletes++;
            }
        }
        c.close();

        // Add new items
        for (FeedParser.Entry e : entryMap.values()) {
            LOGI(TAG, "Scheduling insert: entry_id=" + e.id);
            batch.add(ContentProviderOperation.newInsert(Entry.CONTENT_URI)
                    .withValue(Entry.ENTRY_ID, e.id)
                    .withValue(Entry.ENTRY_TITLE, e.title)
                    .withValue(Entry.ENTRY_LINK, e.link)
                    .withValue(Entry.ENTRY_PUBLISHED, e.published)
                    .build());
            syncResult.stats.numInserts++;
        }
        LOGI(TAG, "Merge solution ready. Applying batch update");
        mContentResolver.applyBatch(LollipopTestContract.CONTENT_AUTHORITY, batch);
        mContentResolver.notifyChange(
                Entry.CONTENT_URI,      // URI where data was modified
                null,                   // No local observer
                false);                 // IMPORTANT: Do not sync to network
        // This sample doesn't support uploads, but if *your* code does, make sure you set
        // syncToNetwork=false in the line above to prevent duplicate syncs.
    }

    /**
     * Given a string representation of a URL, sets up a connection and gets an input stream.
     */
    private InputStream downloadUrl(final URL url) throws IOException {
        LOGI(TAG, "downloadUrl");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(NET_READ_TIMEOUT_MILLIS /* milliseconds */);
        conn.setConnectTimeout(NET_CONNECT_TIMEOUT_MILLIS /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }
}
