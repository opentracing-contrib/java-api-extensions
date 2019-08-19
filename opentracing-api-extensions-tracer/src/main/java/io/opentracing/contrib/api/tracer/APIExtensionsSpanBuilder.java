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

import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.Tracer.SpanBuilder;
import io.opentracing.contrib.api.TracerObserver;
import io.opentracing.tag.Tag;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class APIExtensionsSpanBuilder implements SpanBuilder {

    private final Tracer tracer;
    private final List<TracerObserver> observers;

    private final String operationName;
    private final SpanBuilder wrappedBuilder;
    private long startTimestampMicro = TimeUnit.MILLISECONDS.toMicros(System.currentTimeMillis());
    private long startTimeNano = System.nanoTime();
    private final Map<String,Object> tags = new ConcurrentHashMap<String,Object>();

    APIExtensionsSpanBuilder(Tracer tracer, List<TracerObserver> observers,
            String operationName, SpanBuilder builder) {
        this.tracer = tracer;
        this.observers = observers;
        this.operationName = operationName;
        this.wrappedBuilder = builder;
    }

    @Override
    public SpanBuilder asChildOf(SpanContext parent) {
        if (wrappedBuilder != null) {
            wrappedBuilder.asChildOf(parent);
        }
        return this;
    }

    @Override
    public SpanBuilder asChildOf(Span parent) {
        if (wrappedBuilder != null) {
            wrappedBuilder.asChildOf(parent);
        }
        return this;
    }

    @Override
    public SpanBuilder addReference(String referenceType, SpanContext referencedContext) {
        if (wrappedBuilder != null) {
            wrappedBuilder.addReference(referenceType, referencedContext);
        }
        return this;
    }

    @Override
    public SpanBuilder ignoreActiveSpan() {
        if (wrappedBuilder != null) {
            wrappedBuilder.ignoreActiveSpan();
        }
        return this;
    }

    @Override
    public SpanBuilder withTag(String key, String value) {
        tags.put(key, value);
        if (wrappedBuilder != null) {
            wrappedBuilder.withTag(key, value);
        }
        return this;
    }

    @Override
    public SpanBuilder withTag(String key, boolean value) {
        tags.put(key, value);
        if (wrappedBuilder != null) {
            wrappedBuilder.withTag(key, value);
        }
        return this;
    }

    @Override
    public SpanBuilder withTag(String key, Number value) {
        tags.put(key, value);
        if (wrappedBuilder != null) {
            wrappedBuilder.withTag(key, value);
        }
        return this;
    }

    @Override
    public <T> SpanBuilder withTag(Tag<T> tag, T value) {
        tags.put(tag.getKey(), value);
        if (wrappedBuilder != null) {
            wrappedBuilder.withTag(tag, value);
        }
        return this;
    }

    @Override
    public SpanBuilder withStartTimestamp(long microseconds) {
        if (wrappedBuilder != null) {
            wrappedBuilder.withStartTimestamp(microseconds);
        }
        // Reset the nano start time, so that duration will be calculated based on explicitly
        // provided timestamps
        this.startTimeNano = 0;
        return this;
    }

    @Override
    public Span start() {
        APIExtensionsSpan span = new APIExtensionsSpan(
                (wrappedBuilder == null ? null : wrappedBuilder.start()),
                operationName, startTimestampMicro, startTimeNano, tags);
        for (TracerObserver observer : observers) {
            span.addSpanObserver(observer.onStart(span));
        }
        return span;
    }

    public Map<String, Object> tags() {
        return Collections.unmodifiableMap(tags);
    }

    long startTimeNano() {
        return startTimeNano;
    }

}
