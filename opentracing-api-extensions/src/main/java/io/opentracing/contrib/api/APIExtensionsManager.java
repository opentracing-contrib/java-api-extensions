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
package io.opentracing.contrib.api;

/**
 * This interface represents a component that manages API extensions.
 *
 */
public interface APIExtensionsManager {

    /**
     * This method adds a {@link TracerObserver} to the API extensions manager.
     *
     * @param observer The {@link TracerObserver} instance
     */
    void addTracerObserver(TracerObserver observer);

    /**
     * This method removes a {@link TracerObserver} from the API extensions manager.
     *
     * @param observer The {@link TracerObserver} instance
     */
    void removeTracerObserver(TracerObserver observer);

}
