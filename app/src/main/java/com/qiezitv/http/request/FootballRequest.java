package com.qiezitv.http.request;

import com.qiezitv.common.http.entity.ResponseEntity;
import com.qiezitv.model.LeagueVO;
import com.qiezitv.model.MatchStatusDetail;
import com.qiezitv.model.MatchVO;
import com.qiezitv.model.Page;
import com.qiezitv.model.PlayerVO;
import com.qiezitv.model.TimeLine;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface FootballRequest {

    /**
     * 获取联赛列表
     *
     * @param map
     * @return
     */
    @GET("/service-admin/football/league")
    Call<ResponseEntity<Page<LeagueVO>>> getLeagueList(@QueryMap Map<String, Object> map);


    /**
     * 根据联赛id获取联赛信息
     *
     * @param id
     * @param map
     * @return
     */
    @GET("/service-admin/football/league/{id}")
    Call<ResponseEntity<LeagueVO>> getLeagueById(@Path("id") long id,
                                                 @QueryMap Map<String, Object> map);


    /**
     * 获取比赛列表
     *
     * @param map
     * @return
     */
    @GET("/service-admin/football/match")
    Call<ResponseEntity<Page<MatchVO>>> getMatchList(@QueryMap Map<String, Object> map);


    /**
     * 根据id获取比赛
     *
     * @param id
     * @return
     */
    @GET("/service-admin/football/match/{id}")
    Call<ResponseEntity<MatchVO>> getMatchById(@Path("id") long id);


    /**
     * 获取比赛的状态
     *
     * @param matchId
     * @return
     */
    @GET("/service-admin/football/timeline/status")
    Call<ResponseEntity<MatchStatusDetail>> getMatchStatusDetailById(@Query("matchId") Long matchId);

    /**
     * 删除球赛统计信息
     *
     * @param id
     * @return
     */
    @DELETE("/service-admin/football/timeline")
    Call<ResponseEntity<Boolean>> deleteTimeLine(@Query("id") long id);

    /**
     * 添加球赛统计信息
     *
     * @param timeLinePo
     * @return
     */
    @POST("/service-admin/football/timeline")
    Call<ResponseEntity<Boolean>> addTimeLine(@Body TimeLine timeLinePo);

    /**
     * 获取某个球队里的球员列表
     *
     * @param teamId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GET("/service-admin/football/player")
    Call<ResponseEntity<Page<PlayerVO>>> getTeamPlayerList(@Query("teamId") long teamId,
                                                           @Query("pageNum") int pageNum,
                                                           @Query("pageSize") int pageSize);

    /**
     * 修改比赛比分和状态
     *
     * @param match
     * @return
     */
    @PUT("/service-admin/football/match/score")
    Call<ResponseEntity<Boolean>> updateMatchScoreStatus(@Body MatchVO match);
}
