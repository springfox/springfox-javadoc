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
package springfox.javadoc.plugin;

import org.springframework.core.env.Environment;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelContext;

public class JavadocModelBuilderPlugin implements ModelBuilderPlugin {

    private final Environment environment;

    public JavadocModelBuilderPlugin(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void apply(ModelContext context) {
        String notes = environment.getProperty(context.getType().getTypeName());
        if (notes != null) {
            context.getBuilder().description(notes);
        }
    }

    @Override
    public boolean supports(DocumentationType documentationType) {
        return true;
    }
}
