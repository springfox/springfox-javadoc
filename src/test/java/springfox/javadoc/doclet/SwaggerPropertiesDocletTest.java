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

import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.SourcePosition;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import static org.junit.Assert.*;

/**
 * unit tests for {@link SwaggerPropertiesDoclet}
 *
 * @author MartinNeumannBeTSE
 */
public class SwaggerPropertiesDocletTest {

    /**
     * contains all full paths to relevant directories
     */
    private static final Properties PROPERTIES = new Properties();

    @BeforeClass
    public static void loadProperties() throws IOException {
        InputStream inputStream = SwaggerPropertiesDocletTest.class.getResourceAsStream("/test.properties");
        PROPERTIES.load(inputStream);
        inputStream.close();
    }

    @Test
    public void testOptionLength() throws IOException {
        assertEquals(0, SwaggerPropertiesDoclet.optionLength("dummy"));
        assertEquals(2, SwaggerPropertiesDoclet.optionLength("-classdir"));
    }

    @Test
    public void testValidOptions() {
        String[][] options = new String[][] { new String[] { "foo", "bar" }, new String[] { "baz", "dummy" } };
        DummyDocErrorReporter reporter = new DummyDocErrorReporter();
        assertFalse(SwaggerPropertiesDoclet.validOptions(options, reporter));
        assertTrue(reporter.getErrors().contains("-classdir"));

        options = new String[][] { new String[] { "foo", "bar" }, new String[] { "-classdir", "dummy" } };
        reporter = new DummyDocErrorReporter();
        assertTrue(SwaggerPropertiesDoclet.validOptions(options, reporter));
        assertTrue(reporter.getErrors().isEmpty());
    }

    @Test
    public void testPropertiesGeneration() throws IOException, InterruptedException {
        // read in the classpath file as a string.
        // Using the @<file-path> syntax supported by the javadoc tool won't work, if
        // the classpath contains whitespace.
        FileInputStream fis = new FileInputStream(PROPERTIES.getProperty("classpath.file"));
        StringBuilder classpath = new StringBuilder();
        String line;
        BufferedReader classpathReader = new BufferedReader(new InputStreamReader(fis));
        while ((line = classpathReader.readLine()) != null) {
            classpath.append(line);
        }
        classpathReader.close();


        // all the paths are read from the properties file because the javadoc
        // command-line tool needs full paths.
        // The properties file contains placeholders that are replaced by the maven
        // resource filter plugin to convert project-relative paths to full paths.
        StringBuilder command = new StringBuilder();
        command.append(PROPERTIES.getProperty("java.home"))
          .append("/../bin/javadoc");
        command.append(" -doclet springfox.javadoc.doclet.SwaggerPropertiesDoclet");
        command.append(" -docletpath ")
          .append(PROPERTIES.getProperty("doclet.path"));
        command.append(" -sourcepath ")
          .append(PROPERTIES.getProperty("source.path"));
        command.append(" -subpackages ")
          .append(PROPERTIES.getProperty("package.name"));
        command.append(" -classdir ")
          .append(PROPERTIES.getProperty("class.dir"));
        // enclose the classpath in quotes so that it still works if the classpath
        // contains whitespace
        command.append(" -classpath ")
          .append("\"")
          .append(classpath.toString()).append("\"");

        // run the javadoc command-line tool to execute the SwaggerPropertiesDoclet on
        // the classes in the springfox.javadoc.example package
        Process process = Runtime.getRuntime().exec(command.toString());
        process.waitFor();
        assertEquals(0, process.exitValue()); // make sure there were no errors

        // read in the properties file created by the SwaggerPropertiesDoclet
        InputStream inputStream = SwaggerPropertiesDocletTest.class
          .getResourceAsStream("/" + SwaggerPropertiesDoclet.SPRINGFOX_JAVADOC_PROPERTIES);
        assertNotNull(inputStream);

        // check that the properties match the example sources
        Properties props = new Properties();
        props.load(inputStream);
        assertEquals("test method", props.getProperty("/test/test.GET.notes"));
        assertEquals("dummy value", props.getProperty("/test/test.GET.return"));
        assertEquals("dummy param", props.getProperty("/test/test.GET.param.param"));
        assertEquals("without value or path", props.getProperty("/test.POST.notes"));
        assertEquals("retval", props.getProperty("/test.POST.return"));
        assertEquals("param", props.getProperty("/test.POST.param.bar"));
    }

    public class DummyDocErrorReporter implements DocErrorReporter {

        private final StringBuilder errors = new StringBuilder();
        private final StringBuilder notices = new StringBuilder();
        private final StringBuilder warnings = new StringBuilder();

        @Override
        public void printError(String error) {
            errors.append(error).append("\n");
        }

        @Override
        public void printError(SourcePosition position, String error) {
            errors.append(error).append("\n");
        }

        @Override
        public void printNotice(String notice) {
            notices.append(notice).append("\n");
        }

        @Override
        public void printNotice(SourcePosition position, String notice) {
            notices.append(notice).append("\n");
        }

        @Override
        public void printWarning(String warning) {
            warnings.append(warning).append("\n");
        }

        @Override
        public void printWarning(SourcePosition position, String warning) {
            warnings.append(warning).append("\n");
        }

        public String getErrors() {
            return errors.toString();
        }

        public String getNotices() {
            return notices.toString();
        }

        public String getWarnings() {
            return warnings.toString();
        }
    }
}
