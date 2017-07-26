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
package io.opentracing.contrib.api.tracer.converter;

import java.util.ServiceLoader;

import io.opentracing.contrib.api.TracerObserver;

/**
 * This interface can be implemented to enable a {@link TracerObserver} implementation to be resolved
 * using the {@link ServiceLoader} mechanism.
 */
public interface TracerObserverResolver {

    /**
     * This method attempts to resolve a {@link TracerObserver} implementation.
     *
     * @return The tracer observer, or null if not found
     */
    TracerObserver resolve();

}
