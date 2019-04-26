package springfox.javadoc.doclet;

import jdk.javadoc.doclet.Doclet;

import java.util.Collections;
import java.util.List;

/**
 * Dummy options are added to support default options provided by tool like Gradle
 * which are not supported by default and otherwise create error at runtime.
 */
public class DummyOption implements Doclet.Option {

    private int argumentCount;
    private String name;

    DummyOption(int argumentCount, String name) {
        this.argumentCount = argumentCount;
        this.name = name;
    }

    @Override
    public int getArgumentCount() {
        return argumentCount;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public Kind getKind() {
        return null;
    }

    @Override
    public List<String> getNames() {
        return Collections.singletonList(name);
    }

    @Override
    public String getParameters() {
        return null;
    }

    @Override
    public boolean process(String option, List<String> arguments) {
        return false;
    }
}
