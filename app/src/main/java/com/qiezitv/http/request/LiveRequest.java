package com.qiezitv.http.request;

import com.qiezitv.common.http.entity.ResponseEntity;
import com.qiezitv.model.ActivityVO;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface LiveRequest {

    /**
     * 获取直播信息
     *
     * @param activityId
     * @return
     */
    @GET("/service-admin/activity/{id}")
    Call<ResponseEntity<ActivityVO>> getActivityVo(@Path("id") String activityId);

    /**
     * 获取直播信息
     *
     * @param activityId
     * @return
     */
    @GET("/service-admin/activity/{id}/quality")
    Call<ResponseEntity<Integer>> quality(@Path("id") String activityId);

}
