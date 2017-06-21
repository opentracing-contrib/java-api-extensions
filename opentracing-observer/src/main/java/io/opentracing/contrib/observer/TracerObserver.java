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

/**
 * This interface represents an observer used to receive notifications related to {@link Span}s.
 *
 */
public interface TracerObserver {

    /**
     * Notifies the observer that a new span has been started with the supplied details.
     *
     * @param spanData The data for the span being started
     * @param startMicros The start time in microseconds
     * @param operationName The operation name
     * @return The observer for the {@link Span}
     */
    SpanObserver onStart(SpanData spanData, long startMicros, String operationName);

}
