/*
 * Copyright (C) 2013 Jerzy Chalupski
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

package org.chalup.cerberus;

import static org.chalup.cerberus.DefaultCursorFactory.CURSOR_FACTORY;

import android.database.Cursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQuery;
import android.util.Log;

import java.util.List;
/**
 * This is the main class for using Cerberus. Cerberus is typically used by
 * creating a CerberusCursorFactory instance directly in super call in your
 * {@link SQLiteOpenHelper} constructor.
 *
 * The Cerberus will measure the elapsed time for every SQL query executed
 * by your {@link SQLiteOpenHelper} and if the query runs longer than the
 * limit specified in {@link CerberusCursorFactory} constructor, the query
 * report will be printed to the logcat.
 */
public class CerberusCursorFactory implements CursorFactory {
  private static final String TAG = "Cerberus";
  private static final String PADDING = "    ";

  private final QueryPlanGetter mQueryPlanGetter = new QueryPlanGetter();
  private final int mQueryTimeLimit;

  /**
   * Creates a {@link CursorFactory} to be used inside your {@link SQLiteOpenHelper}.
   *
   * @param queryTimeLimit The time limit of single query in milliseconds. If
   * executing the query takes longer than this, the query report containing the
   * query SQL, stack trace, elapsed query time and the results of the
   * {@code EXPLAIN QUERY PLAN} query for the offending query.
   */
  public CerberusCursorFactory(int queryTimeLimit) {
    mQueryTimeLimit = queryTimeLimit;
  }

  @Override
  public Cursor newCursor(SQLiteDatabase db, SQLiteCursorDriver masterQuery, String editTable, SQLiteQuery query) {
    Cursor cursor = CURSOR_FACTORY.newCursor(db, masterQuery, editTable, query);

    long start = System.currentTimeMillis();
    cursor.getCount(); // to fill window
    long end = System.currentTimeMillis();

    long elapsed = end - start;
    if (elapsed > mQueryTimeLimit) {
      // ugly hack warning, the next line depends on current SQLiteQuery implementation:
      String queryString = query.toString().substring("SQLiteQuery: ".length());
      List<String> queryPlan = mQueryPlanGetter.getQueryPlan(db, queryString, editTable);

      dumpIssueReport(queryString, queryPlan, elapsed);
    }

    return cursor;
  }

  private void dumpIssueReport(String queryString, List<String> queryPlan, long elapsedTime) {
    Log.w(TAG, "------------------- CERBERUS issue report -------------------");
    Log.w(TAG, "In query:");
    Log.w(TAG, PADDING + queryString);
    Log.w(TAG, ".");
    Log.w(TAG, "Executed from:");
    for (String stackFrame : StackTraceHelper.getStackTrace()) {
      Log.w(TAG, PADDING + stackFrame);
    }
    Log.w(TAG, ".");
    Log.e(TAG, "Query took " + elapsedTime + "ms");
    Log.w(TAG, ".");
    Log.w(TAG, "Full query plan:");
    for (String queryPlanRow : queryPlan) {
      Log.w(TAG, PADDING + queryPlanRow);
    }
    Log.w(TAG, "--------------------------------------------------------------");
  }
}
