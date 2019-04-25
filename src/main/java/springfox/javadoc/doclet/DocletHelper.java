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

import javax.lang.model.element.*;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

class DocletHelper {

    static CharSequence asQualifiedName(AnnotationMirror annotationMirror) {
        return ((TypeElement) annotationMirror.getAnnotationType().asElement()).getQualifiedName();
    }

    static Optional<? extends AnnotationMirror> getAnnotationOnElement(DocletEnvironment docletEnvironment,
                                                                       Element element, String annotationClassName) {
        return docletEnvironment.getElementUtils().getAllAnnotationMirrors(element)
          .stream()
          .filter(annotationMirror -> annotationClassName.contentEquals(asQualifiedName(annotationMirror)))
          .findFirst();
    }

    /**
     * Return the value of an annotation parameter retrieved from the annotation passed as parameter.
     * Example: @RequestMapping(method = POST) with paramName = POST would request POST
     *
     * @param annotationMirror the annotation mirror
     * @param paramName        param name
     * @return the annotationValue or empty if none matches the name
     */
    static Optional<AnnotationValue> getAnnotationParam(AnnotationMirror annotationMirror, String paramName) {
        Set<? extends Map.Entry<? extends ExecutableElement, ? extends AnnotationValue>> entries =
          annotationMirror.getElementValues().entrySet();
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : entries) {
            Name annotationAttributeName = entry.getKey().getSimpleName();
            if (paramName.contentEquals(annotationAttributeName)) {
                return Optional.of(entry.getValue());
            }
        }
        return Optional.empty();
    }

}
