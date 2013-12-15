Cerberus
========

Android library for detecting and reporting long running SQLite queries.

```java
public class Database extends SQLiteOpenHelper {

  public Database(Context context) {     super(context, DB_NAME, new CerberusCursorFactory(50), CURRENT_VERSION);   }  }
```

If some of your queries takes longer than the threshold specified in the `CerberusCursorFactory` constructor, the report containing the offending query, elapsed query time, stack trace, and the result of `EXPLAIN QUERY PLAN` sqlite query will be dumped into the logcat.

**NOTE**: do **NOT** use this component in release versions of your app. It changes your db behaviour, executes additional queries, and depends on current implementation of Android classes and certain sqlite features that might differ across the devices. Use Proguard/build variants/separate branch for testing/whatever to remove `CerberusCursorFactory` from your production code after tests.

Building
--------
This is standard maven project. To build it just execute:
```shell
mvn clean package
```
in directory with pom.xml.

License
-------

    Copyright (C) 2013 Jerzy Chalupski

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
