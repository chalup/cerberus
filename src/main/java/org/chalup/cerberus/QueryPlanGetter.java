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
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class QueryPlanGetter {
  private final Map<String, List<String>> mCache = new HashMap<String, List<String>>();

  public List<String> getQueryPlan(SQLiteDatabase db, String queryString, String editTable) {
    if (!mCache.containsKey(queryString)) {
      mCache.put(queryString, buildQueryPlan(db, queryString, editTable));
    }
    return mCache.get(queryString);
  }

  private List<String> buildQueryPlan(SQLiteDatabase db, String queryString, String editTable) {
    Cursor cursor = db.rawQueryWithFactory(CURSOR_FACTORY, "EXPLAIN QUERY PLAN " + queryString, null, editTable);

    if (cursor == null) {
      throw new IllegalStateException();
    }

    try {
      List<String> result = new ArrayList<String>();
      for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
        result.add(cursor.getString(cursor.getColumnIndexOrThrow("detail")));
      }
      return result;
    } finally {
      cursor.close();
    }
  }
}
