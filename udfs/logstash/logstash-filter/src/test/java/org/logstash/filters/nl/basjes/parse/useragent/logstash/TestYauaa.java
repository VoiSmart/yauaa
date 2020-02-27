/*
 * Yet Another UserAgent Analyzer
 * Copyright (C) 2013-2020 Niels Basjes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.logstash.filters.nl.basjes.parse.useragent.logstash;

import co.elastic.logstash.api.Configuration;
import co.elastic.logstash.api.Context;
import co.elastic.logstash.api.Event;
import co.elastic.logstash.api.Filter;
import org.junit.jupiter.api.Test;
import org.logstash.plugins.ConfigurationImpl;
import org.logstash.plugins.ContextImpl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.logstash.filters.nl.basjes.parse.useragent.logstash.Yauaa.FIELDS_CONFIG;
import static org.logstash.filters.nl.basjes.parse.useragent.logstash.Yauaa.SOURCE_CONFIG;

public class TestYauaa {

    @Test
    public void testNormalUse() {
        String sourceField = "foo";

        Map<String, String> fieldMappings = new HashMap<>();
        fieldMappings.put("DeviceClass", "DC");
        fieldMappings.put("AgentNameVersion", "ANV");

        Map<String, Object> configMap = new HashMap<>();
        configMap.put("source", sourceField);
        configMap.put("fields", fieldMappings);

        Configuration config = new ConfigurationImpl(configMap);

        Context context = new ContextImpl(null, null);
        Filter  filter  = new Yauaa("testNormalUse", config, context);

        Event e = new org.logstash.Event();
        e.setField(sourceField,
            "Mozilla/5.0 (X11; Linux x86_64) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) " +
                "Chrome/48.0.2564.82 Safari/537.36");

        Collection<Event> results = filter.filter(Collections.singletonList(e), null);

        assertEquals(1, results.size());
        assertEquals("Desktop", e.getField("DC"));
        assertEquals("Chrome 48.0.2564.82", e.getField("ANV"));
        assertEquals("testNormalUse", filter.getId());
        assertEquals("testNormalUse", filter.getId());
        assertTrue(filter.configSchema().contains(SOURCE_CONFIG));
        assertTrue(filter.configSchema().contains(FIELDS_CONFIG));
    }

    @Test
    public void testBadConfigNothing() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            Map<String, Object> configMap = new HashMap<>();
            Configuration       config    = new ConfigurationImpl(configMap);
            Context             context   = new ContextImpl(null, null);
            Filter              filter    = new Yauaa("bad", config, context);
            fail("Should never get here: " + filter);
        });
        assertTrue(
            allOf(
                containsString("The \"source\" has not been specified."),
                containsString("The list of needed \"fields\" has not been specified."))
                .matches(exception.getMessage()));
    }

    @Test
    public void testBadConfigNoSource() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            Map<String, String> fieldMappings = new HashMap<>();
            fieldMappings.put("DeviceClass", "DC");
            fieldMappings.put("AgentNameVersion", "ANV");

            Map<String, Object> configMap = new HashMap<>();
            configMap.put("fields", fieldMappings);

            Configuration config = new ConfigurationImpl(configMap);

            Context context = new ContextImpl(null, null);
            Filter  filter  = new Yauaa("bad", config, context);
            fail("Should never get here: " + filter);
        });
        assertTrue(exception.getMessage().contains("The \"source\" has not been specified."));
    }

    @Test
    public void testBadConfigEmptySource() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            Map<String, String> fieldMappings = new HashMap<>();
            fieldMappings.put("DeviceClass", "DC");
            fieldMappings.put("AgentNameVersion", "ANV");

            Map<String, Object> configMap = new HashMap<>();
            configMap.put("source", ""); // EMPTY STRING
            configMap.put("fields", fieldMappings);

            Configuration config = new ConfigurationImpl(configMap);

            Context context = new ContextImpl(null, null);
            Filter  filter  = new Yauaa("bad", config, context);
            fail("Should never get here: " + filter);
        });
        assertTrue(exception.getMessage().contains("The \"source\" is empty."));
    }

    @Test
    public void testBadConfigNoFields() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            Map<String, Object> configMap = new HashMap<>();
            configMap.put("source", "foo");

            Configuration config = new ConfigurationImpl(configMap);

            Context context = new ContextImpl(null, null);
            Filter  filter  = new Yauaa("bad", config, context);
            fail("Should never get here: " + filter);
        });
        assertTrue(exception.getMessage().contains("The list of needed \"fields\" has not been specified."));
    }

    @Test
    public void testBadConfigFieldsEmpty() {

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            Map<String, String> fieldMappings = new HashMap<>();

            Map<String, Object> configMap = new HashMap<>();
            configMap.put("source", "foo");
            configMap.put("fields", fieldMappings);

            Configuration config = new ConfigurationImpl(configMap);

            Context context = new ContextImpl(null, null);
            Filter  filter  = new Yauaa("bad", config, context);
            fail("Should never get here: " + filter);
        });
        assertTrue(exception.getMessage().contains("The list of needed \"fields\" is empty."));
    }

    @Test
    public void testBadConfigIllegalField() {

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            Map<String, String> fieldMappings = new HashMap<>();
            fieldMappings.put("NoSuchField", "NSF");
            fieldMappings.put("AgentNameVersion", "ANV");

            Map<String, Object> configMap = new HashMap<>();
            configMap.put("source", "foo");
            configMap.put("fields", fieldMappings);

            Configuration config = new ConfigurationImpl(configMap);

            Context context = new ContextImpl(null, null);
            Filter  filter  = new Yauaa("bad", config, context);
            fail("Should never get here: " + filter);
        });
        assertTrue(exception.getMessage().contains("The requested field \"NoSuchField\" does not exist."));
    }

    @Test
    public void testBadConfigIllegalFieldNoSource() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            Map<String, String> fieldMappings = new HashMap<>();
            fieldMappings.put("NoSuchField", "NSF");
            fieldMappings.put("AgentNameVersion", "ANV");

            Map<String, Object> configMap = new HashMap<>();
            configMap.put("fields", fieldMappings);

            Configuration config = new ConfigurationImpl(configMap);

            Context context = new ContextImpl(null, null);
            Filter  filter  = new Yauaa("bad", config, context);
            fail("Should never get here: " + filter);
        });
        assertTrue(
            allOf(
                containsString("The \"source\" has not been specified."),
                containsString("The requested field \"NoSuchField\" does not exist."))
                .matches(exception.getMessage()));
    }

    @Test
    public void testBadConfigIllegalFieldEmptySource() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            Map<String, String> fieldMappings = new HashMap<>();
            fieldMappings.put("NoSuchField", "NSF");
            fieldMappings.put("AgentNameVersion", "ANV");

            Map<String, Object> configMap = new HashMap<>();
            configMap.put("source", "");
            configMap.put("fields", fieldMappings);

            Configuration config = new ConfigurationImpl(configMap);

            Context context = new ContextImpl(null, null);
            Filter  filter  = new Yauaa("bad", config, context);
            fail("Should never get here: " + filter);
        });
        assertTrue(
            allOf(
                containsString("The \"source\" is empty."),
                containsString("The requested field \"NoSuchField\" does not exist."))
                .matches(exception.getMessage()));
    }

}
