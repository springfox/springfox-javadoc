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

import com.google.common.base.Strings;

public class DocletOptionsBuilder {
    private String propertyFilePath;
    private boolean documentExceptions;

    DocletOptionsBuilder withPropertyFilePath(String propertyFilePath) {
        this.propertyFilePath = propertyFilePath;
        return this;
    }

    DocletOptionsBuilder withDocumentExceptions(boolean documentExceptions) {
        this.documentExceptions = documentExceptions;
        return this;
    }

    DocletOptions build() {
        if (Strings.isNullOrEmpty(propertyFilePath)) {
            throw new IllegalStateException("Usage: javadoc -classdir classes directory [-exceptionRef true|false (generate references to exception"
              + " classes)] -doclet  ...");
        }
        return new DocletOptions(propertyFilePath, documentExceptions);
    }
}
