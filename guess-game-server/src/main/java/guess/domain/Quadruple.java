package guess.domain;

import java.io.Serializable;
import java.util.List;
import java.util.function.Function;

public record Quadruple<T extends Serializable>(T first, T second, T third, T fourth) implements Serializable {
    public <R extends Serializable> Quadruple<R> map(Function<? super T, ? extends R> f) {
        return new Quadruple<>(
                f.apply(first),
                f.apply(second),
                f.apply(third),
                f.apply(fourth)
        );
    }

    public <R extends Serializable> Quadruple<R> parallelMap(Function<? super T, ? extends R> f) {
        List<? extends R> result = List.of(first, second, third, fourth).parallelStream()
                .map(f)
                .toList();

        return new Quadruple<>(
                result.get(0),
                result.get(1),
                result.get(2),
                result.get(3)
        );
    }

    public List<T> asList() {
        return List.of(first, second, third, fourth);
    }
}
