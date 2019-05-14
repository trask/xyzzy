/*
 * Copyright 2014-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.glowroot.instrumentation.engine.impl;

import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.annotations.VisibleForTesting;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.immutables.value.Value;
import org.immutables.value.Value.Style;

import org.glowroot.instrumentation.api.TimerName;

@Value.Immutable
@Style(allParameters = true)
public abstract class TimerNameImpl implements TimerName {

    private static final AtomicInteger nextSpecialHashCode = new AtomicInteger();

    @VisibleForTesting
    public abstract String name();

    public abstract boolean extended();

    @Value.Derived
    public @Nullable TimerNameImpl extendedTimer() {
        if (extended()) {
            return null;
        }
        return ImmutableTimerNameImpl.of(name(), true);
    }

    @Value.Derived
    public int specialHashCode() {
        return nextSpecialHashCode.getAndIncrement();
    }
}
