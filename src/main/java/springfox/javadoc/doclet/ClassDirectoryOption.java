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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

public class ClassDirectoryOption implements Doclet.Option {

    public static final String SPRINGFOX_JAVADOC_PROPERTIES = "META-INF/springfox.javadoc.properties";
    private static final String DEFAULT_OPTION_VALUE = "build/classes/java/main/";
    private String classDirectory = DEFAULT_OPTION_VALUE;

    @Override
    public int getArgumentCount() {
        return 1;
    }

    @Override
    public String getDescription() {
        return "The path to the output class directory of the project. The file with Springfox properties is " +
          "generated @ <classDir>/" + SPRINGFOX_JAVADOC_PROPERTIES + ". It default to " + DEFAULT_OPTION_VALUE;
    }

    @Override
    public Kind getKind() {
        return Kind.OTHER;
    }

    @Override
    public List<String> getNames() {
        return Collections.singletonList("-classdir");
    }

    @Override
    public String getParameters() {
        return "file";
    }

    @Override
    public boolean process(String option, List<String> arguments) {
        classDirectory = arguments.get(0);
        return true;
    }

    Path getClassDirectory() {
        return Paths.get(classDirectory).resolve(SPRINGFOX_JAVADOC_PROPERTIES);
    }
}
