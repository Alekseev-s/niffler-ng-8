package guru.qa.niffler.api;

import guru.qa.niffler.model.userdata.UserJson;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public interface UserdataApi {

    @GET("internal/users/current")
    Call<UserJson> currentUser(@Query("username") String username);

    @POST("internal/invitations/send")
    Call<UserJson> sendInvitation(@Query("username") String username,
                                  @Query("targetUsername") String targetUsername);

    @POST("internal/invitations/accept")
    Call<UserJson> acceptInvitation(@Query("username") String username,
                                    @Query("targetUsername") String targetUsername);

    @GET("internal/users/all")
    Call<List<UserJson>> allUsers(@Query("username") String username,
                                  @Query("searchQuery") String searchQuery);
}
