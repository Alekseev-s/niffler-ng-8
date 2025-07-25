package guru.qa.niffler.test.gql;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.FriendsWithCategoriesQuery;
import guru.qa.UserWithNestedFriendsQuery;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Objects;

public class UserGraphqlTest extends BaseGraphqlTest {

    @User(amountOfFriends = 1)
    @ApiLogin
    @Test
    void anotherUserCategoriesShouldReturnError(@Token String bearerToken) {
        final ApolloCall<FriendsWithCategoriesQuery.Data> statCall = apolloClient.query(
                        FriendsWithCategoriesQuery.builder()
                                .page(0)
                                .size(10)
                                .sort(null)
                                .searchQuery(null)
                                .build())
                .addHttpHeader("authorization", "Bearer " + bearerToken);

        final ApolloResponse<FriendsWithCategoriesQuery.Data> response = Rx2Apollo.single(statCall).blockingGet();

        Assertions.assertTrue(response.hasErrors());
        Assertions.assertTrue(Objects.requireNonNull(response.errors).getFirst().getMessage()
                .contains("Can`t query categories for another user"));
    }

    @User
    @ApiLogin
    @Test
    void nestedFriendsShouldReturnError(@Token String bearerToken) {
        final ApolloCall<UserWithNestedFriendsQuery.Data> statCall = apolloClient.query(
                        UserWithNestedFriendsQuery.builder()
                                .page(0)
                                .size(10)
                                .build())
                .addHttpHeader("authorization", "Bearer " + bearerToken);

        final ApolloResponse<UserWithNestedFriendsQuery.Data> response = Rx2Apollo.single(statCall).blockingGet();

        Assertions.assertTrue(response.hasErrors());
        Assertions.assertTrue(Objects.requireNonNull(response.errors).getFirst().getMessage()
                .contains("Can`t fetch over 2 friends sub-queries"));
    }
}
