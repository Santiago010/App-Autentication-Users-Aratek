package cn.com.aratek.demo.featuresrequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface FingerprintService {
    @GET("users")
    public Call<List<User>> getUsers(@Query("limit") Number limit);

    @POST("users")
    public Call <Newuser> sendUser(@Body DataForLogin dataForLogin);
}
