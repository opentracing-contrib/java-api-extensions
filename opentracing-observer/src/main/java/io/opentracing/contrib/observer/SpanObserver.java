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
package io.opentracing.contrib.observer;

import java.util.Map;

/**
 * This interface represents an observer used to receive notifications related to a {@link io.opentracing.Span}.
 * <p>
 * Note: All of these callback functions are called after the associated operation has been performed on the
 * {@link io.opentracing.Span}, i.e. they are post-change notifications.
 */
public interface SpanObserver {

    /**
     * Notifies the observer that the operation name has been changed.
     *
     * @param spanData The data for the span
     * @param operationName The new operation name
     */
    void onSetOperationName(SpanData spanData, String operationName);

    /**
     * Notifies the observer that the tag with the supplied key has been set or updated
     * on a {@link io.opentracing.Span}.
     *
     * @param spanData The data for the span
     * @param key The tag key
     * @param value The tag value
     */
    void onSetTag(SpanData spanData, String key, Object value);

    /**
     * Notifies the observer that the named baggage item has been set/changed.
     *
     * @param spanData The data for the span
     * @param key The baggage key
     * @param value The baggage value
     */
    void onSetBaggageItem(SpanData spanData, String key, String value);

    /**
     * Notifies the observer that a log event has been recorded.
     *
     * @param spanData The data for the span
     * @param timestampMicroseconds The explicit timestamp for the log record. Must be greater than or equal to the
     *                              Span's start timestamp.
     * @param fields key:value log fields. Tracer implementations should support String, numeric, and boolean values;
     *               some may also support arbitrary Objects.
     */
    void onLog(SpanData spanData, long timestampMicroseconds, Map<String, ?> fields);

    /**
     * Notifies the observer that a log event has been recorded.
     *
     * @param spanData The data for the span
     * @param timestampMicroseconds The explicit timestamp for the log record. Must be greater than or equal to the
     *                              Span's start timestamp.
     * @param event the event value; often a stable identifier for a moment in the Span lifecycle
     */
    void onLog(SpanData spanData, long timestampMicroseconds, String event);

    /**
     * Notifies the observer that a span associated with the supplied data has finished.
     *
     * @param spanData The data for the span
     * @param finishMicros The finish time in microseconds
     */
    void onFinish(SpanData spanData, long finishMicros);

}
