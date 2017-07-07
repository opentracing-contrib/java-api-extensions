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
     * Notifies the observer that a new span has been started with the supplied data.
     *
     * <p>An observer can either be implemented as stateful or stateless:
     * 
     * <p> * A stateful implementation should return a new instance of a {@link SpanObserver} implementation
     * for each span that is started, allowing this implementation to locally maintain
     * information about that span as subsequent {@link SpanObserver} methods are called. For example,
     * where the observer is interested in a sequence of events associated with the span, such as the
     * recording of a new tag initiating a metric which then needs to be completed/recorded when the
     * span is finished.
     * 
     * <p> * A stateless implementation should return a singleton instance
     * of the {@link SpanObserver}. Each call to the observer will be handled in isolation. For example,
     * this approach would be useful when only interested in a specific event - such as
     * a {@link SpanObserver#onLog onLog} event which can result in the log details being recorded
     * to a logging framework, or {@link SpanObserver#onFinish onFinish}
     * being used to record metrics about the duration of the span.
     * 
     * @param spanData The data for the span that has been started
     * @return The observer for the {@link Span}
     */
    SpanObserver onStart(SpanData spanData);

}
