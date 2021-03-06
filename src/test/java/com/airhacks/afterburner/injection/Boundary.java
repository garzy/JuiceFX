package com.airhacks.afterburner.injection;

/*
 * #%L
 * afterburner.fx
 * %%
 * Copyright (C) 2013 Adam Bien
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Singleton;

/**
 *
 * @author adam-bien.com
 */
@Singleton
public class Boundary {

    static AtomicInteger INSTANCE_COUNT = new AtomicInteger(0);

    public Boundary() {
        INSTANCE_COUNT.incrementAndGet();
    }

    public int getNumberOfInstances() {
        return INSTANCE_COUNT.get();
    }
}
