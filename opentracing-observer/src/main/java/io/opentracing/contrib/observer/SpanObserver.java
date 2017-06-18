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
 * This interface represents an observer used to receive notifications related to a {@link Span}.
 *
 */
public interface SpanObserver {

    /**
     * Notifies the observer that the operation name has been changed.
     *
     * @param operationName The new operation name
     */
    void onSetOperationName(String operationName);

    /**
     * Notifies the observer that the named tag has been set/changed.
     *
     * @param name The tag name
     * @param value The tag value
     */
    void onSetTag(String name, Object value);

    /**
     * Notifies the observer that the named baggage item has been set/changed.
     *
     * @param key The baggage key
     * @param value The baggage value
     */
    void onSetBaggageItem(String key, String value);

    /**
     * Notifies the observer that a log event has been recorded.
     *
     * @param timestampMicroseconds The explicit timestamp for the log record. Must be greater than or equal to the
     *                              Span's start timestamp.
     * @param fields key:value log fields. Tracer implementations should support String, numeric, and boolean values;
     *               some may also support arbitrary Objects.
     */
    void onLog(long timestampMicroseconds, Map<String, ?> fields);

    /**
     * Notifies the observer that a log event has been recorded.
     *
     * @param timestampMicroseconds The explicit timestamp for the log record. Must be greater than or equal to the
     *                              Span's start timestamp.
     * @param event the event value; often a stable identifier for a moment in the Span lifecycle
     */
    void onLog(long timestampMicroseconds, String event);

    /**
     * Notifies the observer that the associated {@link Span} has finished.
     *
     * @param finishMicros The finish time in microseconds
     */
    void onFinish(long finishMicros);

}
