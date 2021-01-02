package guess.util.yaml;

import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.ScalarNode;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;

/**
 * Constructor with LocalDate, LocalTime, ZonedDateTime support.
 */
class DateTimeYamlConstructor extends Constructor {
    DateTimeYamlConstructor(Class<?> theRoot) {
        super(theRoot);

        this.yamlClassConstructors.put(NodeId.scalar, new LocalDateLocalTimeConstructor());
    }

    private class LocalDateLocalTimeConstructor extends ConstructScalar {
        @Override
        public Object construct(Node node) {
            if (node.getType().equals(LocalDate.class)) {
                return LocalDate.parse(((ScalarNode) node).getValue());
            } else if (node.getType().equals(LocalTime.class)) {
                return LocalTime.parse(((ScalarNode) node).getValue());
            } else if (node.getType().equals(ZonedDateTime.class)) {
                return ZonedDateTime.parse(((ScalarNode) node).getValue());
            } else {
                return super.construct(node);
            }
        }
    }
}
