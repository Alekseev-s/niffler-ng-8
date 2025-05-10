package guru.qa.niffler.api;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.POST;

public interface AuthUserApi {

    @POST
    Call<Void> register(
            @Field("username") String username,
            @Field("password") String password,
            @Field("passwordSubmit") String passwordSubmit,
            @Field("_csrf") String csrf
    );
}
