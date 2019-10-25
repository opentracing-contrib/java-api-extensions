/**
 * Copyright 2017 The OpenTracing Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package io.opentracing.contrib.api.tracer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import io.opentracing.Span;
import io.opentracing.contrib.api.SpanObserver;

@RunWith(Parameterized.class)
public class APIExtensionsSpanTest {

    private SpanFactory spanFactory;

    @Captor
    private ArgumentCaptor<Long> longCaptor;
    
    public APIExtensionsSpanTest(SpanFactory spanFactory) {
        this.spanFactory = spanFactory;
    }

    @Parameters
    public static Collection<SpanFactory> factories() {
        return Arrays.<SpanFactory>asList(new SpanFactory() {
            @Override
            public Span create() {
                return Mockito.mock(Span.class);
            }            
        },new SpanFactory() {
            @Override
            public Span create() {
                return null;
            }            
        });
    }

    @Before
    public void init(){
       MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetDurationFromTimestampsNotFinished() throws InterruptedException {
        APIExtensionsSpan span = new APIExtensionsSpan(null, null,
                TimeUnit.MILLISECONDS.toMicros(System.currentTimeMillis()), 0, null);
        synchronized(this) {
            wait(100);
        }
        assertEquals(0, span.getDuration());
    }

    @Test
    public void testGetDurationFromTimestampsFinished() throws InterruptedException {
        APIExtensionsSpan span = new APIExtensionsSpan(spanFactory.create(), null,
                TimeUnit.MILLISECONDS.toMicros(System.currentTimeMillis()), 0, null);
        synchronized(this) {
            wait(100);
        }
        span.finish();
        assertNotEquals(0, span.getDuration());
    }

    @Test
    public void testGetDurationFromNanosNotFinished() throws InterruptedException {
        APIExtensionsSpan span = new APIExtensionsSpan(null, null,
                TimeUnit.MILLISECONDS.toMicros(System.currentTimeMillis()), System.nanoTime(), null);
        synchronized(this) {
            wait(100);
        }
        assertEquals(0, span.getDuration());
    }

    @Test
    public void testGetDurationFromNanosFinished() throws InterruptedException {
        APIExtensionsSpan span = new APIExtensionsSpan(spanFactory.create(), null,
                TimeUnit.MILLISECONDS.toMicros(System.currentTimeMillis()), System.nanoTime(), null);
        synchronized(this) {
            wait(100);
        }
        span.finish();
        assertNotEquals(0, span.getDuration());
    }

    @Test
    public void testGetDurationNanosAccuracy() throws InterruptedException {
        APIExtensionsSpan span = new APIExtensionsSpan(spanFactory.create(), null,
                TimeUnit.MILLISECONDS.toMicros(System.currentTimeMillis()), System.nanoTime(), null);
        synchronized(this) {
            wait(100);
        }
        span.finish();
        assertNotEquals(0, span.getDuration() % 1000);
    }

    @Test
    public void testGetDurationExplicitStartMillisAccuracy() throws InterruptedException {
        APIExtensionsSpan span = new APIExtensionsSpan(spanFactory.create(), null,
                TimeUnit.MILLISECONDS.toMicros(System.currentTimeMillis()), 0, null);
        synchronized(this) {
            wait(100);
        }
        span.finish();
        assertEquals(0, span.getDuration() % 1000);
    }

    @Test
    public void testGetDurationExplicitFinishMillisAccuracy() throws InterruptedException {
        APIExtensionsSpan span = new APIExtensionsSpan(spanFactory.create(), null,
                TimeUnit.MILLISECONDS.toMicros(System.currentTimeMillis()), System.nanoTime(), null);
        synchronized(this) {
            wait(100);
        }
        span.finish(TimeUnit.MILLISECONDS.toMicros(System.currentTimeMillis()));
        assertEquals(0, span.getDuration() % 1000);
    }

    @Test
    public void testOnSetOperationName() throws InterruptedException {
        Span testSpan = spanFactory.create();
        APIExtensionsSpan span = new APIExtensionsSpan(testSpan, null,
                0, 0, null);
        SpanObserver observer = Mockito.mock(SpanObserver.class);
        span.addSpanObserver(observer);

        span.setOperationName("testop");

        verify(observer).onSetOperationName(span, "testop");
        if (testSpan != null) {
            verify(testSpan).setOperationName("testop");
        }
    }

    @Test
    public void testOnSetTagString() throws InterruptedException {
        Span testSpan = spanFactory.create();
        APIExtensionsSpan span = new APIExtensionsSpan(testSpan, null,
                0, 0, new HashMap<String, Object>());
        SpanObserver observer = Mockito.mock(SpanObserver.class);
        span.addSpanObserver(observer);

        span.setTag("testkey", "testvalue");

        verify(observer).onSetTag(span, "testkey", "testvalue");
        if (testSpan != null) {
            verify(testSpan).setTag("testkey", "testvalue");
        }
    }

    @Test
    public void testOnSetTagNumber() throws InterruptedException {
        Span testSpan = spanFactory.create();
        APIExtensionsSpan span = new APIExtensionsSpan(testSpan, null,
                0, 0, new HashMap<String, Object>());
        SpanObserver observer = Mockito.mock(SpanObserver.class);
        span.addSpanObserver(observer);

        span.setTag("testkey", 5);

        verify(observer).onSetTag(span, "testkey", 5);
        if (testSpan != null) {
            verify(testSpan).setTag("testkey", 5);
        }
    }

    @Test
    public void testOnSetTagBoolean() throws InterruptedException {
        Span testSpan = spanFactory.create();
        APIExtensionsSpan span = new APIExtensionsSpan(testSpan, null,
                0, 0, new HashMap<String, Object>());
        SpanObserver observer = Mockito.mock(SpanObserver.class);
        span.addSpanObserver(observer);

        span.setTag("testkey", Boolean.TRUE);

        verify(observer).onSetTag(span, "testkey", Boolean.TRUE);
        if (testSpan != null) {
            verify(testSpan).setTag("testkey", Boolean.TRUE);
        }
    }

    @Test
    public void testOnSetTagNullKey() throws InterruptedException {
        Span testSpan = spanFactory.create();
        APIExtensionsSpan span = new APIExtensionsSpan(testSpan, null,
                0, 0, new ConcurrentHashMap<String, Object>());
        SpanObserver observer = Mockito.mock(SpanObserver.class);
        span.addSpanObserver(observer);

        span.setTag((String)null, "testvalue");

        verify(observer).onSetTag(span, null, "testvalue");
        if (testSpan != null) {
            verify(testSpan).setTag((String)null, "testvalue");
        }
    }

    @Test
    public void testOnSetTagNullValue() throws InterruptedException {
        Span testSpan = spanFactory.create();
        APIExtensionsSpan span = new APIExtensionsSpan(testSpan, null,
                0, 0, new ConcurrentHashMap<String, Object>());
        SpanObserver observer = Mockito.mock(SpanObserver.class);
        span.addSpanObserver(observer);

        span.setTag("testkey", (String)null);

        verify(observer).onSetTag(span, "testkey", null);
        if (testSpan != null) {
            verify(testSpan).setTag("testkey", (String)null);
        }
    }

    @Test
    public void testOnSetBaggageItem() throws InterruptedException {
        Span testSpan = spanFactory.create();
        APIExtensionsSpan span = new APIExtensionsSpan(testSpan, null,
                0, 0, null);
        SpanObserver observer = Mockito.mock(SpanObserver.class);
        span.addSpanObserver(observer);

        span.setBaggageItem("testkey", "testvalue");

        verify(observer).onSetBaggageItem(span, "testkey", "testvalue");
        if (testSpan != null) {
            verify(testSpan).setBaggageItem("testkey", "testvalue");
        }
    }

    @Test
    public void testOnLogFields() throws InterruptedException {
        Span testSpan = spanFactory.create();
        APIExtensionsSpan span = new APIExtensionsSpan(testSpan, null,
                0, 0, null);
        SpanObserver observer = Mockito.mock(SpanObserver.class);
        span.addSpanObserver(observer);

        long ts = TimeUnit.MILLISECONDS.toMicros(System.currentTimeMillis());
        Map<String, String> fields = new HashMap<String, String>();
        span.log(ts, fields);

        verify(observer).onLog(span, ts, fields);
        if (testSpan != null) {
            verify(testSpan).log(ts, fields);
        }
    }

    @Test
    public void testOnLogEvent() throws InterruptedException {
        Span testSpan = spanFactory.create();
        APIExtensionsSpan span = new APIExtensionsSpan(testSpan, null,
                0, 0, null);
        SpanObserver observer = Mockito.mock(SpanObserver.class);
        span.addSpanObserver(observer);

        long ts = TimeUnit.MILLISECONDS.toMicros(System.currentTimeMillis());
        span.log(ts, "testevent");

        verify(observer).onLog(span, ts, "testevent");
        if (testSpan != null) {
            verify(testSpan).log(ts, "testevent");
        }
    }

    @Test
    public void testOnFinish() throws InterruptedException {
        Span testSpan = spanFactory.create();
        APIExtensionsSpan span = new APIExtensionsSpan(testSpan, null,
                0, 0, null);
        SpanObserver observer = Mockito.mock(SpanObserver.class);
        span.addSpanObserver(observer);

        span.finish();

        verify(observer).onFinish(Matchers.eq(span), longCaptor.capture());
        assertNotEquals(0, longCaptor.getValue().longValue());
        if (testSpan != null) {
            verify(testSpan).finish();
        }
    }

    @Test
    public void testOnFinishWithTimestamp() throws InterruptedException {
        Span testSpan = spanFactory.create();
        APIExtensionsSpan span = new APIExtensionsSpan(testSpan, null,
                0, 0, null);
        SpanObserver observer = Mockito.mock(SpanObserver.class);
        span.addSpanObserver(observer);

        long ts = TimeUnit.MILLISECONDS.toMicros(System.currentTimeMillis());
        span.finish(ts);

        verify(observer).onFinish(span, ts);
        if (testSpan != null) {
            verify(testSpan).finish(ts);
        }
    }

    public interface SpanFactory {
        public Span create();
    }
}
