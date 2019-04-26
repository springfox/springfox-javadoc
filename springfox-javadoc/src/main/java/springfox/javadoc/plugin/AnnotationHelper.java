package springfox.javadoc.plugin;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class AnnotationHelper {

    static boolean hasValue(Annotation annotation) {
        for (Method method : annotation.annotationType().getDeclaredMethods()) {
            if (method.getName().equals("value")) {
                try {
                    method.invoke(annotation, (Object) null);
                } catch (Exception ex) {
                    return false;
                }
            }
        }
        return false;
    }
}
