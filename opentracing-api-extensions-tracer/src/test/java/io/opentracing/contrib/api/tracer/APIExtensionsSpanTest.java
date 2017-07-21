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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import io.opentracing.Span;
import io.opentracing.contrib.api.SpanObserver;

public class APIExtensionsSpanTest {

    @Captor
    private ArgumentCaptor<Long> longCaptor;
    
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
        APIExtensionsSpan span = new APIExtensionsSpan(Mockito.mock(Span.class), null,
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
        APIExtensionsSpan span = new APIExtensionsSpan(Mockito.mock(Span.class), null,
                TimeUnit.MILLISECONDS.toMicros(System.currentTimeMillis()), System.nanoTime(), null);
        synchronized(this) {
            wait(100);
        }
        span.finish();
        assertNotEquals(0, span.getDuration());
    }

    @Test
    public void testGetDurationNanosAccuracy() throws InterruptedException {
        APIExtensionsSpan span = new APIExtensionsSpan(Mockito.mock(Span.class), null,
                TimeUnit.MILLISECONDS.toMicros(System.currentTimeMillis()), System.nanoTime(), null);
        synchronized(this) {
            wait(100);
        }
        span.finish();
        assertNotEquals(0, span.getDuration() % 1000);
    }

    @Test
    public void testGetDurationExplicitStartMillisAccuracy() throws InterruptedException {
        APIExtensionsSpan span = new APIExtensionsSpan(Mockito.mock(Span.class), null,
                TimeUnit.MILLISECONDS.toMicros(System.currentTimeMillis()), 0, null);
        synchronized(this) {
            wait(100);
        }
        span.finish();
        assertEquals(0, span.getDuration() % 1000);
    }

    @Test
    public void testGetDurationExplicitFinishMillisAccuracy() throws InterruptedException {
        APIExtensionsSpan span = new APIExtensionsSpan(Mockito.mock(Span.class), null,
                TimeUnit.MILLISECONDS.toMicros(System.currentTimeMillis()), System.nanoTime(), null);
        synchronized(this) {
            wait(100);
        }
        span.finish(TimeUnit.MILLISECONDS.toMicros(System.currentTimeMillis()));
        assertEquals(0, span.getDuration() % 1000);
    }

    @Test
    public void testOnSetOperationName() throws InterruptedException {
        APIExtensionsSpan span = new APIExtensionsSpan(Mockito.mock(Span.class), null,
                0, 0, null);
        SpanObserver observer = Mockito.mock(SpanObserver.class);
        span.addSpanObserver(observer);

        span.setOperationName("testop");

        verify(observer).onSetOperationName(span, "testop");
    }

    @Test
    public void testOnSetTagString() throws InterruptedException {
        APIExtensionsSpan span = new APIExtensionsSpan(Mockito.mock(Span.class), null,
                0, 0, new HashMap<String, Object>());
        SpanObserver observer = Mockito.mock(SpanObserver.class);
        span.addSpanObserver(observer);

        span.setTag("testkey", "testvalue");

        verify(observer).onSetTag(span, "testkey", "testvalue");
    }

    @Test
    public void testOnSetTagNumber() throws InterruptedException {
        APIExtensionsSpan span = new APIExtensionsSpan(Mockito.mock(Span.class), null,
                0, 0, new HashMap<String, Object>());
        SpanObserver observer = Mockito.mock(SpanObserver.class);
        span.addSpanObserver(observer);

        span.setTag("testkey", 5);

        verify(observer).onSetTag(span, "testkey", 5);
    }

    @Test
    public void testOnSetTagBoolean() throws InterruptedException {
        APIExtensionsSpan span = new APIExtensionsSpan(Mockito.mock(Span.class), null,
                0, 0, new HashMap<String, Object>());
        SpanObserver observer = Mockito.mock(SpanObserver.class);
        span.addSpanObserver(observer);

        span.setTag("testkey", Boolean.TRUE);

        verify(observer).onSetTag(span, "testkey", Boolean.TRUE);
    }

    @Test
    public void testOnSetBaggageItem() throws InterruptedException {
        APIExtensionsSpan span = new APIExtensionsSpan(Mockito.mock(Span.class), null,
                0, 0, null);
        SpanObserver observer = Mockito.mock(SpanObserver.class);
        span.addSpanObserver(observer);

        span.setBaggageItem("testkey", "testvalue");

        verify(observer).onSetBaggageItem(span, "testkey", "testvalue");
    }

    @Test
    public void testOnLogFields() throws InterruptedException {
        APIExtensionsSpan span = new APIExtensionsSpan(Mockito.mock(Span.class), null,
                0, 0, null);
        SpanObserver observer = Mockito.mock(SpanObserver.class);
        span.addSpanObserver(observer);

        long ts = TimeUnit.MILLISECONDS.toMicros(System.currentTimeMillis());
        Map<String, String> fields = new HashMap<String, String>();
        span.log(ts, fields);

        verify(observer).onLog(span, ts, fields);
    }

    @Test
    public void testOnLogEvent() throws InterruptedException {
        APIExtensionsSpan span = new APIExtensionsSpan(Mockito.mock(Span.class), null,
                0, 0, null);
        SpanObserver observer = Mockito.mock(SpanObserver.class);
        span.addSpanObserver(observer);

        long ts = TimeUnit.MILLISECONDS.toMicros(System.currentTimeMillis());
        span.log(ts, "testevent");

        verify(observer).onLog(span, ts, "testevent");
    }

    @Test
    public void testOnFinish() throws InterruptedException {
        APIExtensionsSpan span = new APIExtensionsSpan(Mockito.mock(Span.class), null,
                0, 0, null);
        SpanObserver observer = Mockito.mock(SpanObserver.class);
        span.addSpanObserver(observer);

        span.finish();

        verify(observer).onFinish(Mockito.eq(span), longCaptor.capture());
        assertNotEquals(0, longCaptor.getValue().longValue());
    }

    @Test
    public void testOnFinishWithTimestamp() throws InterruptedException {
        APIExtensionsSpan span = new APIExtensionsSpan(Mockito.mock(Span.class), null,
                0, 0, null);
        SpanObserver observer = Mockito.mock(SpanObserver.class);
        span.addSpanObserver(observer);

        long ts = TimeUnit.MILLISECONDS.toMicros(System.currentTimeMillis());
        span.finish(ts);

        verify(observer).onFinish(span, ts);
    }

}
