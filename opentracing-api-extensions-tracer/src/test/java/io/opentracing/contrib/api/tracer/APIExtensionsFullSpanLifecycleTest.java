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

import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import io.opentracing.noop.NoopTracerFactory;
import io.opentracing.contrib.api.SpanData;
import io.opentracing.contrib.api.SpanObserver;
import io.opentracing.contrib.api.TracerObserver;
import io.opentracing.mock.MockTracer;

public class APIExtensionsFullSpanLifecycleTest {

    @Test
    public void testMockTracerManualSpan() {
        APIExtensionsTracer extTracer = new APIExtensionsTracer(new MockTracer());
        TracerObserver tracerObserver = Mockito.mock(TracerObserver.class);
        SpanObserver spanObserver = Mockito.mock(SpanObserver.class);
        Mockito.when(tracerObserver.onStart(Matchers.any(SpanData.class))).thenReturn(spanObserver);
        extTracer.addTracerObserver(tracerObserver);

        extTracer.buildSpan("testOp").start().finish();
        
        Mockito.verify(spanObserver, Mockito.times(1)).onFinish(Matchers.any(SpanData.class), Matchers.anyLong());
    }

    @Test
    public void testNoopTracerManualSpan() {
        APIExtensionsTracer extTracer = new APIExtensionsTracer(NoopTracerFactory.create());
        TracerObserver tracerObserver = Mockito.mock(TracerObserver.class);
        SpanObserver spanObserver = Mockito.mock(SpanObserver.class);
        Mockito.when(tracerObserver.onStart(Matchers.any(SpanData.class))).thenReturn(spanObserver);
        extTracer.addTracerObserver(tracerObserver);

        extTracer.buildSpan("testOp").start().finish();
        
        Mockito.verify(spanObserver, Mockito.times(1)).onFinish(Matchers.any(SpanData.class), Matchers.anyLong());
    }

}
