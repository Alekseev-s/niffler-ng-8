package guru.qa.niffler.config;

import javax.annotation.Nonnull;

enum LocalConfig implements Config {
    instance;

    @Nonnull
    @Override
    public String authUrl() {
        return "http://127.0.0.1:9000/";
    }

    @Nonnull
    @Override
    public String userdataUrl() {
        return "http://127.0.0.1:8089/";
    }

    @Nonnull
    @Override
    public String frontUrl() {
        return "http://127.0.0.1:3000/";
    }

    @Nonnull
    @Override
    public String registrationUrl() {
        return "http://127.0.0.1:9000/register";
    }

    @Nonnull
    @Override
    public String profileUrl() {
        return "http://127.0.0.1:3000/profile";
    }

    @Nonnull
    @Override
    public String spendUrl() {
        return "http://127.0.0.1:8093/";
    }

    @Nonnull
    @Override
    public String ghUrl() {
        return "https://api.github.com/";
    }

    @Nonnull
    @Override
    public String friendsUrl() {
        return "http://127.0.0.1:3000/people/friends";
    }

    @Nonnull
    @Override
    public String authJdbcUrl() {
        return "jdbc:postgresql://127.0.0.1:5432/niffler-auth";
    }

    @Nonnull
    @Override
    public String userdataJdbcUrl() {
        return "jdbc:postgresql://127.0.0.1:5432/niffler-userdata";
    }

    @Nonnull
    @Override
    public String spendJdbcUrl() {
        return "jdbc:postgresql://127.0.0.1:5432/niffler-spend";
    }

    @Nonnull
    @Override
    public String currencyJdbcUrl() {
        return "jdbc:postgresql://127.0.0.1:5432/niffler-currency";
    }
}
