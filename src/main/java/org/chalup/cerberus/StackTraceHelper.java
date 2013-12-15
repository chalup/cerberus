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

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

abstract class StackTraceHelper {

  private StackTraceHelper() {
  }

  private static final String ANDROID_DB_PACKAGE = SQLiteDatabase.class.getPackage().getName();
  private static final String CERBERUS_PACKAGE = CerberusCursorFactory.class.getPackage().getName();

  public static List<String> getStackTrace() {
    List<String> result = new ArrayList<String>();

    boolean doneSkipping = false;

    for (StackTraceElement stackTraceElement : new Throwable().getStackTrace()) {
      if (!doneSkipping) {
        if (stackTraceElement.getClassName().startsWith(ANDROID_DB_PACKAGE) ||
            stackTraceElement.getClassName().startsWith(CERBERUS_PACKAGE)) {
          continue;
        } else {
          doneSkipping = true;
        }
      }

      result.add(stackTraceElement.toString());
    }

    return result;
  }
}
