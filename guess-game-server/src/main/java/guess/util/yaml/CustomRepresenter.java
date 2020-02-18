package guess.util.yaml;

import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Custom SnakeYAML representer class.
 */
public class CustomRepresenter extends Representer {
    private final List<PropertyMatcher> propertyMatchers;

    public CustomRepresenter(List<PropertyMatcher> propertyMatchers) {
        this.propertyMatchers = propertyMatchers;

        this.representers.put(LocalDate.class, data -> CustomRepresenter.this.representData(data.toString()));
        this.representers.put(LocalTime.class, data -> CustomRepresenter.this.representData(data.toString()));
    }

    @Override
    protected NodeTuple representJavaBeanProperty(Object javaBean, Property property,
                                                  Object propertyValue, Tag customTag) {
        if (propertyValue != null) {
            if ((propertyValue instanceof Boolean) && (Boolean.FALSE.equals(propertyValue))) {
                // Skip fields with false
                return null;
            } else {
                return super.representJavaBeanProperty(javaBean, property, propertyValue, customTag);
            }
        } else {
            // Skip fields with null
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
