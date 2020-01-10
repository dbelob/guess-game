package guess.domain;

import java.util.List;
import java.util.function.Function;

public class Quadruple<T> {
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

    public <V> Quadruple<V> map(Function<? super T, ? extends V> f) {
        return new Quadruple<>(
                f.apply(first),
                f.apply(second),
                f.apply(third),
                f.apply(fourth)
        );
    }

    public List<T> asList() {
        return List.of(first, second, third, fourth);
    }
}
