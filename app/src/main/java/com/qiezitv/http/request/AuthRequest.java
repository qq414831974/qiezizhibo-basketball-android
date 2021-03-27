package com.qiezitv.http.request;

import com.qiezitv.common.http.entity.ResponseEntity;
import com.qiezitv.http.request.body.LoginBean;
import com.qiezitv.model.AccessToken;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AuthRequest {

    /**
     * 登录,采用oauth2授权标准
     *
     * @param loginBean
     * @return
     */
    @POST("/service-admin/auth")
    Call<ResponseEntity<AccessToken>> login(@Body LoginBean loginBean);

    /**
     * 刷新token
     *
     * @param refreshToken
     * @return
     */
    @POST("/service-admin/auth/refresh_token")
    Call<ResponseEntity<AccessToken>> refreshToken(@Query("refresh_token") String refreshToken);

}
