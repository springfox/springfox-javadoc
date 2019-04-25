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

import jdk.javadoc.doclet.DocletEnvironment;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import java.util.Optional;

class MethodProcessingContextFactory {

    private final DocletEnvironment environment;

    MethodProcessingContextFactory(DocletEnvironment environment) {
        this.environment = environment;
    }

    /**
     * Extract the method request context from the @RequestMapping annotation on the element
     *
     * @param element the candidate element
     * @return the method processing context if element annotated with RequestMapping or Optional.empty otherwise
     */
    Optional<MethodProcessingContext> from(Element element) {
        Optional<? extends AnnotationMirror> annotationOnElement = DocletHelper.getAnnotationOnElement(environment,
          element, RequestMapping.class.getName());
        return annotationOnElement.map(annotationMirror -> {
            MethodProcessingContext methodProcessingContext = new MethodProcessingContext();
            Optional<String> path = SpringMappingsHelper.getPath(annotationMirror);
            path.ifPresent(value -> sanitizePathAndSetRootPath(methodProcessingContext, value));
            setDefaultMethod(annotationMirror, methodProcessingContext);
            return methodProcessingContext;
        });
    }

    private void setDefaultMethod(AnnotationMirror annotationMirror, MethodProcessingContext methodProcessingContext) {
        DocletHelper.getAnnotationParam(annotationMirror, "method")
          .map(annotationValue -> annotationValue.getValue().toString())
          .ifPresent(methodProcessingContext::setDefaultRequestMethod);
    }

    private void sanitizePathAndSetRootPath(MethodProcessingContext methodProcessingContext, String path) {
        path = sanitizePath(path);
        methodProcessingContext.setRootPath(path);
    }

    private String sanitizePath(String path) {
        path = path.replaceAll("\"$|^\"", "");
        if (!path.startsWith("/")) {
            path = "/"+path;
        }
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

}
