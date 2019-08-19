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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import io.opentracing.References;
import io.opentracing.Scope;
import io.opentracing.ScopeManager;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.Tracer.SpanBuilder;
import io.opentracing.contrib.api.TracerObserver;

@RunWith(Parameterized.class)
public class APIExtensionsSpanBuilderTest {

    private SpanBuilderFactory spanBuilderFactory;

    @Captor
    private ArgumentCaptor<Span> spanCaptor;
    
    public APIExtensionsSpanBuilderTest(SpanBuilderFactory spanBuilderFactory) {
        this.spanBuilderFactory = spanBuilderFactory;
    }

    @Parameters
    public static Collection<SpanBuilderFactory> factories() {
        return Arrays.<SpanBuilderFactory>asList(new SpanBuilderFactory() {
            @Override
            public SpanBuilder create() {
                return Mockito.mock(SpanBuilder.class);
            }            
        },new SpanBuilderFactory() {
            @Override
            public SpanBuilder create() {
                return null;
            }            
        });
    }

    @Before
    public void init(){
       MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAsChildOfSpanContext() {
        TestResources res = new TestResources();

        res.extSpanBuilder.asChildOf(res.spanContext);
        if (res.spanBuilder != null) {
            Mockito.verify(res.spanBuilder).asChildOf(res.spanContext);
        }
    }

    @Test
    public void testAsChildOfSpan() {
        TestResources res = new TestResources();

        res.extSpanBuilder.asChildOf(res.span);
        if (res.spanBuilder != null) {
            Mockito.verify(res.spanBuilder).asChildOf(res.span);
        }
    }

    @Test
    public void testAddReference() {
        TestResources res = new TestResources();

        res.extSpanBuilder.addReference(References.FOLLOWS_FROM, res.spanContext);
        if (res.spanBuilder != null) {
            Mockito.verify(res.spanBuilder).addReference(References.FOLLOWS_FROM, res.spanContext);
        }
    }

    @Test
    public void testIgnoreActiveSpan() {
        TestResources res = new TestResources();
        
        res.extSpanBuilder.ignoreActiveSpan();
        if (res.spanBuilder != null) {
            Mockito.verify(res.spanBuilder).ignoreActiveSpan();
        }
    }

    @Test
    public void testWithTagString() {
        TestResources res = new TestResources();

        res.extSpanBuilder.withTag("tagName", "tagValue");
        if (res.spanBuilder != null) {
            Mockito.verify(res.spanBuilder).withTag("tagName", "tagValue");
        }
        assertEquals("tagValue", res.extSpanBuilder.tags().get("tagName"));
    }

    @Test
    public void testWithTagNumber() {
        TestResources res = new TestResources();
        
        res.extSpanBuilder.withTag("tagName", 5);
        if (res.spanBuilder != null) {
            Mockito.verify(res.spanBuilder).withTag("tagName", 5);
        }
        assertEquals(5, res.extSpanBuilder.tags().get("tagName"));
    }

    @Test
    public void testWithTagBoolean() {
        TestResources res = new TestResources();
        
        res.extSpanBuilder.withTag("tagName", Boolean.TRUE);
        if (res.spanBuilder != null) {
            Mockito.verify(res.spanBuilder).withTag("tagName", Boolean.TRUE);
        }
        assertEquals(Boolean.TRUE, res.extSpanBuilder.tags().get("tagName"));
    }

    @Test
    public void testWithStartTimestamp() {
        TestResources res = new TestResources();

        // Initially should have value, but will be reset to 0 when timestamp explicitly set
        assertNotEquals(0, res.extSpanBuilder.startTimeNano());

        long ts = TimeUnit.MILLISECONDS.toMicros(System.currentTimeMillis());
        res.extSpanBuilder.withStartTimestamp(ts);
        if (res.spanBuilder != null) {
            Mockito.verify(res.spanBuilder).withStartTimestamp(ts);
        }

        assertEquals(0, res.extSpanBuilder.startTimeNano());
    }

    @Test
    public void testStart() {
        TestResources res = new TestResources();

        Span manualSpan = res.extSpanBuilder.start();
        assertTrue(manualSpan instanceof APIExtensionsSpan);
        
        APIExtensionsSpan extSpan = (APIExtensionsSpan)manualSpan;

        Mockito.verify(res.observer).onStart(extSpan);
        if (res.spanBuilder != null) {
            Mockito.verify(res.spanBuilder).start();
        }
        assertEquals("op", extSpan.getOperationName());
    }

    public class TestResources {
        public Tracer tracer;
        public SpanBuilder spanBuilder;
        public TracerObserver observer;
        public APIExtensionsSpanBuilder extSpanBuilder;
        public SpanContext spanContext;
        public Span span;
        public Scope scope;
        public ScopeManager scopeManager;
        
        public TestResources() {
            tracer = Mockito.mock(Tracer.class);
            spanBuilder = spanBuilderFactory.create();
            observer = Mockito.mock(TracerObserver.class);
            extSpanBuilder = new APIExtensionsSpanBuilder(tracer, Collections.singletonList(observer),
                    "op", spanBuilder);
            spanContext = Mockito.mock(SpanContext.class);
            span = Mockito.mock(Span.class);
            scope = Mockito.mock(Scope.class);
            scopeManager = Mockito.mock(ScopeManager.class);
        }
    }

    public interface SpanBuilderFactory {
        public SpanBuilder create();
    }
}
