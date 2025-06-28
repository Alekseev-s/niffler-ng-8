package guru.qa.niffler.api;

import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.spend.CategoryJson;
import guru.qa.niffler.model.spend.CurrencyValues;
import guru.qa.niffler.model.spend.SpendJson;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ParametersAreNonnullByDefault
public class SpendApiClient extends RestClient {

  private static final Config CFG = Config.getInstance();

  private final SpendApi spendApi;

  public SpendApiClient(String baseUrl) {
    super(CFG.spendUrl());
    this.spendApi = create(SpendApi.class);
  }

  public @Nullable SpendJson addSpend(SpendJson spend) {
    final Response<SpendJson> response;
    try {
      response = spendApi.addSpend(spend)
              .execute();
    } catch (IOException e) {
        throw new AssertionError(e);
    }
    assertEquals(201, response.code());
    return response.body();
  }

  public @Nullable SpendJson editSpend(SpendJson spend) {
    final Response<SpendJson> response;
    try {
      response = spendApi.editSpend(spend)
              .execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(200, response.code());
    return response.body();
  }

  public @Nullable SpendJson getSpend(String id, String username) {
    final Response<SpendJson> response;
    try {
      response = spendApi.getSpend(id, username)
              .execute();
    } catch (IOException e) {
        throw new AssertionError(e);
    }
    assertEquals(200, response.code());
    return response.body();
  }

  public @Nonnull List<SpendJson> getAllSpends(String username,
                                               @Nullable CurrencyValues currency,
                                               @Nullable Date from,
                                               @Nullable Date to) {
    final Response<List<SpendJson>> response;
    try {
      response = spendApi.getSpends(username, currency, from, to)
              .execute();
    } catch (IOException e) {
        throw new AssertionError(e);
    }
    assertEquals(200, response.code());
    return response.body() != null
            ? response.body()
            : Collections.emptyList();
  }

  public void removeSpend(String username, List<String> ids) {
    final Response<Void> response;
    try {
      response = spendApi.removeSpend(username, ids)
              .execute();
    } catch (IOException e) {
        throw new AssertionError(e);
    }
    assertEquals(200, response.code());
  }

  public @Nullable CategoryJson addCategory(CategoryJson category) {
    final Response<CategoryJson> response;
    try {
      response = spendApi.addCategory(category)
              .execute();
    } catch (IOException e) {
        throw new AssertionError(e);
    }
    assertEquals(200, response.code());
    return response.body();
  }

  public @Nullable CategoryJson updateCategory(CategoryJson category) {
    final Response<CategoryJson> response;
    try {
      response = spendApi.updateCategory(category)
              .execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(200, response.code());
    return response.body();
  }

  public @Nonnull List<CategoryJson> getCategories(String username, boolean excludeArchived) {
    final Response<List<CategoryJson>> response;
    try {
      response = spendApi.getCategories(username, excludeArchived)
              .execute();
    } catch (IOException e) {
        throw new AssertionError(e);
    }
    assertEquals(200, response.code());
    return response.body() != null
            ? response.body()
            : Collections.emptyList();
  }
}
