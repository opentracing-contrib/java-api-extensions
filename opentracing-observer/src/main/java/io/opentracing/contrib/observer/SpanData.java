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

/**
 * This interface provides information about the current {@link Span} to the observer
 * methods.
 *
 */
public interface SpanData {

    /**
     * This method returns an id that can be used for correlation purposes. It should only
     * be used within the application to uniquely distinguish one span from another,
     * to enable state to be maintained within observer implementation where appropriate.
     *
     * @return The unique id for the span
     */
    Object getSpanId();

    String getOperationName();

    /* Spec does not indicate that a tag key could have multiple values - but some tracers support
     * this? Would be good to understand the usecase for multivalued keys - and add some text in the
     * spec to clarify this?
     * Possibly as well as returning the String,Boolean,Number values, it could return a List in those cases?
     * 
     * Q: Do we need a 'getTags' method - in which case would it return Map<String,List<?>> ?
     * Other option may be to have a Set<String> getTagKeys() ?
     */
    Object getTag(String key);

    Object getBaggageItem(String key);

}
