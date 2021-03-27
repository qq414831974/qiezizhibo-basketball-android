package com.qiezitv.http.request;

import com.qiezitv.common.http.entity.ResponseEntity;
import com.qiezitv.model.ScoreBoard;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ScoreBoardRequest {

    /**
     * 获取比分牌
     *
     * @return
     */
    @GET("/service-admin/sys/scoreboard")
    Call<ResponseEntity<List<ScoreBoard>>> getScoreboard();
}
