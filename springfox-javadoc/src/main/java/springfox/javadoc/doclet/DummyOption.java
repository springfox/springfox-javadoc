/*
 *
 *  Copyright 2018-2019 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */
package springfox.javadoc.doclet;

import jdk.javadoc.doclet.Doclet;

import java.util.Collections;
import java.util.List;

/**
 * Dummy options are added to support default options provided by tool like Gradle
 * which are not supported by default and otherwise create error at runtime.
 */
public class DummyOption implements Doclet.Option {

    private int argumentCount;
    private String name;

    DummyOption(int argumentCount, String name) {
        this.argumentCount = argumentCount;
        this.name = name;
    }

    @Override
    public int getArgumentCount() {
        return argumentCount;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public Kind getKind() {
        return null;
    }

    @Override
    public List<String> getNames() {
        return Collections.singletonList(name);
    }

    @Override
    public String getParameters() {
        return null;
    }

    @Override
    public boolean process(String option, List<String> arguments) {
        return false;
    }
}
