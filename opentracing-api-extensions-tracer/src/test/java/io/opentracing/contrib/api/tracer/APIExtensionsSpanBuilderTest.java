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

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import io.opentracing.ActiveSpan;
import io.opentracing.References;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.Tracer.SpanBuilder;
import io.opentracing.contrib.api.TracerObserver;

public class APIExtensionsSpanBuilderTest {

    @Captor
    private ArgumentCaptor<Span> spanCaptor;
    
    @Before
    public void init(){
       MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAsChildOfSpanContext() {
        SpanBuilder spanBuilder = Mockito.mock(SpanBuilder.class);
        APIExtensionsSpanBuilder extSpanBuilder = new APIExtensionsSpanBuilder(null, Collections.<TracerObserver>emptyList(),
                null, spanBuilder);
        
        SpanContext spanContext = Mockito.mock(SpanContext.class);

        extSpanBuilder.asChildOf(spanContext);
        Mockito.verify(spanBuilder).asChildOf(spanContext);
    }

    @Test
    public void testAsChildOfSpan() {
        SpanBuilder spanBuilder = Mockito.mock(SpanBuilder.class);
        APIExtensionsSpanBuilder extSpanBuilder = new APIExtensionsSpanBuilder(null, Collections.<TracerObserver>emptyList(),
                null, spanBuilder);
        
        Span span = Mockito.mock(Span.class);

        extSpanBuilder.asChildOf(span);
        Mockito.verify(spanBuilder).asChildOf(span);
    }

    @Test
    public void testAddReference() {
        SpanBuilder spanBuilder = Mockito.mock(SpanBuilder.class);
        APIExtensionsSpanBuilder extSpanBuilder = new APIExtensionsSpanBuilder(null, Collections.<TracerObserver>emptyList(),
                null, spanBuilder);
        
        SpanContext spanContext = Mockito.mock(SpanContext.class);

        extSpanBuilder.addReference(References.FOLLOWS_FROM, spanContext);
        Mockito.verify(spanBuilder).addReference(References.FOLLOWS_FROM, spanContext);
    }

    @Test
    public void testIgnoreActiveSpan() {
        SpanBuilder spanBuilder = Mockito.mock(SpanBuilder.class);
        APIExtensionsSpanBuilder extSpanBuilder = new APIExtensionsSpanBuilder(null, Collections.<TracerObserver>emptyList(),
                null, spanBuilder);
        
        extSpanBuilder.ignoreActiveSpan();
        Mockito.verify(spanBuilder).ignoreActiveSpan();
    }

    @Test
    public void testWithTagString() {
        SpanBuilder spanBuilder = Mockito.mock(SpanBuilder.class);
        APIExtensionsSpanBuilder extSpanBuilder = new APIExtensionsSpanBuilder(null, Collections.<TracerObserver>emptyList(),
                null, spanBuilder);
        
        extSpanBuilder.withTag("tagName", "tagValue");
        Mockito.verify(spanBuilder).withTag("tagName", "tagValue");
        assertEquals("tagValue", extSpanBuilder.tags().get("tagName"));
    }

    @Test
    public void testWithTagNumber() {
        SpanBuilder spanBuilder = Mockito.mock(SpanBuilder.class);
        APIExtensionsSpanBuilder extSpanBuilder = new APIExtensionsSpanBuilder(null, Collections.<TracerObserver>emptyList(),
                null, spanBuilder);
        
        extSpanBuilder.withTag("tagName", 5);
        Mockito.verify(spanBuilder).withTag("tagName", 5);
        assertEquals(5, extSpanBuilder.tags().get("tagName"));
    }

    @Test
    public void testWithTagBoolean() {
        SpanBuilder spanBuilder = Mockito.mock(SpanBuilder.class);
        APIExtensionsSpanBuilder extSpanBuilder = new APIExtensionsSpanBuilder(null, Collections.<TracerObserver>emptyList(),
                null, spanBuilder);
        
        extSpanBuilder.withTag("tagName", Boolean.TRUE);
        Mockito.verify(spanBuilder).withTag("tagName", Boolean.TRUE);
        assertEquals(Boolean.TRUE, extSpanBuilder.tags().get("tagName"));
    }

    @Test
    public void testWithStartTimestamp() {
        SpanBuilder spanBuilder = Mockito.mock(SpanBuilder.class);
        APIExtensionsSpanBuilder extSpanBuilder = new APIExtensionsSpanBuilder(null, Collections.<TracerObserver>emptyList(),
                null, spanBuilder);

        // Initially should have value, but will be reset to 0 when timestamp explicitly set
        assertNotEquals(0, extSpanBuilder.startTimeNano());

        long ts = TimeUnit.MILLISECONDS.toMicros(System.currentTimeMillis());
        extSpanBuilder.withStartTimestamp(ts);
        Mockito.verify(spanBuilder).withStartTimestamp(ts);

        assertEquals(0, extSpanBuilder.startTimeNano());
    }

    @Test
    public void testStartManual() {
        SpanBuilder spanBuilder = Mockito.mock(SpanBuilder.class);
        TracerObserver observer = Mockito.mock(TracerObserver.class);
        APIExtensionsSpanBuilder extSpanBuilder = new APIExtensionsSpanBuilder(null, Collections.singletonList(observer),
                "op", spanBuilder);

        Span manualSpan = extSpanBuilder.startManual();
        assertTrue(manualSpan instanceof APIExtensionsSpan);
        
        APIExtensionsSpan extSpan = (APIExtensionsSpan)manualSpan;

        Mockito.verify(observer).onStart(extSpan);
        Mockito.verify(spanBuilder).startManual();
        assertEquals("op", extSpan.getOperationName());
    }

    @Test
    public void testStartActive() {
        Tracer tracer = Mockito.mock(Tracer.class);
        ActiveSpan activeSpan = Mockito.mock(ActiveSpan.class);
        SpanBuilder spanBuilder = Mockito.mock(SpanBuilder.class);
        APIExtensionsSpanBuilder extSpanBuilder = new APIExtensionsSpanBuilder(tracer, Collections.<TracerObserver>emptyList(),
                null, spanBuilder);

        Mockito.when(tracer.makeActive(spanCaptor.capture())).thenReturn(activeSpan);

        ActiveSpan extActiveSpan = extSpanBuilder.startActive();

        assertEquals(activeSpan, extActiveSpan);
        assertTrue(spanCaptor.getValue() instanceof APIExtensionsSpan);
    }

}
