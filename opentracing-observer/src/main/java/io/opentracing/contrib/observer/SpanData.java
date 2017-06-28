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
 * This interface provides information about the current {@link io.opentracing.Span} to the observer
 * methods.
 *
 */
public interface SpanData {

    /**
     * This method returns an id that can be used for correlate actions invoked on a
     * stateful observer. It should only be used within the scope of an application,
     * to uniquely distinguish one span from another, to enable state to be maintained
     * within observer implementation where appropriate.
     *
     * @return The correlation id for the span, MUST implement equals/hashCode to enable
     *              it to be used as a map key
     */
    Object getCorrelationId();

    /**
     * The start time of the {@link io.opentracing.Span}.
     *
     * @return The start time (in microseconds)
     */
    long getStartTime();

    /**
     * The finish time of the {@link io.opentracing.Span}.
     *
     * @return The finish time (in microseconds), or 0 if not available
     */
    long getFinishTime();

    /**
     * The duration of the {@link io.opentracing.Span}.
     *
     * @return The duration (in microseconds), or 0 if the span is not finished
     */
    long getDuration();

    /**
     * The operation name of the {@link io.opentracing.Span}.
     *
     * @return The operation name
     */
    String getOperationName();

    /**
     * This method provides access to the tags associated with the span.
     *
     * @return The tags
     */
    Map<String,Object> getTags();

    /**
     * This method retrieves a baggage item associated with the supplied key.
     *
     * @param key The key
     * @return The baggage item, or null if undefined
     */
    Object getBaggageItem(String key);

}
