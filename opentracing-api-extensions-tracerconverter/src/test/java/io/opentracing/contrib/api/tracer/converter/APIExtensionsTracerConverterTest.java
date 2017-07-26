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
package io.opentracing.contrib.api.tracer.converter;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import io.opentracing.NoopTracerFactory;
import io.opentracing.Tracer;
import io.opentracing.contrib.api.SpanData;
import io.opentracing.contrib.api.TracerObserver;
import io.opentracing.contrib.api.tracer.APIExtensionsTracer;
import io.opentracing.contrib.tracerresolver.TracerResolver;

public class APIExtensionsTracerConverterTest {

    @Test
    public void testTracerResolverNoObservers() {
        TestTracerResolver.setTracer(NoopTracerFactory.create());
        TestTracerObserverResolver.setTracerObserver(null);
        assertEquals(TestTracerResolver.getTracer(), TracerResolver.resolveTracer());
    }

    @Test
    public void testTracerResolverWithObservers() {
        TracerObserver observer = Mockito.mock(TracerObserver.class);

        TestTracerResolver.setTracer(NoopTracerFactory.create());
        TestTracerObserverResolver.setTracerObserver(observer);

        Tracer tracer = TracerResolver.resolveTracer();
        assertEquals(APIExtensionsTracer.class, tracer.getClass());

        tracer.buildSpan("testop").startManual();

        Mockito.verify(observer).onStart(Matchers.any(SpanData.class));
    }

}
