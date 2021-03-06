/**
 * Copyright 2017-2018 the original author or authors.
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
package org.glowroot.instrumentation.test.matrix;

import static org.glowroot.instrumentation.test.matrix.JavaVersion.JAVA6;
import static org.glowroot.instrumentation.test.matrix.JavaVersion.JAVA7;
import static org.glowroot.instrumentation.test.matrix.JavaVersion.JAVA8;

public class JAXWS {

    private static final String MODULE_PATH = "instrumentation/jaxws";

    public static void main(String[] args) throws Exception {
        if (args.length == 1 && args[0].equals("short")) {
            runShort();
        } else {
            runAll();
        }
    }

    static void runShort() throws Exception {
        run2x("2.1.1", "2.0.8");
        run3x("3.0.0", "4.3.14.RELEASE");
        run3xJava8("3.2.0", "4.3.14.RELEASE");
    }

    static void runAll() throws Exception {
        // see cxf-parent pom.xml for supported spring version range
        for (int i = 1; i <= 10; i++) {
            run2x("2.1." + i, "2.0.8");
        }
        for (int i = 1; i <= 12; i++) {
            run2x("2.2." + i, "2.5.6");
        }
        for (int i = 0; i <= 11; i++) {
            run2x("2.3." + i, "3.0.5.RELEASE");
        }
        for (int i = 0; i <= 10; i++) {
            run2x("2.4." + i, "3.0.6.RELEASE");
        }
        for (int i = 0; i <= 11; i++) {
            run2x("2.5." + i, "3.0.6.RELEASE");
        }
        for (int i = 0; i <= 17; i++) {
            run2x("2.6." + i, "3.2.18.RELEASE");
        }
        for (int i = 0; i <= 18; i++) {
            run2x("2.7." + i, "3.2.18.RELEASE");
        }
        for (int i = 0; i <= 15; i++) {
            run3x("3.0." + i, "4.3.14.RELEASE");
        }
        for (int i = 0; i <= 16; i++) {
            run3x("3.1." + i, "4.3.14.RELEASE");
        }
        for (int i = 0; i <= 4; i++) {
            run3xJava8("3.2." + i, "4.3.14.RELEASE");
        }
    }

    private static void run2x(String cxfVersion, String springVersion) throws Exception {
        Util.updateLibVersion(MODULE_PATH, "cxf.version", cxfVersion);
        Util.updateLibVersion(MODULE_PATH, "spring.version", springVersion);
        Util.runTests(MODULE_PATH, JAVA8, JAVA7, JAVA6);
    }

    private static void run3x(String cxfVersion, String springVersion) throws Exception {
        Util.updateLibVersion(MODULE_PATH, "cxf.version", cxfVersion);
        Util.updateLibVersion(MODULE_PATH, "spring.version", springVersion);
        Util.runTests(MODULE_PATH, JAVA8, JAVA7);
    }

    private static void run3xJava8(String cxfVersion, String springVersion) throws Exception {
        Util.updateLibVersion(MODULE_PATH, "cxf.version", cxfVersion);
        Util.updateLibVersion(MODULE_PATH, "spring.version", springVersion);
        Util.runTests(MODULE_PATH, JAVA8);
    }
}
