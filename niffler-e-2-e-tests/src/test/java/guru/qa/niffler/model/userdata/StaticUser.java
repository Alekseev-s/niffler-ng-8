package guru.qa.niffler.model.userdata;

public record StaticUser(
        String username,
        String password,
        String friend,
        String income,
        String outcome) {
}
