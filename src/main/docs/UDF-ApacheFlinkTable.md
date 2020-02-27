# User Defined Function for Apache Flink Table

## Getting the UDF
You can get the prebuilt UDF from [maven central](https://search.maven.org/artifact/nl.basjes.parse.useragent/yauaa-flink-table/{{ book.YauaaVersion }}/jar).

If you use a maven based project simply add this dependency to your project.

<pre><code>&lt;dependency&gt;
  &lt;groupId&gt;nl.basjes.parse.useragent&lt;/groupId&gt;
  &lt;artifactId&gt;yauaa-flink-table&lt;/artifactId&gt;
  &lt;version&gt;{{ book.YauaaVersion }}&lt;/version&gt;
&lt;/dependency&gt;
</code></pre>


## IMPORTANT: Breaking API change
In version 5.15 this function was rewritten and as a consequence the API has changed.

The function now returns a `Map<String, String>` with all the requested values in one go.

## Syntax
Assume you register this function under the name `ParseUserAgent`
Then the generic usage in your SQL is

<pre><code>ParseUserAgent(&lt;useragent&gt;)</code></pre>

This returns a `Map<String, String>` with all the requested values in one go.

## Example usage (Java)
Assume you have a either a BatchTableEnvironment or a StreamTableEnvironment in which you have defined your records as a table.
In most cases I see (clickstream data) these records contain the useragent string in a column.

    // Give the stream a Table Name
    tableEnv.registerDataStream("AgentStream", inputStream, "timestamp, url, useragent");

Now you must do four things:

* Determine the names of the fields you need.
* Register the function with the full list of all the fields you want under the name you want.
* Use the function in your SQL to do the parsing and extract the fields from that.
* Run the query


    // Register the function with all the desired fieldnames and optionally the size of the cache
    tableEnv.registerFunction("ParseUserAgent", new AnalyzeUseragentFunction(15000, "DeviceClass", "AgentNameVersionMajor"));

    // Define the query.
    String sqlQuery =
        "SELECT useragent,"+
        "       ParseUserAgent(useragent)   as parsedUseragent" +
        "FROM AgentStream";

    Table  resultTable   = tableEnv.sqlQuery(sqlQuery);

    // A String and the Map with all results
    TypeInformation<Row> tupleType = new RowTypeInfo(STRING, MAP(STRING, STRING));
    DataStream<Row> resultSet = tableEnv.toAppendStream(resultTable, tupleType);

or something like this

    // Register the function with all the desired fieldnames and optionally the size of the cache
    tableEnv.registerFunction("ParseUserAgent", new AnalyzeUseragentFunction(15000, "DeviceClass", "AgentNameVersionMajor"));

    // Define the query.
    String sqlQuery =
        "SELECT useragent,"+
        "       parsedUseragent['DeviceClass']              AS deviceClass," +
        "       parsedUseragent['AgentNameVersionMajor']    AS agentNameVersionMajor " +
        "FROM ( " +
        "   SELECT useragent," +
        "          ParseUserAgent(useragent) AS parsedUseragent" +
        "   FROM   AgentStream " +
        ")";

    Table  resultTable   = tableEnv.sqlQuery(sqlQuery);

    // 3 Strings
    TypeInformation<Row> tupleType = new RowTypeInfo(STRING, STRING, STRING);
    DataStream<Row> resultSet = tableEnv.toAppendStream(resultTable, tupleType);


