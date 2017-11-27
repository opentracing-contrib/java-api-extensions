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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import io.opentracing.ActiveSpan;
import io.opentracing.ActiveSpanSource;
import io.opentracing.NoopTracer;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.contrib.api.APIExtensionsManager;
import io.opentracing.contrib.api.TracerObserver;
import io.opentracing.propagation.Format;
import io.opentracing.util.ThreadLocalActiveSpanSource;

public class APIExtensionsTracer implements Tracer, APIExtensionsManager {

    private final Tracer wrappedTracer;
    private final List<TracerObserver> observers = new CopyOnWriteArrayList<TracerObserver>();
    private final ActiveSpanSource activeSpanSource;

    public APIExtensionsTracer(Tracer tracer) {
        if (tracer instanceof NoopTracer) {
            this.activeSpanSource = new ThreadLocalActiveSpanSource();
            this.wrappedTracer = null;
        } else {
            this.activeSpanSource = null;
            this.wrappedTracer = tracer;
        }
    }

    @Override
    public void addTracerObserver(TracerObserver observer) {
        if (observer != null) {
            observers.add(observer);
        }
    }

    @Override
    public void removeTracerObserver(TracerObserver observer) {
        if (observer != null) {
            observers.remove(observer);
        }
    }

    @Override
    public ActiveSpan activeSpan() {
        if (wrappedTracer != null) {
            return wrappedTracer.activeSpan();
        }
        return activeSpanSource.activeSpan();
    }

    @Override
    public ActiveSpan makeActive(Span span) {
        if (wrappedTracer != null) {
            return wrappedTracer.makeActive(span);
        }
        return activeSpanSource.makeActive(span);
    }

    @Override
    public SpanBuilder buildSpan(String operation) {
        return new APIExtensionsSpanBuilder(this, observers, operation,
                (wrappedTracer == null ? null : wrappedTracer.buildSpan(operation)));
    }

    @Override
    public <C> SpanContext extract(Format<C> format, C carrier) {
        if (wrappedTracer != null) {
            return wrappedTracer.extract(format, carrier);
        }
        return null;
    }

    @Override
    public <C> void inject(SpanContext context, Format<C> format, C carrier) {
        if (wrappedTracer != null) {
            wrappedTracer.inject(context, format, carrier);
        }
    }

}
