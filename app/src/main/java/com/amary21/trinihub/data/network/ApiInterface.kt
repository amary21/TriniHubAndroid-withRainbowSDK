package com.amary21.trinihub.data.network

import com.amary21.trinihub.data.network.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


interface ApiInterface {

    @Headers(
        "accept: application/json",
        "x-rainbow-client: android",
        "x-rainbow-client-version: 1.10.7"
    )
    @GET("api/rainbow/authentication/v1.0/login")
    fun login(
        @Header("Authorization") authorization: String?,
        @Header("x-rainbow-app-auth") appAuth: String?
    ): Call<AuthorizationResponse>

    @Headers("Accept: application/json")
    @GET("api/rainbow/authentication/v1.0/logout")
    fun logout(
        @Header("Authorization") authorization: String?
    ): Call<Void>

    @Headers("Accept: application/json")
    @GET("api/rainbow/admin/v1.0/users/{userId}")
    fun getUser(
        @Header("Authorization") authorization: String?,
        @Path("userId") userId: String?
    ): Call<UserResponse>

    @Headers("Content-Type: application/json")
    @PUT("api/rainbow/admin/v1.0/users/{userId}")
    fun updateInfoUser(
        @Header("Authorization") authorization: String?
        , @Path("userId") userId: String?,
        @Body user: User?
    ): Call<UserResponse>


    /** API Trini HUB **/
    @Multipart
    @POST("insert_user.php")
    fun insertUserHub(
        @PartMap form: HashMap<String, RequestBody?>
    ): Call<InsertHubResponse>

    @Multipart
    @POST("insert_meet.php")
    fun insertMeetHub(
        @Part image: MultipartBody.Part,
        @PartMap form: Map<String, @JvmSuppressWildcards RequestBody>
    ): Call<InsertHubResponse>

    @POST("read_meet.php")
    fun getDataMeetHub(): Call<MeetHubResponse>

    @POST("read_meet_tomorow.php")
    fun getDataMeetTomorowHub() : Call<MeetHubResponse>


    @POST("read_guestlist.php")
    fun getGuestList(
        @Body form: RequestBody
    ): Call<UserHubResponse>

    @POST("insert_guestlist.php")
    fun  insertGuestList(
        @Body form: RequestBody
    ): Call<InsertHubResponse>

}