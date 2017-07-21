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
        TestResources res = new TestResources();

        res.extSpanBuilder.asChildOf(res.spanContext);
        Mockito.verify(res.spanBuilder).asChildOf(res.spanContext);
    }

    @Test
    public void testAsChildOfSpan() {
        TestResources res = new TestResources();

        res.extSpanBuilder.asChildOf(res.span);
        Mockito.verify(res.spanBuilder).asChildOf(res.span);
    }

    @Test
    public void testAddReference() {
        TestResources res = new TestResources();

        res.extSpanBuilder.addReference(References.FOLLOWS_FROM, res.spanContext);
        Mockito.verify(res.spanBuilder).addReference(References.FOLLOWS_FROM, res.spanContext);
    }

    @Test
    public void testIgnoreActiveSpan() {
        TestResources res = new TestResources();
        
        res.extSpanBuilder.ignoreActiveSpan();
        Mockito.verify(res.spanBuilder).ignoreActiveSpan();
    }

    @Test
    public void testWithTagString() {
        TestResources res = new TestResources();

        res.extSpanBuilder.withTag("tagName", "tagValue");
        Mockito.verify(res.spanBuilder).withTag("tagName", "tagValue");
        assertEquals("tagValue", res.extSpanBuilder.tags().get("tagName"));
    }

    @Test
    public void testWithTagNumber() {
        TestResources res = new TestResources();
        
        res.extSpanBuilder.withTag("tagName", 5);
        Mockito.verify(res.spanBuilder).withTag("tagName", 5);
        assertEquals(5, res.extSpanBuilder.tags().get("tagName"));
    }

    @Test
    public void testWithTagBoolean() {
        TestResources res = new TestResources();
        
        res.extSpanBuilder.withTag("tagName", Boolean.TRUE);
        Mockito.verify(res.spanBuilder).withTag("tagName", Boolean.TRUE);
        assertEquals(Boolean.TRUE, res.extSpanBuilder.tags().get("tagName"));
    }

    @Test
    public void testWithStartTimestamp() {
        TestResources res = new TestResources();

        // Initially should have value, but will be reset to 0 when timestamp explicitly set
        assertNotEquals(0, res.extSpanBuilder.startTimeNano());

        long ts = TimeUnit.MILLISECONDS.toMicros(System.currentTimeMillis());
        res.extSpanBuilder.withStartTimestamp(ts);
        Mockito.verify(res.spanBuilder).withStartTimestamp(ts);

        assertEquals(0, res.extSpanBuilder.startTimeNano());
    }

    @Test
    public void testStartManual() {
        TestResources res = new TestResources();

        Span manualSpan = res.extSpanBuilder.startManual();
        assertTrue(manualSpan instanceof APIExtensionsSpan);
        
        APIExtensionsSpan extSpan = (APIExtensionsSpan)manualSpan;

        Mockito.verify(res.observer).onStart(extSpan);
        Mockito.verify(res.spanBuilder).startManual();
        assertEquals("op", extSpan.getOperationName());
    }

    @Test
    public void testStartActive() {
        TestResources res = new TestResources();

        Mockito.when(res.tracer.makeActive(spanCaptor.capture())).thenReturn(res.activeSpan);

        ActiveSpan extActiveSpan = res.extSpanBuilder.startActive();

        assertEquals(res.activeSpan, extActiveSpan);
        assertTrue(spanCaptor.getValue() instanceof APIExtensionsSpan);
    }

    public class TestResources {
        public Tracer tracer;
        public SpanBuilder spanBuilder;
        public TracerObserver observer;
        public APIExtensionsSpanBuilder extSpanBuilder;
        public SpanContext spanContext;
        public Span span;
        public ActiveSpan activeSpan;
        
        public TestResources() {
            tracer = Mockito.mock(Tracer.class);
            spanBuilder = Mockito.mock(SpanBuilder.class);
            observer = Mockito.mock(TracerObserver.class);
            extSpanBuilder = new APIExtensionsSpanBuilder(tracer, Collections.singletonList(observer),
                    "op", spanBuilder);
            spanContext = Mockito.mock(SpanContext.class);
            span = Mockito.mock(Span.class);
            activeSpan = Mockito.mock(ActiveSpan.class);
        }
    }
}
