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

import com.sun.source.doctree.*;
import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;
import springfox.javadoc.plugin.JavadocParameterBuilderPlugin;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Generate properties file based on Javadoc.
 * <p>
 * The generated properties file will then be read by the
 * {@link JavadocParameterBuilderPlugin} to enhance the Swagger documentation.
 *
 * @author rgoers
 * @author MartinNeumannBeTSE
 */
public class SwaggerPropertiesDoclet implements Doclet {

    private static final String NEWLINE = "\n";
    private static final String EMPTY = "";

    private ClassDirectoryOption classDirectoryOption = new ClassDirectoryOption();
    private Reporter reporter;
    private OutputStream springfoxPropertiesOutputStream;
    private DocletEnvironment environment;
    private MethodProcessingContextFactory methodProcessingContextFactory;

    private static void appendPath(StringBuilder rootPath, String path) {
        String value = path.replaceAll("\"$|^\"", "");
        if (value.startsWith("/")) {
            rootPath.append(value).append(".");
        } else {
            rootPath.append("/").append(value).append(".");
        }
    }

    private static void saveProperty(
      Properties properties,
      String key,
      String value) {

        value = value.replaceAll(NEWLINE, EMPTY);
        if (value.length() > 0) {
            properties.setProperty(key, value);
        }
    }

    @Override
    public void init(Locale locale, Reporter reporter) {
        reporter.print(Diagnostic.Kind.NOTE, "Doclet using locale: " + locale);
        this.reporter = reporter;
    }

    @Override
    public String getName() {
        return "springfox-javadoc-doclet";
    }

    @Override
    public Set<? extends Option> getSupportedOptions() {
        return new HashSet<>(Arrays.asList(new DummyOption(1, "-doctitle"),
          new DummyOption(1, "-windowtitle"),
          new DummyOption(1, "-author"), new DummyOption(1, "-d"),
          classDirectoryOption));
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_9;
    }

    @Override
    public boolean run(DocletEnvironment environment) {
        this.environment = environment;
        this.methodProcessingContextFactory = new MethodProcessingContextFactory(environment);
        try {
            preparePropertiesOutputStream();
            environment.getIncludedElements()
              .stream()
              .filter(element -> element.getKind().isClass())
              .map(element -> (TypeElement) element)
              .forEach(this::processClass);
            return true;
        } finally {
            closeSpringfoxPropertiesStream();
        }
    }

    private void closeSpringfoxPropertiesStream() {
        try {
            if (springfoxPropertiesOutputStream != null) {
                springfoxPropertiesOutputStream.close();
            }
        } catch (IOException e) {
            reporter.print(Diagnostic.Kind.WARNING, "Could not close output stream: " + e.getMessage());
        }
    }

    private void preparePropertiesOutputStream() {
        Path outputFile = classDirectoryOption.getClassDirectory();
        reporter.print(Diagnostic.Kind.NOTE, "Writing output to " + outputFile.toAbsolutePath().toString());
        try {
            Files.createDirectories(outputFile.getParent());
            springfoxPropertiesOutputStream = new FileOutputStream(classDirectoryOption.getClassDirectory().toFile());
        } catch (IOException e) {
            throw new SpringfoxDocletException(e);
        }
    }

    private void processClass(TypeElement typeElement) {
        Properties properties = new Properties();
        DocletHelper.getTypeElementDoc(environment, typeElement).ifPresent(typeElementDoc  -> {
            properties.put(typeElement.getQualifiedName().toString(), typeElementDoc);
        });
        methodProcessingContextFactory.from(typeElement)
          .ifPresent(methodProcessingContext -> {
              environment.getElementUtils().getAllMembers(typeElement).stream()
                .filter(element -> element.getKind() == ElementKind.METHOD)
                .forEach(methodElement -> this.processMethod(properties, methodProcessingContext, methodElement));
          });
        storeProperties(typeElement, properties);
    }

    private void storeProperties(TypeElement typeElement, Properties properties) {
        try {
            properties.store(springfoxPropertiesOutputStream, "Class = "+typeElement.getQualifiedName().toString());
        } catch (IOException e) {
            throw new SpringfoxDocletException(e);
        }
    }

    private void processMethod(Properties properties, MethodProcessingContext methodProcessingContext,
                               Element methodElement) {
        for (AnnotationMirror annotationMirror : environment.getElementUtils().getAllAnnotationMirrors(methodElement)) {
            if (SpringRequestMappings.isMapping(annotationMirror)) {
                String path = getMappingFullPath(methodProcessingContext, annotationMirror);
                String requestMethod = SpringRequestMappings.getRequestMethod(annotationMirror,
                  methodProcessingContext.getDefaultRequestMethod());
                if (requestMethod != null) {
                    path = path + requestMethod;
                    DocCommentTree docCommentTree = environment.getDocTrees().getDocCommentTree(methodElement);
                    saveProperty(properties, path + ".notes", docCommentTree.getFullBody().toString());
                    int throwIndex = 0;
                    for (DocTree docTree : docCommentTree.getBlockTags()) {
                        if (docTree instanceof ParamTree) {
                            ParamTree paramTree = (ParamTree) docTree;
                            saveProperty(properties, path + ".param." + paramTree.getName(),
                              paramTree.getDescription().toString());
                        }
                        if (docTree instanceof ReturnTree) {
                            ReturnTree returnTree = (ReturnTree) docTree;
                            saveProperty(properties, path + ".return", returnTree.getDescription().toString());
                        }
                        if (docTree instanceof ThrowsTree) {
                            ThrowsTree throwTree = (ThrowsTree) docTree;
                            String key = path + ".throws." + throwIndex;
                            String value =
                              throwTree.getExceptionName().getSignature() + "-" + throwTree.getDescription().toString();
                            saveProperty(properties, key, value);
                            throwIndex++;
                        }
                    }
                }
            }
        }
    }

    private String getMappingFullPath(MethodProcessingContext methodProcessingContext,
                                      AnnotationMirror annotationMirror) {
        StringBuilder path = new StringBuilder(methodProcessingContext.getRootPath());
        Optional<String> mappingPath = SpringMappingsHelper.getPath(annotationMirror);
        mappingPath.ifPresent(mappingAnnotationPath -> appendPath(path, mappingAnnotationPath));
        if (!path.substring(path.length() - 1).equals(".")) {
            path.append(".");
        }
        return path.toString();
    }
}
