package springfox.javadoc.plugin;

import org.springframework.core.env.Environment;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelContext;

public class JavadocModelBuilderPlugin implements ModelBuilderPlugin {

    private final Environment environment;

    public JavadocModelBuilderPlugin(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void apply(ModelContext context) {
        String notes = environment.getProperty(context.getType().getTypeName());
        if (notes != null) {
            context.getBuilder().description(notes);
        }
    }

    @Override
    public boolean supports(DocumentationType documentationType) {
        return true;
    }
}
