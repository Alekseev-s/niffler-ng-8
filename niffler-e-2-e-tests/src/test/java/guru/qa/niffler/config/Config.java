package guru.qa.niffler.config;

public interface Config {

    static Config getInstance() {
        return "docker".equals(System.getProperty("test.env"))
                ? DockerConfig.instance
                : LocalConfig.instance;
    }

    String authUrl();

    String userdataUrl();

    String frontUrl();

    String registrationUrl();

    String profileUrl();

    String spendUrl();

    String ghUrl();

    String friendsUrl();

    String authJdbcUrl();

    String userdataJdbcUrl();

    String spendJdbcUrl();

    String currencyJdbcUrl();
}
