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
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;

public class JavadocModelPropertyBuilderPlugin implements ModelPropertyBuilderPlugin {

    private final Environment environment;

    public JavadocModelPropertyBuilderPlugin(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void apply(ModelPropertyContext context) {
        if (context.getBeanPropertyDefinition().isPresent()) {
            com.fasterxml.jackson.databind.introspect.AnnotatedField field =
              context.getBeanPropertyDefinition().get().getField();
            String key = field.getDeclaringClass().getName() + "." + field.getName();
            String notes = environment.getProperty(key);
            if (notes != null) {
                context.getBuilder().description(notes);
            }
        }
    }

    @Override
    public boolean supports(DocumentationType documentationType) {
        return true;
    }
}
