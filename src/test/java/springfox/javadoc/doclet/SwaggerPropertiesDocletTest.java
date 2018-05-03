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
import com.sun.tools.javadoc.Main;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;

import static org.junit.Assert.*;
import static springfox.javadoc.doclet.SwaggerPropertiesDoclet.*;

public class SwaggerPropertiesDocletTest {

    private static final String BUILD_PROPERTY_FILE_LOCATION = "./build/property-file-location";
    private static final String GENERATED_PROPERTY_FILE =
      String.format("%s/%s", BUILD_PROPERTY_FILE_LOCATION, SPRINGFOX_JAVADOC_PROPERTIES);

    @BeforeClass
    public static void setupFixture() {
        deletePropertyFile();
    }

    @AfterClass
    public static void cleanupFixture() {
        deletePropertyFile();
    }

    @Test
    public void testValidOptionLength() {
        assertEquals(2, optionLength("-classdir"));
    }

    @Test
    public void testInvalidOptionLength() {
        assertEquals(0, optionLength("dummy"));
    }

    @Test
    public void testValidOptions() {
        String[][] options = new String[][] { new String[] { "foo", "bar" }, new String[] { "-classdir", "dummy" } };
        DummyDocErrorReporter reporter = new DummyDocErrorReporter();
        assertTrue(validOptions(options, reporter));
        assertTrue(reporter.getErrors().isEmpty());
    }

    @Test
    public void testInvalidOptions() {
        String[][] options = new String[][] { new String[] { "foo", "bar" }, new String[] { "baz", "dummy" } };
        DummyDocErrorReporter reporter = new DummyDocErrorReporter();
        assertFalse(validOptions(options, reporter));
        assertTrue(reporter.getErrors().contains("-classdir"));
    }

    @Test
    public void testPropertiesGeneration() throws IOException {

        StringWriter err = new StringWriter();
        StringWriter warn = new StringWriter();
        StringWriter notice = new StringWriter();

        String[] args = new String[] {
          "-sourcepath",
          "./src/test/java",
          "-subpackages",
          "springfox.javadoc",
          "springfox.javadoc",
          "-classdir",
          BUILD_PROPERTY_FILE_LOCATION
        };

        Main.execute(
          "SwaggerPropertiesDoclet",
          new PrintWriter(err),
          new PrintWriter(warn),
          new PrintWriter(notice),
          SwaggerPropertiesDoclet.class.getName(),
          args);

        Properties props = generatedProperties();
        assertEquals("test method", props.getProperty("/test/test.GET.notes"));
        assertEquals("dummy value", props.getProperty("/test/test.GET.return"));
        assertEquals("dummy param", props.getProperty("/test/test.GET.param.param"));
        assertEquals("without value or path", props.getProperty("/test.POST.notes"));
        assertEquals("retval", props.getProperty("/test.POST.return"));
        assertEquals("param", props.getProperty("/test.POST.param.bar"));
    }

    private Properties generatedProperties() throws IOException {
        // read in the properties file created by the SwaggerPropertiesDoclet
        InputStream inputStream = new FileInputStream(GENERATED_PROPERTY_FILE);
        assertNotNull(inputStream);

        // check that the properties match the example sources
        Properties props = new Properties();
        props.load(inputStream);
        return props;
    }

    private static void deletePropertyFile() {
        File propertyFile = new File(GENERATED_PROPERTY_FILE);
        if (propertyFile.exists()) {
            propertyFile.delete();
        }
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
