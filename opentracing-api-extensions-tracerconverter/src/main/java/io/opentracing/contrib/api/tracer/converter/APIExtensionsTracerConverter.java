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

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import io.opentracing.Tracer;
import io.opentracing.contrib.api.TracerObserver;
import io.opentracing.contrib.api.tracer.APIExtensionsTracer;
import io.opentracing.contrib.tracerresolver.TracerConverter;

/**
 * This implementation of the {@link TracerConverter} interface is used to create an
 * {@link APIExtensionsTracer} wrapper, if one or more {@link TracerObserver} implementation can
 * be resolved using service loadable {@link TracerObserverResolver}s.
 *
 */
public class APIExtensionsTracerConverter implements TracerConverter {

    @Override
    public Tracer convert(Tracer tracer) {
        List<TracerObserver> observers = getObservers();
        if (!observers.isEmpty()) {
            APIExtensionsTracer extTracer = new APIExtensionsTracer(tracer);
            for (TracerObserver observer : observers) {
                extTracer.addTracerObserver(observer);
            }
            tracer = extTracer;
        }
        return tracer;
    }

    List<TracerObserver> getObservers() {
        List<TracerObserver> observers = new ArrayList<TracerObserver>();
        ServiceLoader<TracerObserverResolver> resolvers = ServiceLoader.load(TracerObserverResolver.class,
                Thread.currentThread().getContextClassLoader());
        for (TracerObserverResolver resolver : resolvers) {
            TracerObserver observer = resolver.resolve();
            if (observer != null) {
                observers.add(observer);
            }
        }
        return observers;
    }
}
