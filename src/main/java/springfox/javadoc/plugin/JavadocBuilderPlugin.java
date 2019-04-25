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

import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.schema.ModelReference;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.ParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.spi.service.contexts.ParameterContext;
import springfox.javadoc.doclet.SwaggerPropertiesDoclet;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Plugin to generate the @ApiParam and @ApiOperation values from the properties
 * file generated by the {@link SwaggerPropertiesDoclet}.
 *
 * @author rgoers
 * @author MartinNeumannBeTSE
 */
@Component
@Order
public class JavadocBuilderPlugin implements OperationBuilderPlugin, ParameterBuilderPlugin {

    private static final String PERIOD = ".";
    private static final String API_PARAM = "io.swagger.annotations.ApiParam";
    private static final String REQUEST_PARAM = "org.springframework.web.bind.annotation.RequestParam";
    private static final String PATH_VARIABLE = "org.springframework.web.bind.annotation.PathVariable";
    private final Environment environment;

    public JavadocBuilderPlugin(Environment environment) {
        this.environment = environment;
    }

    private static Annotation annotationFromField(ParameterContext context, String annotationType) {

        ResolvedMethodParameter methodParam = context.resolvedMethodParameter();

        for (Annotation annotation : methodParam.getAnnotations()) {
            if (annotation.annotationType().getName().equals(annotationType)) {
                return annotation;
            }
        }
        return null;

    }

    @Override
    public boolean supports(DocumentationType delimiter) {
        return true;
    }

    @Override
    public void apply(OperationContext context) {

        String notes = context.requestMappingPattern() + PERIOD + context.httpMethod().toString() + ".notes";
        if (StringUtils.hasText(notes) && StringUtils.hasText(environment.getProperty(notes))) {
            context.operationBuilder().notes("<b>" + context.getName() + "</b><br/>" + environment.getProperty(notes));
        }
        String returnDescription = context.requestMappingPattern() + PERIOD + context.httpMethod().toString()
          + ".return";
        if (StringUtils.hasText(returnDescription) && StringUtils.hasText(environment.getProperty(returnDescription))) {
            context.operationBuilder().summary("returns " + environment.getProperty(returnDescription));
        }
        String throwsDescription = context.requestMappingPattern() + PERIOD + context.httpMethod().toString()
          + ".throws.";
        int i = 0;
        Set<ResponseMessage> responseMessages = new HashSet<>();
        while (StringUtils.hasText(throwsDescription + i)
          && StringUtils.hasText(environment.getProperty(throwsDescription + i))) {
            String[] throwsValues = StringUtils.split(environment.getProperty(throwsDescription + i), "-");
            if (throwsValues!= null && throwsValues.length == 2) {
                // TODO[MN]: proper mapping once
                // https://github.com/springfox/springfox/issues/521 is solved
                String thrownExceptionName = throwsValues[0];
                String throwComment = throwsValues[1];
                ModelReference model = new ModelRef(thrownExceptionName);
                ResponseMessage message = new ResponseMessageBuilder().code(500).message(throwComment)
                  .responseModel(model).build();
                responseMessages.add(message);
            }
            i++;
        }
        context.operationBuilder().responseMessages(responseMessages);

    }

    @Override
    public void apply(ParameterContext context) {
        String description = null;
        Optional<String> parameterName = context.resolvedMethodParameter().defaultName();
        Annotation apiParam = annotationFromField(context, API_PARAM);
        if (apiParam != null) {
            java.util.Optional<Boolean> isRequired = isParamRequired(apiParam, context);
            isRequired.ifPresent(aBoolean -> context.parameterBuilder().required(aBoolean));
        }
        if (parameterName.isPresent() && (apiParam == null || !hasValue(apiParam))) {
            String key = context.getOperationContext().requestMappingPattern() + PERIOD
              + context.getOperationContext().httpMethod().name() + ".param." + parameterName.get();
            description = environment.getProperty(key);
        }
        if (description != null) {
            context.parameterBuilder().description(description);
        }
    }

    private java.util.Optional<Boolean> isParamRequired(Annotation apiParam, ParameterContext context) {
        if (apiParam != null) {
            java.util.Optional<Boolean> required = isRequired(apiParam);
            if (required.isPresent()) {
                return required;
            }
        }
        Annotation annotation = annotationFromField(context, REQUEST_PARAM);
        if (annotation == null) {
            annotation = annotationFromField(context, PATH_VARIABLE);
        }
        return annotation != null ? isRequired(annotation) : java.util.Optional.empty();
    }

    private java.util.Optional<Boolean> isRequired(Annotation annotation) {
        for (Method method : annotation.annotationType().getDeclaredMethods()) {
            if (method.getName().equals("required")) {
                try {
                    return java.util.Optional.of((Boolean) method.invoke(annotation, (Object) null));
                } catch (Exception ex) {
                    return java.util.Optional.empty();
                }
            }
        }
        return java.util.Optional.empty();
    }

    private boolean hasValue(Annotation annotation) {
        for (Method method : annotation.annotationType().getDeclaredMethods()) {
            if (method.getName().equals("value")) {
                try {
                    method.invoke(annotation, (Object) null);
                } catch (Exception ex) {
                    return false;
                }
            }
        }
        return false;
    }
}
