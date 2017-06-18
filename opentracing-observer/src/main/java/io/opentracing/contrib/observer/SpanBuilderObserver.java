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

import io.opentracing.Span;
import io.opentracing.SpanContext;

/**
 * This interface represents an observer used to receive notifications related to a {@link SpanBuilder}.
 *
 */
public interface SpanBuilderObserver {

    /**
     * Notifies observer that the named tag has been set/changed.
     *
     * @param name The tag name
     * @param value The tag value
     */
    void onWithTag(String name, Object value);

    /**
     * Notifies observer that a reference has been added of the specified
     * type.
     *
     * @param referenceType The reference type
     * @param referencedContext The referenced span context
     */
    void onAddReference(String referenceType, SpanContext referencedContext);

    /**
     * Notifies the observer that the {@link Span} has been started.
     *
     * @param span The started span
     * @param startMicros The start time in microseconds
     * @return The observer for the span
     */
    SpanObserver onStart(Span span, long startMicros);

}
