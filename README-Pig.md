# User Defined Function for Apache Pig

## Getting the UDF
You can get the prebuilt UDF from maven central.
If you use a maven based project simply add this dependency

    <dependency>
      <groupId>nl.basjes.parse.useragent</groupId>
      <artifactId>yauaa-pig</artifactId>
      <classifier>udf</classifier>
      <version>3.2</version>
    </dependency>

## Building
Simply install the normal build tools for a Java project (i.e. maven and jdk) and then simply do:

    mvn clean package

## Example usage
    -- Import the UDF jar file so this script can use it
    REGISTER ../target/*-udf.jar;

    ------------------------------------------------------------------------
    -- Define a more readable name for the UDF and pass optional parameters
    -- First parameter is ALWAYS the cache size (as a text string!)
    -- The parameters after that are the requested fields.
    ----------
    -- If you simply want 'everything'
    -- DEFINE ParseUserAgent  nl.basjes.parse.useragent.pig.ParseUserAgent;
    ----------
    -- If you just want to set the cache
    -- DEFINE ParseUserAgent  nl.basjes.parse.useragent.pig.ParseUserAgent('10000');
    ----------
    -- If you want to set the cache and only retrieve the specified fields
    DEFINE ParseUserAgent  nl.basjes.parse.useragent.pig.ParseUserAgent('10000', 'DeviceClass', 'DeviceBrand' );

    rawData =
        LOAD 'testcases.txt'
        USING PigStorage()
        AS  ( useragent: chararray );

    UaData =
        FOREACH  rawData
        GENERATE useragent,
                 -- Do NOT specify a type for this field as the UDF provides the definitions
                 ParseUserAgent(useragent) AS parsedAgent;

License
=======
    Yet Another UserAgent Analyzer
    Copyright (C) 2013-2017 Niels Basjes

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
