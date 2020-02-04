package guess.util.yaml;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.emitter.Emitter;

import java.io.Writer;

/**
 * Custom SnakeYAML Emitter class.
 */
public class CustomEmitter {
    public CustomEmitter(Writer stream, DumperOptions opts) {
//        super(stream, opts);
    }

    private DumperOptions.ScalarStyle chooseScalarStyle() {
/*        ScalarEvent ev = (ScalarEvent) event;
        if (analysis == null) {
            analysis = analyzeScalar(ev.getValue());
        }
        if (!ev.isPlain() && ev.getScalarStyle() == DumperOptions.ScalarStyle.DOUBLE_QUOTED || this.canonical) {
            return DumperOptions.ScalarStyle.DOUBLE_QUOTED;
        }
        if (ev.isPlain() && ev.getImplicit().canOmitTagInPlainScalar()) {
            if (!(simpleKeyContext && (analysis.empty || analysis.multiline))
                    && ((flowLevel != 0 && analysis.allowFlowPlain) || (flowLevel == 0 && analysis.allowBlockPlain))) {
                return null;
            }
        }
        if (!ev.isPlain() && (ev.getScalarStyle() == DumperOptions.ScalarStyle.LITERAL || ev.getScalarStyle() == DumperOptions.ScalarStyle.FOLDED)) {
            if (flowLevel == 0 && !simpleKeyContext && analysis.allowBlock) {
//                return ev.getScalarStyle();
                return DumperOptions.ScalarStyle.DOUBLE_QUOTED;
            }
        }
        if (ev.isPlain() || ev.getScalarStyle() == DumperOptions.ScalarStyle.SINGLE_QUOTED) {
            if (analysis.allowSingleQuoted && !(simpleKeyContext && analysis.multiline)) {
//                return DumperOptions.ScalarStyle.SINGLE_QUOTED;
                return DumperOptions.ScalarStyle.DOUBLE_QUOTED;
            }
        } */
        return DumperOptions.ScalarStyle.DOUBLE_QUOTED;
    }
}
