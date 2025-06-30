package guru.qa.niffler.config;

import javax.annotation.Nonnull;

public interface Config {

    static @Nonnull Config getInstance() {
        return "docker".equals(System.getProperty("test.env"))
                ? DockerConfig.instance
                : LocalConfig.instance;
    }

    @Nonnull
    String authUrl();

    @Nonnull
    String userdataUrl();

    @Nonnull
    String frontUrl();

    @Nonnull
    String registrationUrl();

    @Nonnull
    String profileUrl();

    @Nonnull
    String spendUrl();

    @Nonnull
    String ghUrl();

    @Nonnull
    String friendsUrl();

    @Nonnull
    String gatewayUrl();

    @Nonnull
    String authJdbcUrl();

    @Nonnull
    String userdataJdbcUrl();

    @Nonnull
    String spendJdbcUrl();

    @Nonnull
    String currencyJdbcUrl();
}
