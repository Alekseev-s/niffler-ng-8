package guru.qa.niffler.model;

public enum AuthorityValues {
    READ, WRITE;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
