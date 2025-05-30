package guru.qa.niffler.config;

import javax.annotation.Nonnull;

public enum DockerConfig implements Config {
    instance;

    @Nonnull
    @Override
    public String authUrl() {
        return "";
    }

    @Nonnull
    @Override
    public String userdataUrl() {
        return "";
    }

    @Nonnull
    @Override
    public String frontUrl() {
        return "";
    }

    @Nonnull
    @Override
    public String registrationUrl() {
        return "";
    }

    @Nonnull
    @Override
    public String profileUrl() {
        return "";
    }

    @Nonnull
    @Override
    public String spendUrl() {
        return "";
    }

    @Nonnull
    @Override
    public String ghUrl() {
        return "";
    }

    @Nonnull
    @Override
    public String friendsUrl() {
        return "";
    }

    @Nonnull
    @Override
    public String authJdbcUrl() {
        return "";
    }

    @Nonnull
    @Override
    public String userdataJdbcUrl() {
        return "";
    }

    @Nonnull
    @Override
    public String spendJdbcUrl() {
        return "";
    }

    @Nonnull
    @Override
    public String currencyJdbcUrl() {
        return "";
    }
}
