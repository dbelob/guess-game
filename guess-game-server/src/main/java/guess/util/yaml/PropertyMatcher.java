package guess.util.yaml;

import java.util.List;

public class PropertyMatcher {
    private List<String> propertyNames;
    private Class<?> clazz;

    public PropertyMatcher(List<String> propertyNames, Class<?> clazz) throws NoSuchFieldException {
        this.propertyNames = propertyNames;
        this.clazz = clazz;

        // Check fields existence
        for (String propertyName : propertyNames) {
            clazz.getDeclaredField(propertyName);
        }
    }

    public List<String> getPropertyNames() {
        return propertyNames;
    }

    public Class<?> getClazz() {
        return clazz;
    }
}
