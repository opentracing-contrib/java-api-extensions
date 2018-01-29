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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import io.opentracing.noop.NoopSpanContext;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.contrib.api.SpanData;
import io.opentracing.contrib.api.SpanObserver;

public class APIExtensionsSpan implements Span, SpanData  {

    private final Span wrappedSpan;

    private String operationName;
    private final long startTimestampMicro;
    private long finishTimestampMicro;
    private final long startTimeNano;
    private long finishTimeNano;
    private final Map<String,Object> tags;

    private final List<SpanObserver> observers = new CopyOnWriteArrayList<SpanObserver>();

    private final UUID correlationId = UUID.randomUUID();

    /**
     * This is the constructor for the extensions API span wrapper.
     *
     * @param span The span being wrapped
     * @param operationName The operation name
     * @param startTimestampMicro The start timestamp (microseconds)
     * @param startTimeNano The start nano time, or 0 if the start timestamp was explicitly provided by the app
     * @param tags The initial tags
     */
    APIExtensionsSpan(Span span, String operationName,
            long startTimestampMicro, long startTimeNano, Map<String,Object> tags) {
        this.wrappedSpan = span;
        this.operationName = operationName;
        this.startTimestampMicro = startTimestampMicro;
        this.startTimeNano = startTimeNano;
        this.tags = tags;
    }

    /**
     * This method adds a new {@link SpanObserver}.
     *
     * @param observer The observer
     */
    public void addSpanObserver(SpanObserver observer) {
        if (observer != null) {
            observers.add(observer);
        }
    }

    /**
     * This method removes a {@link SpanObserver}.
     *
     * @param observer The observer
     */
    public void removeSpanObserver(SpanObserver observer) {
        if (observer != null) {
            observers.remove(observer);
        }
    }

    @Override
    public SpanContext context() {
        if (wrappedSpan != null) {
            return wrappedSpan.context();
        }
        return SpanContextImpl.INSTANCE;
    }

    @Override
    public Object getCorrelationId() {
        return correlationId;
    }

    @Override
    public long getStartTime() {
        return startTimestampMicro;
    }

    @Override
    public long getFinishTime() {
        return finishTimestampMicro;
    }

    @Override
    public Span setOperationName(String operationName) {
        if (wrappedSpan != null) {
            wrappedSpan.setOperationName(operationName);
        }
        this.operationName = operationName;
        for (SpanObserver observer : observers) {
            observer.onSetOperationName(this, operationName);
        }
        return this;
    }

    @Override
    public String getOperationName() {
        return operationName;
    }

    @Override
    public String getBaggageItem(String name) {
        if (wrappedSpan != null) {
            return wrappedSpan.getBaggageItem(name);
        }
        return null;
    }

    @Override
    public Span setBaggageItem(String name, String value) {
        if (wrappedSpan != null) {
            wrappedSpan.setBaggageItem(name, value);
        }
        for (SpanObserver observer : observers) {
            observer.onSetBaggageItem(this, name, value);
        }
        return this;
    }

    @Override
    public Span log(Map<String, ?> fields) {
        if (wrappedSpan != null) {
            wrappedSpan.log(fields);
        }
        return handleLog(TimeUnit.MILLISECONDS.toMicros(System.currentTimeMillis()), fields);
    }

    @Override
    public Span log(long timestampMicroseconds, Map<String, ?> fields) {
        if (wrappedSpan != null) {
            wrappedSpan.log(timestampMicroseconds, fields);
        }
        return handleLog(timestampMicroseconds, fields);
    }

    private Span handleLog(long timestampMicroseconds, Map<String, ?> fields) {
        for (SpanObserver observer : observers) {
            observer.onLog(this, timestampMicroseconds, fields);
        }
        return this;
    }

    @Override
    public Span log(String event) {
        if (wrappedSpan != null) {
            wrappedSpan.log(event);
        }
        return handleLog(TimeUnit.MILLISECONDS.toMicros(System.currentTimeMillis()), event);
    }

    @Override
    public Span log(long timestampMicroseconds, String event) {
        if (wrappedSpan != null) {
            wrappedSpan.log(timestampMicroseconds, event);
        }
        return handleLog(timestampMicroseconds, event);
    }

    private Span handleLog(long timestampMicroseconds, String event) {
        for (SpanObserver observer : observers) {
            observer.onLog(this, timestampMicroseconds, event);
        }
        return this;
    }

    @Override
    public Span setTag(String key, String value) {
        if (wrappedSpan != null) {
            wrappedSpan.setTag(key, value);
        }
        return handleSetTag(key, value);
    }
    
    @Override
    public Span setTag(String key, boolean value) {
        if (wrappedSpan != null) {
            wrappedSpan.setTag(key, value);
        }
        return handleSetTag(key, value);
    }

    @Override
    public Span setTag(String key, Number value) {
        if (wrappedSpan != null) {
            wrappedSpan.setTag(key, value);
        }
        return handleSetTag(key, value);
    }

    private Span handleSetTag(String key, Object value) {
        tags.put(key, value);
        for (SpanObserver observer : observers) {
            observer.onSetTag(this, key, value);
        }
        return this;
    }

    @Override
    public Map<String,Object> getTags() {
        return Collections.unmodifiableMap(tags);
    }

    @Override
    public String getStringTag(String key) {
        Object value = tags.get(key);
        if (value instanceof String) {
            return (String) value;
        }
        return null;
    }

    @Override
    public Number getNumberTag(String key) {
        Object value = tags.get(key);
        if (value instanceof Number) {
            return (Number) value;
        }
        return null;
    }

    @Override
    public Boolean getBooleanTag(String key) {
        Object value = tags.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return null;
    }

    @Override
    public void finish() {
        if (wrappedSpan != null) {
            wrappedSpan.finish();
        }
        // Only set the finish nano time if not explicitly providing a timestamp
        finishTimeNano = System.nanoTime();
        handleFinish(TimeUnit.MILLISECONDS.toMicros(System.currentTimeMillis()));
    }

    @Override
    public void finish(long finishMicros) {
        if (wrappedSpan != null) {
            wrappedSpan.finish(finishMicros);
        }
        handleFinish(finishMicros);
    }

    private void handleFinish(long finishMicros) {
        finishTimestampMicro = finishMicros;
        for (SpanObserver observer : observers) {
            observer.onFinish(this, finishMicros);
        }
    }

    @Override
    public long getDuration() {
        // If start or finish nano times are not available, then use timestamps
        if (startTimeNano == 0 || finishTimeNano == 0) {
            return finishTimestampMicro == 0 ? 0 : finishTimestampMicro - startTimestampMicro;
        }
        return TimeUnit.NANOSECONDS.toMicros(finishTimeNano - startTimeNano);
    }

    static class SpanContextImpl implements NoopSpanContext {
        static final SpanContextImpl INSTANCE = new SpanContextImpl();

        @Override
        public Iterable<Map.Entry<String, String>> baggageItems() {
            return Collections.emptyList();
        }

        @Override
        public String toString() { return SpanContext.class.getSimpleName(); }

    }
}
