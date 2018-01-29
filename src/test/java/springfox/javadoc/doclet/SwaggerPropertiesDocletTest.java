package springfox.javadoc.doclet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;

public class SwaggerPropertiesDocletTest {

    private static final Properties PROPERTIES = new Properties();

    @BeforeClass
    public static void loadProperties() throws IOException {
        InputStream inputStream = SwaggerPropertiesDocletTest.class.getResourceAsStream("/test.properties");
        PROPERTIES.load(inputStream);
        inputStream.close();
    }

    @Test
    public void testOptionLength() throws IOException {
        // TODO[MN]: implement me
    }

    @Test
    public void testValidOptions() {
        // TODO[MN]: implement me
    }

    @Test
    public void testPropertiesGeneration() throws IOException, InterruptedException {
        FileInputStream fis = new FileInputStream(PROPERTIES.getProperty("classpath.file"));
        StringBuilder classpath = new StringBuilder();
        String line;
        BufferedReader classpathReader = new BufferedReader(new InputStreamReader(fis));
        while((line = classpathReader.readLine()) != null) {
            classpath.append(line);
        }
        classpathReader.close();

        StringBuilder command = new StringBuilder();
        command.append(PROPERTIES.getProperty("java.home")).append("/../bin/javadoc ");
        command.append("-doclet springfox.javadoc.doclet.SwaggerPropertiesDoclet ");
        command.append("-docletpath ").append(PROPERTIES.getProperty("doclet.path"));
        command.append(" -sourcepath ").append(PROPERTIES.getProperty("source.path"));
        command.append(" -subpackages ").append(PROPERTIES.getProperty("package.name"));
        command.append(" -classdir ").append(PROPERTIES.getProperty("class.dir"));
        command.append(" -classpath ").append("\"").append(classpath.toString()).append("\"");

        Process process = Runtime.getRuntime().exec(command.toString());
        process.waitFor();
        assertEquals(0, process.exitValue());

        InputStream inputStream = SwaggerPropertiesDocletTest.class
                .getResourceAsStream("/" + SwaggerPropertiesDoclet.SPRINGFOX_JAVADOC_PROPERTIES);
        assertNotNull(inputStream);

        Properties props = new Properties();
        props.load(inputStream);
        assertEquals("test method", props.getProperty("/test/test.GET.notes"));
        assertEquals("dummy value", props.getProperty("/test/test.GET.return"));
        assertEquals("dummy param", props.getProperty("/test/test.GET.param.param"));
    }

}
