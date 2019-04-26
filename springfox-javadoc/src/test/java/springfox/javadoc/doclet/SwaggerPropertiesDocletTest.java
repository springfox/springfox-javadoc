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

import org.junit.BeforeClass;
import org.junit.Test;
import springfox.javadoc.example.SecretAgent;
import springfox.javadoc.example.TestController;

import javax.tools.DocumentationTool;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static springfox.javadoc.doclet.ClassDirectoryOption.SPRINGFOX_JAVADOC_PROPERTIES;

public class SwaggerPropertiesDocletTest {

    private static final String BUILD_PROPERTY_FILE_LOCATION = "./build/property-file-location";
    private static final String GENERATED_PROPERTY_FILE =
      String.format("%s/%s", BUILD_PROPERTY_FILE_LOCATION, SPRINGFOX_JAVADOC_PROPERTIES);

    @BeforeClass
    public static void deletePropertyFile() {
        File propertyFile = new File(GENERATED_PROPERTY_FILE);
        if (propertyFile.exists()) {
            //noinspection ResultOfMethodCallIgnored
            propertyFile.delete();
        }
    }

    @Test
    public void testPropertiesGeneration() throws IOException {
        DocumentationTool systemDocumentationTool = ToolProvider.getSystemDocumentationTool();
        String[] args = new String[] {
          "-sourcepath",
          "./src/test/java",
          "-subpackages",
          "springfox.javadoc.doclet",
          "springfox.javadoc.example",
          "-d",
          "whatever not used just to show compatibility",
          "-author",
          "whatever not used just to show compatibility",
          "-doctitle",
          "whatever not used just to show compatibility",
          "-windowtitle",
          "whatever not used just to show compatibility",
          "-classdir",
          BUILD_PROPERTY_FILE_LOCATION
        };
        DocumentationTool.DocumentationTask task = systemDocumentationTool.getTask(null, null, null,
          SwaggerPropertiesDoclet.class, Arrays.asList(args), null);

        task.call();

        Properties props = generatedProperties();
        assertEquals("test controller class", props.getProperty(TestController.class.getName()));
        assertEquals("Secret Agent, it can be James Bond!", props.getProperty(SecretAgent.class.getName()));
        assertEquals("Secret agent name, probably something badass", props.getProperty(SecretAgent.class.getName()+".secretAgentName"));
        assertEquals("test method", props.getProperty("/test/test.GET.notes"));
        assertEquals("dummy value", props.getProperty("/test/test.GET.return"));
        assertEquals("dummy param", props.getProperty("/test/test.GET.param.param"));
        assertEquals("without value or path", props.getProperty("/test.POST.notes"));
        assertEquals("other without value or path", props.getProperty("/other.POST.notes"));
        assertEquals("other without value or path other line in delete mapping", props.getProperty("/other.DELETE.notes"));
        assertEquals("InvalidNameException-when parameter smaller than 1", props.getProperty("/other/test.GET.throws.1"));
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

}
