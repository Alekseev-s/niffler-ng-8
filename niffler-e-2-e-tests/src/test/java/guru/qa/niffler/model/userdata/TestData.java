package guru.qa.niffler.model.userdata;

import guru.qa.niffler.model.rest.CategoryJson;
import guru.qa.niffler.model.rest.SpendJson;

import java.util.ArrayList;
import java.util.List;

public record TestData(
        String password,
        List<CategoryJson> categories,
        List<SpendJson> spendings,
        List<UserJson> friends,
        List<UserJson> outcomeInvitations,
        List<UserJson> incomeInvitations
) {
    public TestData(String password) {
        this(password, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }
}
