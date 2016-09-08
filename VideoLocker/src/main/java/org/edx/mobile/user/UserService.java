package org.edx.mobile.user;

import org.edx.mobile.model.Page;
import org.edx.mobile.profiles.BadgeAssertion;

import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

import static org.edx.mobile.http.ApiConstants.PARAM_PAGE_SIZE;

public interface UserService {
    @GET("/api/user/v1/accounts/{username}")
    Call<Account> getAccount(@Path("username") String username);

    @PATCH("/api/user/v1/accounts/{username}")
    Call<Account> updateAccount(@Path("username") String username, @Body Map<String, Object> fields);

    @POST("/api/user/v1/accounts/{username}/image")
    Call<ResponseBody> setProfileImage(@Path("username") String username, @Header("Content-Disposition") String contentDisposition, @Body RequestBody file);

    @DELETE("/api/user/v1/accounts/{username}/image")
    Call<ResponseBody> deleteProfileImage(@Path("username") String username);

    @GET("/api/mobile/v0.5/users/{username}/course_enrollments")
    Call<ResponseBody> getUserEnrolledCourses(@Path("username") String username);

    @GET("/api/badges/v1/assertions/user/{username}?" + PARAM_PAGE_SIZE)
    Call<Page<BadgeAssertion>> getBadges(@Path("username") String username,
                                         @Query("page") int page);
}
