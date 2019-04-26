package springfox.javadoc.plugin;

import org.springframework.core.env.Environment;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;

public class JavadocModelPropertyBuilderPlugin implements ModelPropertyBuilderPlugin {

    private final Environment environment;

    public JavadocModelPropertyBuilderPlugin(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void apply(ModelPropertyContext context) {
        if (context.getBeanPropertyDefinition().isPresent()) {
            com.fasterxml.jackson.databind.introspect.AnnotatedField field =
              context.getBeanPropertyDefinition().get().getField();
            String key = field.getDeclaringClass().getName() + "." + field.getName();
            System.out.println(key);
            String notes = environment.getProperty(key);
            if (notes != null) {
                context.getBuilder().description(notes);
            }
        }
    }

    @Override
    public boolean supports(DocumentationType documentationType) {
        return true;
    }
}
