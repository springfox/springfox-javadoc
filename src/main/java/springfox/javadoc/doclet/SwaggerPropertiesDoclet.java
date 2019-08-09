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

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.AnnotationValue;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.Tag;
import com.sun.javadoc.ThrowsTag;
import springfox.javadoc.plugin.JavadocBuilderPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static java.util.Collections.singletonList;
import static springfox.javadoc.doclet.DocletOptionParser.*;

// the NOSONAR comment is added to ignore sonar warning about usage of Sun classes
// because doclets can only be written using Sun classes

/**
 * Generate properties file based on Javadoc.
 * <p>
 * The generated properties file will then be read by the
 * {@link JavadocBuilderPlugin} to enhance the Swagger documentation.
 *
 * @author rgoers
 * @author MartinNeumannBeTSE
 */
public class SwaggerPropertiesDoclet {

    public static final String SPRINGFOX_JAVADOC_PROPERTIES = "META-INF/springfox.javadoc.properties";
    private static final String REQUEST_MAPPING = "org.springframework.web.bind.annotation.RequestMapping";
    private static final String REQUEST_GET_MAPPING = "org.springframework.web.bind.annotation.RequestMethod.GET";
    private static final String REQUEST_POST_MAPPING = "org.springframework.web.bind.annotation.RequestMethod.POST";
    private static final String REQUEST_PUT_MAPPING = "org.springframework.web.bind.annotation.RequestMethod.PUT";
    private static final String REQUEST_PATCH_MAPPING = "org.springframework.web.bind.annotation.RequestMethod.PATCH";
    private static final String REQUEST_DELETE_MAPPING = "org.springframework.web.bind.annotation.RequestMethod.DELETE";
    private static final String DELETE_MAPPING = "org.springframework.web.bind.annotation.DeleteMapping";
    private static final String GET_MAPPING = "org.springframework.web.bind.annotation.GetMapping";
    private static final String PATCH_MAPPING = "org.springframework.web.bind.annotation.PatchMapping";
    private static final String POST_MAPPING = "org.springframework.web.bind.annotation.PostMapping";
    private static final String PUT_MAPPING = "org.springframework.web.bind.annotation.PutMapping";
    private static final String RETURN = "@return";
    private static final String PATH = "path";
    private static final String VALUE = "value";
    private static final String NEWLINE = "\n";
    private static final String EMPTY = "";
    private static final String METHOD = "method";

    private static final String[] MAPPINGS = new String[] {
      DELETE_MAPPING,
      GET_MAPPING,
      PATCH_MAPPING,
      POST_MAPPING,
      PUT_MAPPING,
      REQUEST_MAPPING };

    private static final String[][] REQUEST_MAPPINGS = new String[][] {
      { REQUEST_DELETE_MAPPING, "DELETE" },
      { REQUEST_GET_MAPPING, "GET" },
      { REQUEST_PATCH_MAPPING, "PATCH" },
      { REQUEST_POST_MAPPING, "POST" },
      { REQUEST_PUT_MAPPING, "PUT" }
    };

    private static DocletOptions docletOptions;

    private SwaggerPropertiesDoclet() {
        throw new UnsupportedOperationException();
    }


    /**
     * See <a href=
     * "https://docs.oracle.com/javase/8/docs/technotes/guides/javadoc/doclet/overview.html#options">Using
     * custom command-line options</a>
     * @param option option evaluate an expected length for a given option
     * @return number of options
     */
    @SuppressWarnings("WeakerAccess")
    public static int optionLength(String option) {
        int length = 0;
        if (option.equalsIgnoreCase(CLASS_DIR_OPTION)) {
            length = 2;
        }
        if (option.equalsIgnoreCase(EXCEPTION_REF_OPTION)) {
            length = 2;
        }
        return length;
    }

    /**
     * See <a href=
     * "https://docs.oracle.com/javase/8/docs/technotes/guides/javadoc/doclet/overview.html#options">Using
     * custom command-line options</a>
     * @param options command line options split as key value pairs on index 0 and 1
     * @param reporter reporter for errors
     * @return true if options are valid
     */
    @SuppressWarnings("WeakerAccess")
    public static boolean validOptions(
      String[][] options,
      DocErrorReporter reporter) {

        DocletOptionParser parser = new DocletOptionParser(options);

        try {
            docletOptions = parser.parse();
            return true;
        } catch (IllegalStateException e) {
            reporter.printError(e.getMessage());
        }
        return false;
    }

    /**
     * See <a href=
     * "https://docs.oracle.com/javase/8/docs/technotes/guides/javadoc/doclet/overview.html#simple">A
     * Simple Example Doclet</a>
     * @param root {@link RootDoc}
     * @return true if it started successfully
     */
    @SuppressWarnings({ "unused", "WeakerAccess", "UnusedReturnValue" })
    public static boolean start(RootDoc root) {

        String propertyFilePath = docletOptions.getPropertyFilePath();
        if (propertyFilePath == null || propertyFilePath.length() == 0) {
            root.printError("No output location was specified");
            return false;
        } else {
            StringBuilder sb = new StringBuilder(propertyFilePath);
            if (!propertyFilePath.endsWith("/")) {
                sb.append("/");
            }
            sb.append(SPRINGFOX_JAVADOC_PROPERTIES);
            String out = sb.toString();
            root.printNotice("Writing output to " + out);
            File file = new File(out);
            //noinspection ResultOfMethodCallIgnored
            file.getParentFile().mkdirs();
            OutputStream javadoc = null;
            try {
                javadoc = new FileOutputStream(file);
                Properties properties = new Properties();

                for (ClassDoc classDoc : root.classes()) {
                    sb.setLength(0);
                    String defaultRequestMethod = processClass(classDoc, sb);
                    String pathRoot = sb.toString();
                    for (MethodDoc methodDoc : classDoc.methods()) {
                        processMethod(
                          properties,
                          methodDoc,
                          defaultRequestMethod,
                          pathRoot,
                          docletOptions.isDocumentExceptions());
                    }
                }
                properties.store(javadoc, "Springfox javadoc properties");
            } catch (IOException e) {
                root.printError(e.getMessage());
            } finally {
                if (javadoc != null) {
                    try {
                        javadoc.close();
                    } catch (IOException e) {
                        // close for real
                    }
                }
            }
        }
        return true;
    }

    private static String processClass(
      ClassDoc classDoc,
      StringBuilder pathRoot) {

        String defaultRequestMethod = null;
        for (AnnotationDesc annotationDesc : classDoc.annotations()) {
            if (REQUEST_MAPPING.equals(annotationDesc.annotationType().qualifiedTypeName())) {
                for (AnnotationDesc.ElementValuePair pair : annotationDesc.elementValues()) {

                    if (VALUE.equals(pair.element().name()) || PATH.equals(pair.element().name())) {
                        setRoot(pathRoot, pair);
                    }
                    if (METHOD.equals(pair.element().name())) {
                        defaultRequestMethod = pair.value().toString();
                    }
                }
                break;
            }
        }
        return defaultRequestMethod;
    }

    private static void setRoot(
      StringBuilder pathRoot,
      AnnotationDesc.ElementValuePair pair) {

        String value = pair.value().toString().replaceAll("\"$|^\"", "");
        if (!value.startsWith("/")) {
            pathRoot.append("/");
        }
        if (value.endsWith("/")) {
            pathRoot.append(value, 0, value.length() - 1);
        } else {
            pathRoot.append(value);
        }
    }

    private static void processMethod(
      Properties properties,
      MethodDoc methodDoc,
      String defaultRequestMethod,
      String pathRoot,
      boolean exceptionRef) {

        for (AnnotationDesc annotationDesc : methodDoc.annotations()) {
            String annotationType = annotationDesc.annotationType().toString();
            if (isMapping(annotationType)) {
                StringBuilder path = new StringBuilder(pathRoot);
                for (AnnotationDesc.ElementValuePair pair : annotationDesc.elementValues()) {
                    if (VALUE.equals(pair.element().name()) || PATH.equals(pair.element().name())) {
                        appendPath(path, pair);
                        break;
                    }
                }
                if (!path.substring(path.length() - 1).equals(".")) {
                    path.append(".");
                }
                for (String requestMethod : getRequestMethods(annotationDesc, annotationType, defaultRequestMethod)) {
                    if (requestMethod != null) {
                        String fullPath = path.toString() + requestMethod;
                        saveProperty(properties, fullPath + ".notes", methodDoc.commentText());

                        for (ParamTag paramTag : methodDoc.paramTags()) {
                            saveProperty(
                              properties,
                              fullPath + ".param." + paramTag.parameterName(),
                              paramTag.parameterComment());
                        }
                        for (Tag tag : methodDoc.tags()) {
                            if (tag.name().equals(RETURN)) {
                                saveProperty(properties, fullPath + ".return", tag.text());
                                break;
                            }
                        }
                        if (exceptionRef) {
                            processThrows(properties, methodDoc.throwsTags(), fullPath);
                        }
                    }
                }
            }
        }
    }

    private static void appendPath(
      StringBuilder path,
      AnnotationDesc.ElementValuePair pair) {

        String value = pair.value().toString().replaceAll("\"$|^\"", "");
        if (value.startsWith("/")) {
            path.append(value).append(".");
        } else {
            path.append("/").append(value).append(".");
        }
    }

    private static boolean isMapping(String name) {
        for (String mapping : MAPPINGS) {
            if (mapping.equals(name)) {
                return true;
            }
        }
        return false;
    }

    private static List<String> getRequestMethods(
      AnnotationDesc annotationDesc,
      String name,
      String defaultRequestMethod) {

        if (REQUEST_MAPPING.equals(name)) {
            for (AnnotationDesc.ElementValuePair pair : annotationDesc.elementValues()) {
                if (METHOD.equals(pair.element().name())) {
                    return resolveRequestMethods(pair, defaultRequestMethod);
                }
            }
        } else if (PUT_MAPPING.equals(name)) {
            return singletonList("PUT");
        } else if (POST_MAPPING.equals(name)) {
            return singletonList("POST");
        } else if (PATCH_MAPPING.equals(name)) {
            return singletonList("PATCH");
        } else if (GET_MAPPING.equals(name)) {
            return singletonList("GET");
        } else if (DELETE_MAPPING.equals(name)) {
            return singletonList("DELETE");
        }
        return singletonList(defaultRequestMethod);
    }

    private static List<String> resolveRequestMethods(
      AnnotationDesc.ElementValuePair pair, String defaultRequestMethod) {
        List<String> result = new ArrayList<String>();
        AnnotationValue method = pair.value();
        if (method.value().getClass().isArray()) {
            for (Object val : (Object[]) method.value()) {
                String value = getMethodName(val.toString());
                if (value != null) {
                    result.add(value);
                }
            }
        } else {
            String value = getMethodName(method.toString());
            if (value != null) {
                result.add(value);
            }
        }
        if (result.isEmpty()) {
            return singletonList(defaultRequestMethod);
        }
        return result;

    }

    /** Given a qualified request mapping, return the method name. */
    private static String getMethodName(String requestMapping) {
        for (String[] each : REQUEST_MAPPINGS) {
            if (each[0].equals(requestMapping)) {
                return each[1];
            }
        }
        return null;
    }

    private static void processThrows(
      Properties properties,
      ThrowsTag[] throwsTags,
      String path) {

        for (int i = 0; i < throwsTags.length; i++) {
            String key = path + ".throws." + i;
            String value =
              throwsTags[i].exceptionType().typeName() + "-" + throwsTags[i].exceptionComment();
            saveProperty(properties, key, value);
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
}
