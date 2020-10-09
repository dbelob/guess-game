package guess.domain;

public abstract class QuestionAnswer<T extends Identifiable> extends Identifier {
    private final T entity;

    protected QuestionAnswer(T entity) {
        super(entity.getId());

        this.entity = entity;
    }

    public T getEntity() {
        return entity;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
