package com.qiezitv.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerview.view.TimePickerView;
import com.qiezitv.R;
import com.qiezitv.activity.LoginActivity;
import com.qiezitv.activity.MainActivity;
import com.qiezitv.activity.MatchActivity;
import com.qiezitv.adapter.TeamPlayerAdapter;
import com.qiezitv.common.FinishActivityManager;
import com.qiezitv.common.ImageLoaderUtil;
import com.qiezitv.common.http.AutoRefreshTokenCallback;
import com.qiezitv.common.http.RetrofitManager;
import com.qiezitv.common.http.entity.ResponseEntity;
import com.qiezitv.http.request.FootballRequest;
import com.qiezitv.model.FootballEvent;
import com.qiezitv.model.FootballStatusEventData;
import com.qiezitv.model.FootballTimelineEventData;
import com.qiezitv.model.MatchStatusDetail;
import com.qiezitv.model.MatchVO;
import com.qiezitv.model.Page;
import com.qiezitv.model.PlayerVO;
import com.qiezitv.model.TimeLine;
import com.qiezitv.model.TimeLineVO;
import com.qiezitv.view.ResultEventItemView;
import com.qiezitv.view.WaitingDialog;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Response;

import static com.qiezitv.model.FootballEvent.CLEARANCE;
import static com.qiezitv.model.FootballEvent.CORNER;
import static com.qiezitv.model.FootballEvent.CROSS;
import static com.qiezitv.model.FootballEvent.EXTRA;
import static com.qiezitv.model.FootballEvent.FINISH;
import static com.qiezitv.model.FootballEvent.FOUL;
import static com.qiezitv.model.FootballEvent.FREE_KICK;
import static com.qiezitv.model.FootballEvent.GOAL;
import static com.qiezitv.model.FootballEvent.HALF_TIME;
import static com.qiezitv.model.FootballEvent.INJURY;
import static com.qiezitv.model.FootballEvent.LONG_PASS;
import static com.qiezitv.model.FootballEvent.OFFSIDE;
import static com.qiezitv.model.FootballEvent.OWN_GOAL;
import static com.qiezitv.model.FootballEvent.PAUSE;
import static com.qiezitv.model.FootballEvent.PENALTY;
import static com.qiezitv.model.FootballEvent.PENALTY_GOAL;
import static com.qiezitv.model.FootballEvent.PENALTY_KICK;
import static com.qiezitv.model.FootballEvent.RED;
import static com.qiezitv.model.FootballEvent.SAVE;
import static com.qiezitv.model.FootballEvent.SECOND_HALF;
import static com.qiezitv.model.FootballEvent.SHOOT;
import static com.qiezitv.model.FootballEvent.START;
import static com.qiezitv.model.FootballEvent.SUBSTITUTE;
import static com.qiezitv.model.FootballEvent.TACKLE;
import static com.qiezitv.model.FootballEvent.UNOPEN;
import static com.qiezitv.model.FootballEvent.YELLOW;

public class ResultFragment extends BaseFragment {
    private static final String TAG = ResultFragment.class.getSimpleName();

    private WaitingDialog waitingDialog;

    private Button btnBack;
    private TextView tvName, tvHostTeamName, tvScore, tvGuestTeamName, tvStatus, tvTimeMin;
    private ImageView ivHostTeamImg;
    private ImageView ivGuestTeamImg;
    private LinearLayout llResultContainer;

    // 删除事件框内容
    private DialogPlus deleteEventDialog;
    private TextView deleteTvEventName;
    private int lastDeleteIndex = -1;
    private boolean isDeleting = false;

    // 新增状态框内容
    private DialogPlus statusDetailDialog;
    private TextView statusDetailTvEventName, statusDetailCondition;
    private EditText statusDetailTvTime;
    private ImageView statusDetailIvTime;
    private ImageView statusDetailIvEvent;
    private int eventType;
    private String statusRemark;

    // 选择球队框内容
    private DialogPlus chooseTeamDialog;
    private long chooseTeamId = -1;
    private List<PlayerVO> choosePlayerList;
    private PlayerVO choosePlayer1;

    // 新增球赛事件内容
    private DialogPlus eventDetailDialog;
    private TextView eventDetailTvTime, eventDetailTvTeam, eventDetailTvPlayer, eventDetailTvEvent,
            eventDetailTvParam, eventDetailTvPlayer2;
    private ImageView eventDetailIvTeam, eventDetailIvPlayer, eventDetailIvEvent;
    private EditText eventDetailEtRemark;
    private PlayerVO choosePlayer2;
    private String timelineRemark;

    private List<PlayerVO> hostPlayerList;
    private List<PlayerVO> guestPlayerList;

    // 变更比分框内容
    private DialogPlus changeScoreDialog;
    private EditText etScore;
    private RadioGroup rgStatus;
    private RadioButton rbNotStart, rbStart, rbHalfTime, rbSecondHalf, rbFinish;
    private boolean isQueryOrChangeMatchVo = false;

    //定时刷新
    private Timer mTimer = new Timer();
    private TimerTask mTimerTask;
    private int SECOND = 10;

    private MatchVO matchVo;
    private MatchStatusDetail matchStatusDetail;

    private List<ResultEventItemView> resultEventItemViewList = new ArrayList<>();

    private OnAddEventListener onAddEventListener;
    private OnScoreAndStatusChangeListener onScoreAndStatusChangeListener;

    private FootballStatusEventData statusEventData;

    public void setOnAddEventListener(OnAddEventListener onAddEventListener) {
        this.onAddEventListener = onAddEventListener;
    }

    public void setOnScoreAndStatusChangeListener(OnScoreAndStatusChangeListener onScoreAndStatusChangeListener) {
        this.onScoreAndStatusChangeListener = onScoreAndStatusChangeListener;
    }

    private View.OnClickListener btnBackClickListener;
    private View.OnClickListener itemViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isDeleting) {
                showToast("正在删除其他数据，请稍后重试");
                return;
            }
            lastDeleteIndex = llResultContainer.indexOfChild(v);
            showDeleteEventDialog();
        }
    };

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_result;
    }

    @Override
    protected void onBindFragment(View view) {
        btnBack = view.findViewById(R.id.btn_back);
        tvName = view.findViewById(R.id.tv_name);
        ImageView ivEvent = view.findViewById(R.id.iv_event);
        ImageView ivStatus = view.findViewById(R.id.iv_status);
        ivHostTeamImg = view.findViewById(R.id.iv_host_team_img);
        tvHostTeamName = view.findViewById(R.id.tv_host_team_name);
        tvScore = view.findViewById(R.id.tv_score);
        tvTimeMin = view.findViewById(R.id.tv_time_min);
        tvStatus = view.findViewById(R.id.tv_status);
        ivGuestTeamImg = view.findViewById(R.id.iv_guest_team_img);
        tvGuestTeamName = view.findViewById(R.id.tv_guest_team_name);
        llResultContainer = view.findViewById(R.id.ll_result_container);
        LinearLayout llScore = view.findViewById(R.id.ll_score);
        llScore.setOnClickListener(v -> queryMatchVo());

        ivStatus.setOnClickListener(v -> showStatusDialog());
        ivEvent.setOnClickListener(v -> showEventDialog());

        if (btnBackClickListener != null) {
            btnBack.setOnClickListener(btnBackClickListener);
        }

        initView();
        queryTeamPlayerList();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initTimer();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    private void initTimer() {
        //定时刷新
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                queryMatchStatusDetail();
            }
        };
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        mTimer = new Timer();
        mTimer.schedule(mTimerTask, 0, SECOND * 1000);
    }

    public void initView() {
        if (matchVo != null) {
            tvName.setText(matchVo.getName());
            if (matchVo.getHostTeam() == null) {
                tvHostTeamName.setText("无");
            } else {
                tvHostTeamName.setText(matchVo.getHostTeam().getName());
                ImageLoader.getInstance().displayImage(matchVo.getHostTeam().getHeadImg(), ivHostTeamImg, ImageLoaderUtil.getOptions());
            }
            if (matchVo.getGuestTeam() == null) {
                tvGuestTeamName.setText("无");
            } else {
                ImageLoader.getInstance().displayImage(matchVo.getGuestTeam().getHeadImg(), ivGuestTeamImg, ImageLoaderUtil.getOptions());
                tvGuestTeamName.setText(matchVo.getGuestTeam().getName());
            }
        }

        if (matchStatusDetail != null) {
            tvScore.setText(matchStatusDetail.getScore());
            tvStatus.setText(FootballEvent.translateStatus(matchStatusDetail.getStatus()));

            List<TimeLineVO> timeLineVoList = matchStatusDetail.getTimeLines();
            boolean isTop = true, isBottom = false;
            Long hostTeamId = matchVo.getHostTeamId();
            for (int i = 0; i < timeLineVoList.size(); i++) {
                if (i == timeLineVoList.size() - 1) {
                    isBottom = true;
                }
                ResultEventItemView view = new ResultEventItemView(getContext(), timeLineVoList.get(i),
                        hostTeamId, isTop, isBottom);
                view.setOnClickListener(itemViewClickListener);
                llResultContainer.addView(view);
                resultEventItemViewList.add(view);
                if (i == 0) {
                    isTop = false;
                }
            }
        }
    }

    private void resetTimelineEvent() {
        statusRemark = null;
        chooseTeamId = -1;
        choosePlayer1 = null;
        choosePlayer2 = null;
        timelineRemark = null;
        if (eventDetailIvTeam != null) {
            eventDetailIvTeam.setImageResource(R.drawable.ic_user_picture);
        }
        if (eventDetailIvPlayer != null) {
            eventDetailIvPlayer.setImageResource(R.drawable.ic_user_picture);
        }
        if (eventDetailIvEvent != null) {
            eventDetailIvEvent.setImageResource(R.drawable.ic_result_start);
        }
        if (eventDetailTvPlayer != null) {
            eventDetailTvPlayer.setText("无");
        }
    }

    private void refreshMatchStatusDetailListView() {
        llResultContainer.removeAllViews();
        resultEventItemViewList.clear();
        if (matchStatusDetail != null) {
            tvScore.setText(matchStatusDetail.getScore());
            tvStatus.setText(FootballEvent.translateStatus(matchStatusDetail.getStatus()));

            List<TimeLineVO> timeLineVoList = matchStatusDetail.getTimeLines();
            boolean isTop = true, isBottom = false;
            Long hostTeamId = matchVo.getHostTeamId();
            for (int i = 0; i < timeLineVoList.size(); i++) {
                if (i == timeLineVoList.size() - 1) {
                    isBottom = true;
                }
                ResultEventItemView view = new ResultEventItemView(getContext(), timeLineVoList.get(i),
                        hostTeamId, isTop, isBottom);
                view.setOnClickListener(itemViewClickListener);
                llResultContainer.addView(view);
                resultEventItemViewList.add(view);
                if (i == 0) {
                    isTop = false;
                }
            }
        }
    }

    @Override
    protected void lazyLoad() {
    }

    public void setBtnBackOnClick(View.OnClickListener listener) {
        this.btnBackClickListener = listener;
        if (btnBack != null) {
            btnBack.setOnClickListener(btnBackClickListener);
        }
    }

    public void setMatchVo(MatchVO matchVo) {
        this.matchVo = matchVo;
    }

    public void setMatchStatusDetail(MatchStatusDetail matchStatusDetail) {
        this.matchStatusDetail = matchStatusDetail;
    }

    private void showDeleteEventDialog() {
        if (deleteEventDialog == null) {
            deleteEventDialog = DialogPlus.newDialog(getContext())
                    .setContentHolder(new ViewHolder(R.layout.dialog_delete_event_confirm))
                    .setContentBackgroundResource(R.drawable.shape_circle_bottom_popup)
                    .setGravity(Gravity.BOTTOM)
                    .setCancelable(true)
                    .setOnClickListener((dialog, view) -> {
                        dialog.dismiss();
                        if (view instanceof Button) {
                            dialog.dismiss();
                            switch (view.getId()) {
                                case R.id.btn_confirm:
                                    deleteEvent();
                                    break;
                                case R.id.btn_count:
                                    break;
                            }
                        }
                    })
                    .setExpanded(false)
                    .create();
            deleteTvEventName = (TextView) deleteEventDialog.findViewById(R.id.tv_event_name);
        }
        deleteTvEventName.setText(resultEventItemViewList.get(lastDeleteIndex).getDeleteEventName());
        deleteEventDialog.show();
    }

    private synchronized void deleteEvent() {
        isDeleting = true;
        TimeLineVO timeLineVo = matchStatusDetail.getTimeLines().get(lastDeleteIndex);
        FootballRequest request = RetrofitManager.getInstance().getRetrofit().create(FootballRequest.class);
        Call<ResponseEntity<Boolean>> response = request.deleteTimeLine(timeLineVo.getId());
        response.enqueue(new AutoRefreshTokenCallback<ResponseEntity<Boolean>>() {
            @Override
            public void onRefreshTokenFail() {
                isDeleting = false;
                gotoLoginActivity();
            }

            @Override
            public void onSuccess(ResponseEntity<Boolean> result) {
                isDeleting = false;
                if (result.getData()) {
                    if (getActivity() != null) {
                        // 删除成功
                        getActivity().runOnUiThread(() -> {
                            if (lastDeleteIndex == resultEventItemViewList.size() - 1 && resultEventItemViewList.size() > 2) {
                                resultEventItemViewList.get(resultEventItemViewList.size() - 2).refreshTopAndBottomView(false, true);
                            }
                            matchStatusDetail.getTimeLines().remove(lastDeleteIndex);
                            resultEventItemViewList.remove(lastDeleteIndex);
                            llResultContainer.removeViewAt(lastDeleteIndex);
                            if (lastDeleteIndex == 0 && resultEventItemViewList.size() > 1) {
                                resultEventItemViewList.get(0).refreshTopAndBottomView(true, false);
                            }
                        });
                    }
                } else {
                    Gson gson = new Gson();
                    showToast("删除请求失败:" + gson.toJson(result));
                }
            }

            @Override
            public void onFail(@Nullable Response<ResponseEntity<Boolean>> response, @Nullable Throwable t) {
                isDeleting = false;
                if (response != null) {
                    showToast("请求失败:" + (response.body() != null ? response.body().getMessage() : ""));
                }
                if (t != null) {
                    showToast("网络请求失败:" + t.getMessage());
                }
            }
        });
    }

    private void addEvent(TimeLine timeLine) {
        if (timeLine == null) {
            showToast("参数错误,请重新选择");
            return;
        }
        showWaitingDialog();
        FootballRequest request = RetrofitManager.getInstance().getRetrofit().create(FootballRequest.class);
        Call<ResponseEntity<Boolean>> response = request.addTimeLine(timeLine);
        response.enqueue(new AutoRefreshTokenCallback<ResponseEntity<Boolean>>() {
            @Override
            public void onRefreshTokenFail() {
                gotoLoginActivity();
            }

            @Override
            public void onSuccess(ResponseEntity<Boolean> result) {
                dismissWaitingDialog();
                Gson gson = new Gson();
                if (result.getData()) {
                    //通知添加了事件
                    if (onAddEventListener != null) {
                        onAddEventListener.onAddEvent(timeLine);
                    }// 新增成功
                    queryMatchStatusDetail();
                } else {
                    showToast("新增请求失败:" + gson.toJson(result));
                }
            }

            @Override
            public void onFail(@Nullable Response<ResponseEntity<Boolean>> response, @Nullable Throwable t) {
                dismissWaitingDialog();
                if (response != null) {
                    showToast("请求失败:" + (response.body() != null ? response.body().getMessage() : ""));
                }
                if (t != null) {
                    showToast("网络请求失败:" + t.getMessage());
                }
            }
        });
    }

    private void showStatusDialog() {
        resetTimelineEvent();
        eventType = -1;
        DialogPlus statusDialog = DialogPlus.newDialog(getContext())
                .setContentHolder(new ViewHolder(R.layout.dialog_game_status))
                .setContentBackgroundResource(R.drawable.shape_circle_bottom_popup)
                .setGravity(Gravity.BOTTOM)
                .setCancelable(true)
                .setOnDismissListener(dialog -> {
                    if (eventType != -1) {
                        showStatusDetailDialog();
                    }
                })
                .setOnClickListener((dialog, view) -> {
                    if (view instanceof LinearLayout) {
                        switch (view.getId()) {
                            case R.id.ll_start:
                                eventType = START;
                                break;
                            case R.id.ll_midfield:
                                eventType = HALF_TIME;
                                break;
                            case R.id.ll_second_half:
                                eventType = SECOND_HALF;
                                break;
                            case R.id.ll_injury:
                                eventType = INJURY;
                                break;
                            case R.id.ll_extra:
                                eventType = EXTRA;
                                break;
                            case R.id.ll_penalty:
                                eventType = PENALTY;
                                break;
                            case R.id.ll_finish:
                                eventType = FINISH;
                                break;
                            case R.id.ll_pause:
                                eventType = PAUSE;
                                break;
                        }
                    }
                    dialog.dismiss();
                })
                .setExpanded(false)
                .create();
        statusDialog.show();
    }

    private void showStatusDetailDialog() {
        //获取当前比赛时间
        Integer time = matchStatusDetail.getMinute();
        Map<Integer, FootballStatusEventData> eventDataMap = new HashMap<Integer, FootballStatusEventData>() {
            {
                put(START, new FootballStatusEventData(R.drawable.ic_result_start, "比赛开始", "开始于", 0));
                put(HALF_TIME, new FootballStatusEventData(R.drawable.ic_result_half_time, "中场", "开始于", matchVo.getHalfDuration()));
                put(SECOND_HALF, new FootballStatusEventData(R.drawable.ic_result_start, "下半场", "开始于", matchVo.getHalfDuration()));
                put(INJURY, new FootballStatusEventData(R.drawable.ic_result_injury, "伤停", "伤停分钟数", time));
                put(EXTRA, new FootballStatusEventData(R.drawable.ic_result_extra, "加时", "开始于", time));
                put(PENALTY, new FootballStatusEventData(R.drawable.ic_result_penalty, "点球大战", "开始于", 120));
                put(FINISH, new FootballStatusEventData(R.drawable.ic_result_finish, "比赛结束", "结束", 150));
                put(PAUSE, new FootballStatusEventData(R.drawable.ic_result_pause, "暂停", "暂停分钟数", time));
            }
        };
        if (!eventDataMap.containsKey(eventType)) {
            return;
        }
        statusEventData = eventDataMap.get(eventType);

        if (statusDetailDialog == null) {
            statusDetailDialog = DialogPlus.newDialog(getContext())
                    .setContentHolder(new ViewHolder(R.layout.dialog_status_detail))
                    .setContentBackgroundResource(R.drawable.shape_circle_popup)
                    .setGravity(Gravity.CENTER)
                    .setCancelable(true)
                    .setOnClickListener((dialog, view) -> {
                        switch (view.getId()) {
                            case R.id.ll_time:
                            case R.id.tv_time:
                                //暂停及伤停需要输入分钟
                                if (eventType != PAUSE && eventType != INJURY) {
                                    TimePickerView pvTime = new TimePickerBuilder(getContext(), (date, v) -> {
                                        if (date.after(new Date())) {
                                            showToast("选择时间不能超过当前时间，请重新选择");
                                            return;
                                        }
                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
                                        statusDetailTvTime.setText(simpleDateFormat.format(date));
                                        statusRemark = simpleDateFormat.format(date);
                                    }).setType(new boolean[]{true, true, true, true, true, true}).build();
                                    pvTime.show();
                                }
                                break;
                            case R.id.btn_confirm:
                                //暂停及伤停需要输入分钟
                                if (eventType == PAUSE || eventType == INJURY) {
                                    if (TextUtils.isEmpty(statusDetailTvTime.getText())) {
                                        showToast("请输入正确的数字");
                                        return;
                                    } else {
                                        try {
                                            Integer.parseInt(statusDetailTvTime.getText().toString().trim());
                                        } catch (NumberFormatException e) {
                                            showToast("请输入正确的数字");
                                            return;
                                        }
                                        statusRemark = statusDetailTvTime.getText().toString().trim();
                                    }
                                }
                                addEvent(createAddStatusEventBody(matchVo.getId(), eventType, statusEventData.getMinute(), statusRemark));
                                dialog.dismiss();
                                break;
                        }
                    })
                    .setExpanded(false)
                    .create();
            statusDetailIvEvent = (ImageView) statusDetailDialog.findViewById(R.id.iv_event_ic);
            statusDetailTvEventName = (TextView) statusDetailDialog.findViewById(R.id.tv_event_name);
            statusDetailTvTime = (EditText) statusDetailDialog.findViewById(R.id.tv_time);
            statusDetailIvTime = (ImageView) statusDetailDialog.findViewById(R.id.iv_time);
            statusDetailCondition = (TextView) statusDetailDialog.findViewById(R.id.tv_condition);
        }
        statusDetailIvEvent.setBackgroundResource(statusEventData.getBackgroundResourceId());
        statusDetailTvEventName.setText(statusEventData.getText());
        statusDetailCondition.setText(statusEventData.getConditionText());
        //暂停及伤停需要输入分钟
        if (eventType != PAUSE && eventType != INJURY) {
            statusDetailTvTime.setFocusable(false);
            statusDetailTvTime.setFocusableInTouchMode(false);
        } else {
            statusDetailTvTime.setFocusable(true);
            statusDetailTvTime.setFocusableInTouchMode(true);
            statusDetailTvTime.requestFocus();
        }
        //比赛结束不需要传时间
        if (eventType == FINISH) {
            statusDetailIvTime.setVisibility(View.INVISIBLE);
            statusDetailTvTime.setVisibility(View.INVISIBLE);
            statusDetailTvTime.setText(null);
            statusRemark = null;
        } else {
            statusDetailIvTime.setVisibility(View.VISIBLE);
            statusDetailTvTime.setVisibility(View.VISIBLE);
            Date nowDate = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
            statusDetailTvTime.setText(simpleDateFormat.format(nowDate));
            statusRemark = simpleDateFormat.format(nowDate);
        }
        //清空时间
//        statusDetailTvTime.setText(null);
        statusDetailDialog.show();
    }

    private TimeLine createAddStatusEventBody(Long matchId, Integer eventType, Integer minute, String remark) {
        if (matchId == null || eventType == null || minute == null) {
            return null;
        }
        TimeLine timeLine = new TimeLine();
        timeLine.setMatchId(matchId);
        timeLine.setEventType(eventType);
        timeLine.setMinute(minute);
        if (remark != null) {
            timeLine.setRemark(remark);
        }
        return timeLine;
    }

    private TimeLine createAddTimelineEventBody(Long matchId, Long teamId, Long playerId, Integer eventType, Integer minute, String remark, String text) {
        TimeLine timeLine = new TimeLine();
        timeLine.setMatchId(matchId);
        timeLine.setTeamId(teamId);
        timeLine.setEventType(eventType);
        timeLine.setMinute(minute);
        if (playerId != null) {
            timeLine.setPlayerId(playerId);
        }
        if (remark != null) {
            timeLine.setRemark(remark);
        }
        if (text != null) {
            timeLine.setText(text);
        }
        return timeLine;
    }

    private void showEventDialog() {
        resetTimelineEvent();
        eventType = -1;
        DialogPlus eventDialog = DialogPlus.newDialog(getContext())
                .setContentHolder(new ViewHolder(R.layout.dialog_game_event))
                .setContentBackgroundResource(R.drawable.shape_circle_bottom_popup)
                .setGravity(Gravity.BOTTOM)
                .setCancelable(true)
                .setOnDismissListener(dialog -> {
                    if (eventType != -1) {
                        showChooseTeamDialog();
                    }
                })
                .setOnClickListener((dialog, view) -> {
                    if (view instanceof LinearLayout) {
                        switch (view.getId()) {
                            case R.id.ll_goal:
                                eventType = GOAL;
                                break;
                            case R.id.ll_yellow_card:
                                eventType = YELLOW;
                                break;
                            case R.id.ll_red_card:
                                eventType = RED;
                                break;
                            case R.id.ll_corner:
                                eventType = CORNER;
                                break;
                            case R.id.ll_free_kick:
                                eventType = FREE_KICK;
                                break;
                            case R.id.ll_shoot:
                                eventType = SHOOT;
                                break;
                            case R.id.ll_penalty:
                                eventType = PENALTY_KICK;
                                break;
                            case R.id.ll_substitution:
                                eventType = SUBSTITUTE;
                                break;
                            case R.id.ll_save:
                                eventType = SAVE;
                                break;
                            case R.id.ll_offside:
                                eventType = OFFSIDE;
                                break;
                            case R.id.ll_tackle:
                                eventType = TACKLE;
                                break;
                            case R.id.ll_foul:
                                eventType = FOUL;
                                break;
                            case R.id.ll_own_goal:
                                eventType = OWN_GOAL;
                                break;
                            case R.id.ll_penalty_kick:
                                eventType = PENALTY_GOAL;
                                break;
                            case R.id.ll_clearance:
                                eventType = CLEARANCE;
                                break;
                            case R.id.ll_cross:
                                eventType = CROSS;
                                break;
                            case R.id.ll_long_pass:
                                eventType = LONG_PASS;
                                break;
                        }
                    }
                    dialog.dismiss();
                })
                .setExpanded(false)
                .create();
        eventDialog.show();
    }

    private void queryTeamPlayerList() {
        if (matchVo.getHostTeamId() == null || matchVo.getGuestTeamId() == null) {
            return;
        }
        FootballRequest request = RetrofitManager.getInstance().getRetrofit().create(FootballRequest.class);
        Call<ResponseEntity<Page<PlayerVO>>> response = request.getTeamPlayerList(matchVo.getHostTeamId(), 1, 200);
        response.enqueue(new AutoRefreshTokenCallback<ResponseEntity<Page<PlayerVO>>>() {
            @Override
            public void onRefreshTokenFail() {
                gotoLoginActivity();
            }

            @Override
            public void onSuccess(ResponseEntity<Page<PlayerVO>> result) {
                hostPlayerList = result.getData().getRecords();
            }

            @Override
            public void onFail(@Nullable Response<ResponseEntity<Page<PlayerVO>>> response, @Nullable Throwable t) {
                if (response != null) {
                    showToast("请求失败:" + (response.body() != null ? response.body().getMessage() : ""));
                }
                if (t != null) {
                    showToast("网络请求失败:" + t.getMessage());
                }
            }
        });

        response = request.getTeamPlayerList(matchVo.getGuestTeamId(), 1, 200);
        response.enqueue(new AutoRefreshTokenCallback<ResponseEntity<Page<PlayerVO>>>() {
            @Override
            public void onFailure(@NonNull Call<ResponseEntity<Page<PlayerVO>>> call, @NonNull Throwable t) {
                Log.e(TAG, t.getMessage());
            }

            @Override
            public void onRefreshTokenFail() {
                gotoLoginActivity();
            }

            @Override
            public void onSuccess(ResponseEntity<Page<PlayerVO>> result) {
                guestPlayerList = result.getData().getRecords();
            }

            @Override
            public void onFail(@Nullable Response<ResponseEntity<Page<PlayerVO>>> response, @Nullable Throwable t) {
                if (response != null) {
                    showToast("获取球队信息失败:" + (response.body() != null ? response.body().getMessage() : ""));
                }
                if (t != null) {
                    showToast("获取球队信息失败:" + t.getMessage());
                }
            }
        });
    }

    private void showChooseTeamDialog() {
        chooseTeamId = -1;
        choosePlayerList = null;
        if (chooseTeamDialog == null) {
            chooseTeamDialog = DialogPlus.newDialog(getContext())
                    .setContentHolder(new ViewHolder(R.layout.dialog_choose_team))
                    .setContentBackgroundResource(R.drawable.shape_circle_bottom_popup)
                    .setGravity(Gravity.BOTTOM)
                    .setCancelable(true)
                    .setOnDismissListener(dialog -> {
                        if (chooseTeamId != -1) {
                            //需要选择球员的事件
                            if (FootballTimelineEventData.timelineEventDataMap.get(eventType).isNeedPlayerId()) {
                                showChoosePlayerDialog();
                            } else {
                                if (getActivity() != null) {
                                    getActivity().runOnUiThread(this::showEventDetailDialog);
                                }
                            }
                        }
                    })
                    .setOnClickListener((dialog, view) -> {
                        if (matchVo.getHostTeamId() == null || matchVo.getGuestTeamId() == null) {
                            return;
                        }
                        switch (view.getId()) {
                            case R.id.tv_host_team_name:
                            case R.id.iv_host_team_img:
                                chooseTeamId = matchVo.getHostTeamId();
                                choosePlayerList = hostPlayerList;
                                break;
                            case R.id.tv_guest_team_name:
                            case R.id.iv_guest_team_img:
                                chooseTeamId = matchVo.getGuestTeamId();
                                choosePlayerList = guestPlayerList;
                                break;
                        }
                        dialog.dismiss();
                    })
                    .setExpanded(false)
                    .create();
            if (matchVo.getHostTeam() != null) {
                TextView tvHostTeamName = (TextView) chooseTeamDialog.findViewById(R.id.tv_host_team_name);
                tvHostTeamName.setText(matchVo.getHostTeam().getName());
                ImageView ivHostTeam = (ImageView) chooseTeamDialog.findViewById(R.id.iv_host_team_img);
                ImageLoader.getInstance().displayImage(matchVo.getHostTeam().getHeadImg(), ivHostTeam, ImageLoaderUtil.getOptions());
            }
            if (matchVo.getGuestTeam() != null) {
                TextView tvGuestTeamName = (TextView) chooseTeamDialog.findViewById(R.id.tv_guest_team_name);
                tvGuestTeamName.setText(matchVo.getGuestTeam().getName());
                ImageView ivGuestTeam = (ImageView) chooseTeamDialog.findViewById(R.id.iv_guest_team_img);
                ImageLoader.getInstance().displayImage(matchVo.getGuestTeam().getHeadImg(), ivGuestTeam, ImageLoaderUtil.getOptions());
            }
        }
        chooseTeamDialog.show();
    }

    private void showChoosePlayerDialog() {
        choosePlayer1 = null;
        DialogPlus choosePlayerDialog = DialogPlus.newDialog(getContext())
                .setContentHolder(new ViewHolder(R.layout.dialog_choose_player))
                .setContentBackgroundResource(R.drawable.shape_circle_bottom_popup)
                .setOnDismissListener(dialog -> {
                    if (choosePlayer1 != null) {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(this::showEventDetailDialog);
                        }
                    }
                })
                .setGravity(Gravity.BOTTOM)
                .setCancelable(true)
                .setExpanded(false)
                .create();
        TeamPlayerAdapter adapter = new TeamPlayerAdapter(getContext(), choosePlayerList);
        GridView gridView = (GridView) choosePlayerDialog.findViewById(R.id.grid_view);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            choosePlayer1 = choosePlayerList.get(position);
            choosePlayerDialog.dismiss();
        });

        choosePlayerDialog.show();
    }

    private void showChooseMinDialog() {
        List<Integer> options1Items = new ArrayList<>();
        for (int i = 0; i < 150; i++) {
            options1Items.add(i);
        }
        OptionsPickerView<Integer> pvOptions = new OptionsPickerBuilder(getContext(),
                (options1, option2, options3, v) -> eventDetailTvTime.setText(String.valueOf(options1))).build();
        pvOptions.setPicker(options1Items, null, null);
        pvOptions.show();
    }

    private void showChoosePlayer2Dialog() {
        List<String> options1Items = new ArrayList<>();
        List<PlayerVO> tempPlayerVOList = new ArrayList<>();
        for (PlayerVO playerVo : choosePlayerList) {
            if (playerVo.getId().equals(choosePlayer1.getId())) {
                continue;
            }
            tempPlayerVOList.add(playerVo);
            options1Items.add(playerVo.getName() + " " + playerVo.getShirtNum() + "号");
        }
        @SuppressLint("SetTextI18n") OptionsPickerView<String> pvOptions = new OptionsPickerBuilder(getContext(), (options1, option2, options3, v) -> {
            choosePlayer2 = tempPlayerVOList.get(options1);
            timelineRemark = String.valueOf(choosePlayer2.getId());
            eventDetailTvPlayer2.setText(choosePlayer2.getName() + " " + choosePlayer2.getShirtNum() + "号");
        }).build();
        pvOptions.setPicker(options1Items, null, null);
        pvOptions.show();
    }

    private void showChooseStatusDialog(Map<String, String> map) {
        List<String> options1Items = new ArrayList<>();
        List<String> valueList = new ArrayList<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String mapKey = entry.getKey();
            String mapValue = entry.getValue();
            options1Items.add(mapKey);
            valueList.add(mapValue);
        }
        @SuppressLint("SetTextI18n") OptionsPickerView<String> pvOptions = new OptionsPickerBuilder(getContext(), (options1, option2, options3, v) -> {
            timelineRemark = valueList.get(options1);
            eventDetailTvPlayer2.setText(options1Items.get(options1));
        }).build();
        pvOptions.setPicker(options1Items, null, null);
        pvOptions.show();
    }

    private void queryMatchStatusDetail() {
        FootballRequest request = RetrofitManager.getInstance().getRetrofit().create(FootballRequest.class);
        Call<ResponseEntity<MatchStatusDetail>> response = request.getMatchStatusDetailById(matchVo.getId());
        response.enqueue(new AutoRefreshTokenCallback<ResponseEntity<MatchStatusDetail>>() {
            @Override
            public void onRefreshTokenFail() {
                gotoLoginActivity();
            }

            @Override
            public void onSuccess(ResponseEntity<MatchStatusDetail> result) {
                matchStatusDetail = result.getData();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> refreshMatchStatusDetailListView());
                }
            }

            @Override
            public void onFail(@Nullable Response<ResponseEntity<MatchStatusDetail>> response, @Nullable Throwable t) {
                if (response != null) {
                    showToast("请求失败:" + (response.body() != null ? response.body().getMessage() : ""));
                }
                if (t != null) {
                    showToast("网络请求失败:" + t.getMessage());
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void showEventDetailDialog() {
        choosePlayer2 = null;
        if (eventDetailDialog == null) {
            eventDetailDialog = DialogPlus.newDialog(getContext())
                    .setContentHolder(new ViewHolder(R.layout.dialog_event_detail))
                    .setContentBackgroundResource(R.drawable.shape_circle_bottom_popup)
                    .setGravity(Gravity.BOTTOM)
                    .setCancelable(true)
                    .setOnDismissListener(dialog -> {

                    })
                    .setOnClickListener((dialog, view) -> {
                        switch (view.getId()) {
                            case R.id.iv_time:
                            case R.id.tv_time:
                                showChooseMinDialog();
                                break;
                            case R.id.tv_player2:
                                FootballTimelineEventData eventData = FootballTimelineEventData.timelineEventDataMap.get(eventType);
                                if (eventData.getRemarkName() != null && eventData.getRemarkValueMap() != null) {
                                    showChooseStatusDialog(eventData.getRemarkValueMap());
                                } else if (eventData.getRemarkName() != null && eventData.getRemarkValueMap() == null) {
                                    showChoosePlayer2Dialog();
                                }
                                break;
                            case R.id.btn_confirm:
                                String eventDetailTimeText = eventDetailTvTime.getText().toString();
                                if (TextUtils.isEmpty(eventDetailTimeText)) {
                                    showToast("请输入正确的分钟数");
                                    return;
                                }
                                Long playerId = null;
                                if (choosePlayer1 != null) {
                                    playerId = choosePlayer1.getId();
                                }
                                String text = null;
                                if (TextUtils.isEmpty(eventDetailEtRemark.getText())) {
                                    text = eventDetailEtRemark.getText().toString().trim();
                                }
                                if (eventType == SUBSTITUTE && (timelineRemark == null || timelineRemark.equalsIgnoreCase(""))) {
                                    showToast("请选择换上的队员");
                                    return;
                                }
                                addEvent(createAddTimelineEventBody(matchVo.getId(), chooseTeamId, playerId, eventType, Integer.parseInt(eventDetailTimeText), timelineRemark, text));
                                dialog.dismiss();
                                break;
                        }
                    })
                    .setExpanded(false)
                    .create();
            eventDetailIvTeam = (ImageView) eventDetailDialog.findViewById(R.id.iv_team);
            eventDetailTvTeam = (TextView) eventDetailDialog.findViewById(R.id.tv_team);
            eventDetailIvPlayer = (ImageView) eventDetailDialog.findViewById(R.id.iv_player);
            eventDetailTvPlayer = (TextView) eventDetailDialog.findViewById(R.id.tv_player);
            eventDetailIvEvent = (ImageView) eventDetailDialog.findViewById(R.id.iv_event);
            eventDetailTvEvent = (TextView) eventDetailDialog.findViewById(R.id.tv_event);
            eventDetailTvTime = (TextView) eventDetailDialog.findViewById(R.id.tv_time);
            eventDetailTvPlayer2 = (TextView) eventDetailDialog.findViewById(R.id.tv_player2);
            eventDetailEtRemark = (EditText) eventDetailDialog.findViewById(R.id.et_remark);
            eventDetailTvParam = (TextView) eventDetailDialog.findViewById(R.id.tv_param);
        }
        if (matchStatusDetail.getMinute() != null) {
            eventDetailTvTime.setText(matchStatusDetail.getMinute().toString());
        }

        eventDetailTvPlayer2.setText("请选择");
        eventDetailEtRemark.setText("");
        if (chooseTeamId == matchVo.getHostTeamId()) {
            eventDetailTvTeam.setText(matchVo.getHostTeam().getName());
            ImageLoader.getInstance().displayImage(matchVo.getHostTeam().getHeadImg(), eventDetailIvTeam, ImageLoaderUtil.getOptions());
        } else {
            eventDetailTvTeam.setText(matchVo.getGuestTeam().getName());
            ImageLoader.getInstance().displayImage(matchVo.getGuestTeam().getHeadImg(), eventDetailIvTeam, ImageLoaderUtil.getOptions());
        }
        if (choosePlayer1 != null) {
            eventDetailTvPlayer.setText(choosePlayer1.getName());
            if (choosePlayer1.getHeadImg() != null) {
                ImageLoader.getInstance().displayImage(choosePlayer1.getHeadImg(), eventDetailIvPlayer, ImageLoaderUtil.getOptions());
            }
        }
        FootballTimelineEventData eventData = FootballTimelineEventData.timelineEventDataMap.get(eventType);
        if (eventType != -1) {
            eventDetailTvEvent.setText(eventData.getText());
            eventDetailIvEvent.setImageResource(FootballEvent.getEventImageDrawable(eventType));
        }
        if (eventData.getRemarkName() != null) {
            eventDetailTvParam.setVisibility(View.VISIBLE);
            eventDetailTvPlayer2.setVisibility(View.VISIBLE);
            eventDetailTvParam.setText(eventData.getRemarkName());
        } else {
            eventDetailTvParam.setVisibility(View.INVISIBLE);
            eventDetailTvPlayer2.setVisibility(View.INVISIBLE);
        }
        eventDetailDialog.show();
    }

    private synchronized void queryMatchVo() {
        if (isQueryOrChangeMatchVo) {
            return;
        }
        isQueryOrChangeMatchVo = true;
        FootballRequest request = RetrofitManager.getInstance().getRetrofit().create(FootballRequest.class);
        Call<ResponseEntity<MatchVO>> response = request.getMatchById(matchVo.getId());
        response.enqueue(new AutoRefreshTokenCallback<ResponseEntity<MatchVO>>() {
            @Override
            public void onFailure(@NonNull Call<ResponseEntity<MatchVO>> call, @NonNull Throwable t) {
                Log.e(TAG, t.getMessage());
                showToast("获取比赛状态请求失败:" + t.getMessage());
            }

            @Override
            public void onRefreshTokenFail() {
                isQueryOrChangeMatchVo = false;
                gotoLoginActivity();
            }

            @Override
            public void onSuccess(ResponseEntity<MatchVO> result) {
                isQueryOrChangeMatchVo = false;
                matchVo = result.getData();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        tvScore.setText(matchVo.getScore());
                        tvStatus.setText(FootballEvent.translateStatus(matchVo.getStatus()));
                    });
                }
                showChangeScoreDialog();
            }

            @Override
            public void onFail(@Nullable Response<ResponseEntity<MatchVO>> response, @Nullable Throwable t) {
                isQueryOrChangeMatchVo = false;
                if (response != null) {
                    showToast("获取比赛状态请求失败:" + (response.body() != null ? response.body().getMessage() : ""));
                }
                if (t != null) {
                    showToast("获取比赛状态请求失败:" + t.getMessage());
                }
            }
        });
    }

    private void showChangeScoreDialog() {
        if (changeScoreDialog == null) {
            changeScoreDialog = DialogPlus.newDialog(getContext())
                    .setContentHolder(new ViewHolder(R.layout.dialog_change_scroe))
                    .setContentBackgroundResource(R.drawable.shape_circle_bottom_popup)
                    .setGravity(Gravity.BOTTOM)
                    .setCancelable(true)
                    .setOnClickListener((dialog, view) -> {
                        if (view.getId() == R.id.btn_confirm) {
                            if (TextUtils.isEmpty(etScore.getText().toString().trim())) {
                                showToast("输入的比分必须满足'X-X'格式");
                                return;
                            }
                            String[] temp = etScore.getText().toString().trim().split("-");
                            if (temp.length != 2) {
                                showToast("输入的比分必须满足'X-X'格式");
                                return;
                            }
                            try {
                                Integer.parseInt(temp[0]);
                                Integer.parseInt(temp[1]);
                            } catch (NumberFormatException e) {
                                showToast("输入的比分必须满足'X-X'格式");
                                return;
                            }

                            int selectedId = rgStatus.getCheckedRadioButtonId();
                            if (selectedId == -1) {
                                showToast("请选择比赛状态");
                                return;
                            }
                            switch (selectedId) {
                                case R.id.rb_not_start:
                                    eventType = UNOPEN;
                                    break;
                                case R.id.rb_start:
                                    eventType = START;
                                    break;
                                case R.id.rb_half_time:
                                    eventType = HALF_TIME;
                                    break;
                                case R.id.rb_second_half:
                                    eventType = SECOND_HALF;
                                    break;
                                case R.id.rb_finish:
                                    eventType = FINISH;
                                    break;
                            }
                            updateMatchScoreStatus();
                            dialog.dismiss();
                        }
                    })
                    .setExpanded(false)
                    .create();
            rgStatus = (RadioGroup) changeScoreDialog.findViewById(R.id.rg_status);
            etScore = (EditText) changeScoreDialog.findViewById(R.id.et_score);
            rbNotStart = (RadioButton) changeScoreDialog.findViewById(R.id.rb_not_start);
            rbStart = (RadioButton) changeScoreDialog.findViewById(R.id.rb_start);
            rbHalfTime = (RadioButton) changeScoreDialog.findViewById(R.id.rb_half_time);
            rbSecondHalf = (RadioButton) changeScoreDialog.findViewById(R.id.rb_second_half);
            rbFinish = (RadioButton) changeScoreDialog.findViewById(R.id.rb_finish);
        }
        rgStatus.clearCheck();
        etScore.setText(matchVo.getScore());
        switch (matchVo.getStatus()) {
            case UNOPEN:
                rbNotStart.setChecked(true);
                break;
            case START:
                rbStart.setChecked(true);
                break;
            case HALF_TIME:
                rbHalfTime.setChecked(true);
                break;
            case SECOND_HALF:
                rbSecondHalf.setChecked(true);
                break;
            case FINISH:
                rbFinish.setChecked(true);
                break;
        }
        changeScoreDialog.show();
    }

    private synchronized void updateMatchScoreStatus() {
        if (isQueryOrChangeMatchVo) {
            return;
        }
        isQueryOrChangeMatchVo = true;
        MatchVO paramMatchVO = new MatchVO();
        paramMatchVO.setId(matchVo.getId());
        paramMatchVO.setPenaltyScore(matchVo.getPenaltyScore());
        paramMatchVO.setStatus(eventType);
        paramMatchVO.setScore(etScore.getText().toString());
        FootballRequest request = RetrofitManager.getInstance().getRetrofit().create(FootballRequest.class);
        Call<ResponseEntity<Boolean>> response = request.updateMatchScoreStatus(paramMatchVO);
        response.enqueue(new AutoRefreshTokenCallback<ResponseEntity<Boolean>>() {
            @Override
            public void onRefreshTokenFail() {
                isQueryOrChangeMatchVo = false;
                gotoLoginActivity();
            }

            @Override
            public void onSuccess(ResponseEntity<Boolean> result) {
                isQueryOrChangeMatchVo = false;
                if (result.getData()) {
                    if (onScoreAndStatusChangeListener != null) {
                        onScoreAndStatusChangeListener.onScoreAndStatusChange(etScore.getText().toString().trim(), eventType);
                    }
                    queryMatchVo();
                    queryMatchStatusDetail();
                } else {
                    showToast("修改比赛状态请求失败:" + result.getMessage());
                }
            }

            @Override
            public void onFail(@Nullable Response<ResponseEntity<Boolean>> response, @Nullable Throwable t) {
                isQueryOrChangeMatchVo = false;
                if (response != null) {
                    showToast("修改比赛状态请求失败:" + (response.body() != null ? response.body().getMessage() : ""));
                }
                if (t != null) {
                    showToast("修改比赛状态请求失败:" + t.getMessage());
                }
            }
        });
    }

    //添加事件Listener
    public interface OnAddEventListener {
        void onAddEvent(TimeLine event);
    }

    public interface OnScoreAndStatusChangeListener {
        void onScoreAndStatusChange(String score, int eventType);
    }

    private void gotoLoginActivity() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                showToast("授权过期，请重新登录");
                Activity currentActivity = FinishActivityManager.getManager().currentActivity();
                readyGo(LoginActivity.class);
                FinishActivityManager.getManager().finishActivity(currentActivity);
                FinishActivityManager.getManager().finishActivity(MatchActivity.class);
                FinishActivityManager.getManager().finishActivity(MainActivity.class);
            });
        }
    }

    private void showWaitingDialog() {
        if (waitingDialog == null) {
            waitingDialog = new WaitingDialog(getActivity(), "");
            waitingDialog.setCanceledOnTouchOutside(false);
        }
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> waitingDialog.show());
        }
    }

    private void dismissWaitingDialog() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                if (waitingDialog != null) {
                    waitingDialog.dismiss();
                }
            });
        }
    }
}
