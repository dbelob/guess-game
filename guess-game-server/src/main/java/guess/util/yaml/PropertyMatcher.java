package guess.util.yaml;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PropertyMatcher {
    private final Class<?> clazz;
    private final List<String> propertyNames;

    public PropertyMatcher(Class<?> clazz, List<String> propertyNames) throws NoSuchFieldException {
        this.clazz = clazz;
        this.propertyNames = propertyNames;

        // Check fields existence
        checkPropertyNamesExistence(propertyNames, getClassFieldNames(clazz));
    }

    static Set<String> getClassFieldNames(Class<?> clazz) {
        List<Field> fields = Arrays.asList(clazz.getDeclaredFields());
        Set<String> result = fields.stream()
                .map(Field::getName)
                .collect(Collectors.toCollection(HashSet::new));

        if (clazz.getSuperclass() != null) {
            result.addAll(getClassFieldNames(clazz.getSuperclass()));
        }

        return result;
    }

    static void checkPropertyNamesExistence(List<String> propertyNames, Set<String> classFieldNames) throws NoSuchFieldException {
        for (String propertyName : propertyNames) {
            if (!classFieldNames.contains(propertyName)) {
                throw new NoSuchFieldException(propertyName);
            }
        }
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public List<String> getPropertyNames() {
        return propertyNames;
    }
}
