package guru.qa.niffler.service;

import guru.qa.niffler.model.userdata.UserJson;

public interface UsersClient {

    UserJson createUser(String username, String password);

    void createIncomeInvitations(UserJson targetUser, int count);

    void createOutcomeInvitations(UserJson targetUser, int count);

    void createFriend(UserJson targetUser, int count);
}
