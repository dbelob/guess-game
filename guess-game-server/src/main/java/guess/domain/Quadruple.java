package guess.domain;

import java.io.Serializable;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Quadruple<T extends Serializable> implements Serializable {
    private final T first;
    private final T second;
    private final T third;
    private final T fourth;

    public Quadruple(T first, T second, T third, T fourth) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
    }

    public T getFirst() {
        return first;
    }

    public T getSecond() {
        return second;
    }

    public T getThird() {
        return third;
    }

    public T getFourth() {
        return fourth;
    }

    public <R extends Serializable> Quadruple<R> map(Function<? super T, ? extends R> f) {
        return new Quadruple<>(
                f.apply(first),
                f.apply(second),
                f.apply(third),
                f.apply(fourth)
        );
    }

    public <R extends Serializable> Quadruple<R> parallelMap(Function<? super T, ? extends R> f) {
        List<R> result = List.of(first, second, third, fourth).parallelStream()
                .map(f)
                .collect(Collectors.toList());

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
