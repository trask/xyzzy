/*
 * Copyright 2018-2019 the original author or authors.
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
package org.glowroot.instrumentation.engine.weaving;

import org.junit.Before;
import org.junit.Test;

import org.glowroot.instrumentation.engine.weaving.SomeInstrumentation.BasicAdvice;
import org.glowroot.instrumentation.engine.weaving.SomeInstrumentation.IterableAdvice;
import org.glowroot.instrumentation.engine.weaving.targets.DefaultMethodAbstractNotIterable.ExtendsDefaultMethodAbstractNotIterable;
import org.glowroot.instrumentation.engine.weaving.targets.DefaultMethodAbstractNotMisc2.ExtendsDefaultMethodAbstractNotMisc2;
import org.glowroot.instrumentation.engine.weaving.targets.DefaultMethodMiscBridge;
import org.glowroot.instrumentation.engine.weaving.targets.DefaultMethodMiscBridge2;
import org.glowroot.instrumentation.engine.weaving.targets.DefaultMethodMiscImpl;
import org.glowroot.instrumentation.engine.weaving.targets.DefaultMethodSubMiscImpl;

import static org.assertj.core.api.Assertions.assertThat;

public class Weaver8Test {

    @Before
    public void before() {
        SomeInstrumentationThreadLocals.resetThreadLocals();
    }

    // ===================== default methods =====================

    @Test
    public void shouldExecuteDefaultMethodAdvice() throws Exception {
        // given
        DefaultMethodMiscBridge test = WeaverTest.newWovenObject(DefaultMethodMiscImpl.class,
                DefaultMethodMiscBridge.class, BasicAdvice.class);
        // when
        test.execute1();
        // then
        assertThat(SomeInstrumentationThreadLocals.onBeforeCount.get()).isEqualTo(1);
        assertThat(SomeInstrumentationThreadLocals.onReturnCount.get()).isEqualTo(1);
        assertThat(SomeInstrumentationThreadLocals.onThrowCount.get()).isEqualTo(0);
        assertThat(SomeInstrumentationThreadLocals.onAfterCount.get()).isEqualTo(1);
    }

    @Test
    public void shouldExecuteDefaultMethodAdvice2() throws Exception {
        // given
        DefaultMethodMiscBridge test = WeaverTest.newWovenObject(DefaultMethodSubMiscImpl.class,
                DefaultMethodMiscBridge.class, BasicAdvice.class);
        // when
        test.execute1();
        // then
        assertThat(SomeInstrumentationThreadLocals.onBeforeCount.get()).isEqualTo(1);
        assertThat(SomeInstrumentationThreadLocals.onReturnCount.get()).isEqualTo(1);
        assertThat(SomeInstrumentationThreadLocals.onThrowCount.get()).isEqualTo(0);
        assertThat(SomeInstrumentationThreadLocals.onAfterCount.get()).isEqualTo(1);
    }

    // the three tests below document less than ideal behavior around weaving default methods

    @Test
    public void shouldNotWeaveDefaultMethodAdvice() throws Exception {
        // given
        DefaultMethodMiscBridge2 test =
                WeaverTest.newWovenObject(ExtendsDefaultMethodAbstractNotMisc2.class,
                        DefaultMethodMiscBridge2.class, BasicAdvice.class);
        // when
        test.execute2();
        // then
        assertThat(SomeInstrumentationThreadLocals.onBeforeCount.get()).isEqualTo(0);
        assertThat(SomeInstrumentationThreadLocals.onReturnCount.get()).isEqualTo(0);
        assertThat(SomeInstrumentationThreadLocals.onThrowCount.get()).isEqualTo(0);
        assertThat(SomeInstrumentationThreadLocals.onAfterCount.get()).isEqualTo(0);
    }

    @Test
    public void shouldWeaveIterableNonDefaultMethod() throws Exception {
        // given
        Iterable<?> test = WeaverTest.newWovenObject(ExtendsDefaultMethodAbstractNotIterable.class,
                Iterable.class, IterableAdvice.class);
        // when
        test.iterator();
        // then
        assertThat(SomeInstrumentationThreadLocals.onBeforeCount.get()).isEqualTo(1);
        assertThat(SomeInstrumentationThreadLocals.onReturnCount.get()).isEqualTo(1);
        assertThat(SomeInstrumentationThreadLocals.onThrowCount.get()).isEqualTo(0);
        assertThat(SomeInstrumentationThreadLocals.onAfterCount.get()).isEqualTo(1);
    }

    @Test
    public void shouldNotWeaveIterableDefaultMethod() throws Exception {
        // given
        Iterable<?> test = WeaverTest.newWovenObject(ExtendsDefaultMethodAbstractNotIterable.class,
                Iterable.class, IterableAdvice.class);
        // when
        test.spliterator();
        // then
        assertThat(SomeInstrumentationThreadLocals.onBeforeCount.get()).isEqualTo(0);
        assertThat(SomeInstrumentationThreadLocals.onReturnCount.get()).isEqualTo(0);
        assertThat(SomeInstrumentationThreadLocals.onThrowCount.get()).isEqualTo(0);
        assertThat(SomeInstrumentationThreadLocals.onAfterCount.get()).isEqualTo(0);
    }
}
