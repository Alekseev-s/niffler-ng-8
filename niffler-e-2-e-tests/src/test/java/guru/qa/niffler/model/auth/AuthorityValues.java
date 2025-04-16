package guru.qa.niffler.model.auth;

public enum AuthorityValues {
    READ, WRITE;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
