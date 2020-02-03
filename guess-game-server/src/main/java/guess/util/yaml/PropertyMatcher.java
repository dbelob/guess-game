package guess.util.yaml;

import java.util.List;

public class PropertyMatcher {
    private Class<?> clazz;
    private List<String> propertyNames;

    public PropertyMatcher(Class<?> clazz, List<String> propertyNames) throws NoSuchFieldException {
        this.clazz = clazz;
        this.propertyNames = propertyNames;

        // Check fields existence
        for (String propertyName : propertyNames) {
            clazz.getDeclaredField(propertyName);
        }
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public List<String> getPropertyNames() {
        return propertyNames;
    }
}
