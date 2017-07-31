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
package io.opentracing.contrib.api.tracer.decorator;

import io.opentracing.Tracer;
import io.opentracing.contrib.tracerresolver.TracerResolver;

public class TestTracerResolver extends TracerResolver {

    private static Tracer tracer;

    public static void setTracer(Tracer t) {
        tracer = t;
    }

    public static Tracer getTracer() {
        return tracer;
    }

    @Override
    protected Tracer resolve() {
        return tracer;
    }

}
