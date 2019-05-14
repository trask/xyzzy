/*
 * Copyright 2019 the original author or authors.
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
package org.glowroot.instrumentation.test.harness.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

import org.checkerframework.checker.nullness.qual.Nullable;

class ObjectInputStreamWithClassLoader extends ObjectInputStream {

    private final @Nullable ClassLoader loader;

    ObjectInputStreamWithClassLoader(InputStream in, @Nullable ClassLoader loader)
            throws IOException {
        super(in);
        this.loader = loader;
    }

    @Override
    protected Class<?> resolveClass(ObjectStreamClass desc)
            throws IOException, ClassNotFoundException {
        try {
            return Class.forName(desc.getName(), false, loader);
        } catch (ClassNotFoundException ex) {
            return super.resolveClass(desc);
        }
    }
}
