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

import org.junit.Test;
import org.mockito.Mockito;

import io.opentracing.noop.NoopTracerFactory;
import io.opentracing.ScopeManager;
import io.opentracing.Span;
import io.opentracing.Tracer;

public class APIExtensionsTracerTest {

    @Test
    public void testActiveSpan() {
        Tracer tracer = Mockito.mock(Tracer.class);
        Span span = Mockito.mock(Span.class);
        Mockito.when(tracer.activeSpan()).thenReturn(span);
        
        APIExtensionsTracer extTracer = new APIExtensionsTracer(tracer);
        assertEquals(span, extTracer.activeSpan());
    }

    @Test
    public void testScopeManager() {
        Tracer tracer = Mockito.mock(Tracer.class);
        ScopeManager scopeManager = Mockito.mock(ScopeManager.class);
        Mockito.when(tracer.scopeManager()).thenReturn(scopeManager);
        
        APIExtensionsTracer extTracer = new APIExtensionsTracer(tracer);
        assertEquals(scopeManager, extTracer.scopeManager());
    }

    @Test
    public void testBuild() {
        Tracer tracer = Mockito.mock(Tracer.class);
        
        APIExtensionsTracer extTracer = new APIExtensionsTracer(tracer);
        assertTrue(extTracer.buildSpan("testop") instanceof APIExtensionsSpanBuilder);
    }

    @Test
    public void testBuildNoopTracer() {
        APIExtensionsTracer extTracer = new APIExtensionsTracer(NoopTracerFactory.create());
        assertTrue(extTracer.buildSpan("testop") instanceof APIExtensionsSpanBuilder);
    }

}
