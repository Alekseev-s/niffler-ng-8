package guru.qa.niffler.utils;

import com.github.javafaker.Faker;

public final class DataUtils {
    private static final Faker FAKER = new Faker();

    private DataUtils() {
    }

    public static String getRandomCategoryName() {
        return FAKER.commerce().productName();
    }

    public static String getRandomUsername() {
        return FAKER.name().firstName();
    }

    public static String getRandomPassword() {
        return FAKER.internet().password(3, 10);
    }


}
