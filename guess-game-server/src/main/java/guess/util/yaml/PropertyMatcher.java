package guess.util.yaml;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PropertyMatcher {
    private final Class<?> clazz;
    private final List<String> propertyNames;

    public PropertyMatcher(Class<?> clazz, List<String> propertyNames) throws NoSuchFieldException {
        this.clazz = clazz;
        this.propertyNames = propertyNames;

        // Check fields existence
        List<String> fieldNames = getFields(clazz).stream()
                .map(Field::getName)
                .collect(Collectors.toList());

        for (String propertyName : propertyNames) {
            if (!fieldNames.contains(propertyName)) {
                throw new NoSuchFieldException(propertyName);
            }
        }
    }

    List<Field> getFields(Class<?> clazz) {
        List<Field> result = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));

        if (clazz.getSuperclass() != null) {
            result.addAll(getFields(clazz.getSuperclass()));
        }

        return result;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public List<String> getPropertyNames() {
        return propertyNames;
    }
}
