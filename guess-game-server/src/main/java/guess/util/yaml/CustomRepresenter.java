package guess.util.yaml;

import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Custom SnakeYAML representer.
 */
public class CustomRepresenter extends Representer {
    private final List<PropertyMatcher> propertyMatchers;

    public CustomRepresenter(List<PropertyMatcher> propertyMatchers) {
        this.propertyMatchers = propertyMatchers;
    }

    @Override
    protected NodeTuple representJavaBeanProperty(Object javaBean, Property property,
                                                  Object propertyValue, Tag customTag) {
        // Skip fields with null
        if (propertyValue != null) {
            return super.representJavaBeanProperty(javaBean, property, propertyValue, customTag);
        } else {
            return null;
        }
    }

    @Override
    protected Set<Property> getProperties(Class<?> type) {
        // Filter and sort fields
        Set<Property> originalProperties = super.getProperties(type);
        Map<String, Property> propertyMap = originalProperties.stream()
                .collect(Collectors.toMap(
                        Property::getName,
                        p -> p));

        for (PropertyMatcher propertyMatcher : propertyMatchers) {
            if (type.equals(propertyMatcher.getClazz())) {
                return propertyMatcher.getPropertyNames().stream()
                        .filter(propertyMap::containsKey)
                        .map(propertyMap::get)
                        .collect(Collectors.toCollection(LinkedHashSet::new));
            }
        }

        return originalProperties;
    }
}
