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

import org.springframework.web.bind.annotation.*;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import java.util.Arrays;
import java.util.Optional;

@SuppressWarnings("unused")
public enum SpringRequestMappings {

    DELETE_MAPPING(DeleteMapping.class),
    GET_MAPPING(GetMapping.class),
    PATCH_MAPPING(PatchMapping.class),
    POST_MAPPING(PostMapping.class),
    PUT_MAPPING(PutMapping.class),
    REQUEST_MAPPING(RequestMapping.class);

    Class<?> annotationClass;

    SpringRequestMappings(Class<?> annotationClass) {
        this.annotationClass = annotationClass;
    }

    static boolean isMapping(AnnotationMirror annotationMirror) {
        CharSequence charSequence = DocletHelper.asQualifiedName(annotationMirror);
        return Arrays.stream(values()).anyMatch(value -> value.annotationClass.getName().contentEquals(charSequence));
    }

    static String getRequestMethod(AnnotationMirror annotationMirror, String defaultRequestMethod) {
        CharSequence annotationClassName = DocletHelper.asQualifiedName(annotationMirror);

        if (REQUEST_MAPPING.annotationClass.getName().contentEquals(annotationClassName)) {
            Optional<AnnotationValue> method = DocletHelper.getAnnotationParam(annotationMirror, "method");
            if (method.isPresent()) {
                RequestMethod[] methods = (RequestMethod[]) method.get().getValue();
                //TODO Make the simple assumption that there is only one method...
                return methods[0].name();
            }
        }

        for (SpringRequestMappings value : values()) {
            if (value.annotationClass.getName().contentEquals(annotationClassName)) {
                RequestMethod[] requestMethod = value.annotationClass.getAnnotationsByType(RequestMapping.class)[0].method();
                return requestMethod[0].name();
            }
        }

        return defaultRequestMethod;
    }

}
