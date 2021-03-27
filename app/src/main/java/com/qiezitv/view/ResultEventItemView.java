package com.qiezitv.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qiezitv.R;
import com.qiezitv.common.ImageLoaderUtil;
import com.qiezitv.model.PlayerVO;
import com.qiezitv.model.TimeLineVO;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Locale;

import static com.qiezitv.model.FootballEvent.*;

@SuppressLint("ViewConstructor")
public class ResultEventItemView extends LinearLayout {

    private Context context;
    private TextView tvHostPlayers2Name, tvHostPlayers1Name, tvHostTimeMin, tvEventName,
            tvGuestPlayers2Name, tvGuestPlayers1Name, tvGuestTimeMin;
    private ImageView ivHostPlayers2Img, ivHostPlayers1Img, ivEventIc, ivGuestPlayers2Img,
            ivGuestPlayers1Img, ivHostDoubleArrow, ivGuestDoubleArrow;
    private View lintTop, lintBottom;

    private TimeLineVO timeLineVo;
    private boolean isTop;
    private boolean isBottom;
    private Long hostTeamId;

    public ResultEventItemView(Context context, TimeLineVO timeLineVo, Long hostTeamId,
                               boolean isTop, boolean isBottom) {
        super(context);
        this.context = context;
        this.timeLineVo = timeLineVo;
        this.isTop = isTop;
        this.isBottom = isBottom;
        this.hostTeamId = hostTeamId;
        init();
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        View view = LayoutInflater.from(context).inflate(R.layout.item_result_event, this, true);
        tvHostPlayers2Name = view.findViewById(R.id.tv_host_players_2_name);
        tvHostPlayers1Name = view.findViewById(R.id.tv_host_players_1_name);
        tvHostTimeMin = view.findViewById(R.id.tv_host_time_min);
        tvEventName = view.findViewById(R.id.tv_event_name);
        tvGuestPlayers2Name = view.findViewById(R.id.tv_guest_players_2_name);
        tvGuestPlayers1Name = view.findViewById(R.id.tv_guest_players_1_name);
        tvGuestTimeMin = view.findViewById(R.id.tv_guest_time_min);
        ivHostPlayers2Img = view.findViewById(R.id.iv_host_players_2_img);
        ivHostPlayers1Img = view.findViewById(R.id.iv_host_players_1_img);
        ivEventIc = view.findViewById(R.id.iv_event_ic);
        ivGuestPlayers2Img = view.findViewById(R.id.iv_guest_players_2_img);
        ivGuestPlayers1Img = view.findViewById(R.id.iv_guest_players_1_img);
        lintTop = view.findViewById(R.id.line_top);
        lintBottom = view.findViewById(R.id.line_bottom);
        ivHostDoubleArrow = view.findViewById(R.id.iv_host_double_arrow);
        ivGuestDoubleArrow = view.findViewById(R.id.iv_guest_double_arrow);

        if (isTop) {
            lintTop.setVisibility(INVISIBLE);
        }
        if (isBottom) {
            lintBottom.setVisibility(INVISIBLE);
        }
        if (timeLineVo.getEventType() == null) {
            Toast.makeText(context, "timeLineVo.getEventType() == null", Toast.LENGTH_LONG).show();
            return;
        }

        Gson gson = new Gson();
        int ivEventIcRes = -1;
        String eventName = null;
        boolean isShowHostPlayers2 = false, isShowHostPlayers1 = false, isShowHostDoubleArrow = false,
                isShowHostTimeMin = false;
        boolean isShowGuestPlayers2 = false, isShowGuestPlayers1 = false, isShowGuestDoubleArrow = false,
                isShowGuestTimeMin = false;
        boolean isHostTeam = false;
        if (hostTeamId.equals(timeLineVo.getTeamId())) {
            isHostTeam = true;
        }
        switch (timeLineVo.getEventType()) {
            case START:
                ivEventIcRes = R.drawable.ic_result_start;
                eventName = "比赛开始";
                break;
            case GOAL:
                ivEventIcRes = R.drawable.ic_result_goal;
                eventName = "进球";
                if (isHostTeam) {
                    isShowHostPlayers1 = true;
                    isShowHostTimeMin = true;
                } else {
                    isShowGuestPlayers1 = true;
                    isShowGuestTimeMin = true;
                }
                break;
            case SHOOT:
                ivEventIcRes = R.drawable.ic_result_shoot;
                eventName = "射门";
                if (isHostTeam) {
                    isShowHostPlayers1 = true;
                    isShowHostTimeMin = true;
                } else {
                    isShowGuestPlayers1 = true;
                    isShowGuestTimeMin = true;
                }
                break;
            case OFFSIDE:
                ivEventIcRes = R.drawable.ic_result_offside;
                eventName = "越位";
                if (isHostTeam) {
                    isShowHostTimeMin = true;
                } else {
                    isShowGuestTimeMin = true;
                }
                break;
            case TACKLE:
                ivEventIcRes = R.drawable.ic_result_tackle;
                eventName = "抢断";
                if (isHostTeam) {
                    isShowHostTimeMin = true;
                } else {
                    isShowGuestTimeMin = true;
                }
                break;
            case FREE_KICK:
                ivEventIcRes = R.drawable.ic_result_free_kick;
                eventName = "任意球";
                if (isHostTeam) {
                    isShowHostTimeMin = true;
                } else {
                    isShowGuestTimeMin = true;
                }
                break;
            case FOUL:
                ivEventIcRes = R.drawable.ic_result_foul;
                eventName = "犯规";
                if (isHostTeam) {
                    isShowHostTimeMin = true;
                } else {
                    isShowGuestTimeMin = true;
                }
                break;
            case YELLOW:
                ivEventIcRes = R.drawable.ic_result_yellow_card;
                eventName = "黄牌";
                if (isHostTeam) {
                    isShowHostPlayers1 = true;
                    isShowHostTimeMin = true;
                } else {
                    isShowGuestPlayers1 = true;
                    isShowGuestTimeMin = true;
                }
                break;
            case RED:
                ivEventIcRes = R.drawable.ic_result_red_card;
                eventName = "红牌";
                if (isHostTeam) {
                    isShowHostPlayers1 = true;
                    isShowHostTimeMin = true;
                } else {
                    isShowGuestPlayers1 = true;
                    isShowGuestTimeMin = true;
                }
                break;
            case SAVE:
                ivEventIcRes = R.drawable.ic_result_save;
                eventName = "扑救";
                if (isHostTeam) {
                    isShowHostTimeMin = true;
                } else {
                    isShowGuestTimeMin = true;
                }
                break;
            case SUBSTITUTE:
                ivEventIcRes = R.drawable.ic_result_substitution;
                eventName = "换人";
                if (isHostTeam) {
                    isShowHostPlayers1 = true;
                    isShowHostTimeMin = true;
                    isShowHostDoubleArrow = true;
                    isShowHostPlayers2 = true;
                } else {
                    isShowGuestPlayers1 = true;
                    isShowGuestTimeMin = true;
                    isShowGuestDoubleArrow = true;
                    isShowGuestPlayers2 = true;
                }
                break;
            case EXTRA:
                ivEventIcRes = R.drawable.ic_result_extra;
                eventName = "加时";
                break;
            case PENALTY:
                ivEventIcRes = R.drawable.ic_result_penalty;
                eventName = "点球大战";
                break;
            case INJURY:
                ivEventIcRes = R.drawable.ic_result_injury;
                eventName = "伤停";
                break;
            case HALF_TIME:
                ivEventIcRes = R.drawable.ic_result_half_time;
                eventName = "中场";
                break;
            case SECOND_HALF:
                ivEventIcRes = R.drawable.ic_result_half_time;
                eventName = "下半场";
                break;
            case PAUSE:
                ivEventIcRes = R.drawable.ic_result_pause;
                eventName = "暂停";
                break;
            case CORNER:
                ivEventIcRes = R.drawable.ic_result_corner;
                eventName = "角球";
                if (isHostTeam) {
                    isShowHostTimeMin = true;
                } else {
                    isShowGuestTimeMin = true;
                }
                break;
            case CROSS:
                ivEventIcRes = R.drawable.ic_result_cross;
                eventName = "传中";
                if (isHostTeam) {
                    isShowHostTimeMin = true;
                } else {
                    isShowGuestTimeMin = true;
                }
                break;
            case LONG_PASS:
                ivEventIcRes = R.drawable.ic_result_long_pass;
                eventName = "长传";
                if (isHostTeam) {
                    isShowHostTimeMin = true;
                } else {
                    isShowGuestTimeMin = true;
                }
                break;
            case CLEARANCE:
                ivEventIcRes = R.drawable.ic_result_clearance;
                eventName = "解围";
                if (isHostTeam) {
                    isShowHostTimeMin = true;
                } else {
                    isShowGuestTimeMin = true;
                }
                break;
            case FINISH:
                ivEventIcRes = R.drawable.ic_result_finish;
                eventName = "比赛结束";
                break;
            case OWN_GOAL:
                ivEventIcRes = R.drawable.ic_result_own_goal;
                eventName = "乌龙球";
                if (isHostTeam) {
                    isShowHostPlayers1 = true;
                    isShowHostTimeMin = true;
                } else {
                    isShowGuestPlayers1 = true;
                    isShowGuestTimeMin = true;
                }
                break;
            case PENALTY_KICK:
                ivEventIcRes = R.drawable.ic_result_penalty;
                eventName = "点球";
                break;
            case PENALTY_GOAL:
                eventName = "点球大战进球";
                ivEventIcRes = R.drawable.ic_result_penalty;
                break;
            default:
                break;
        }
        if (ivEventIcRes != -1) {
            ivEventIc.setBackground(getContext().getDrawable(ivEventIcRes));
        }
        if (eventName != null) {
            tvEventName.setText(eventName);
        }
        if (isShowHostTimeMin) {
            tvHostTimeMin.setVisibility(VISIBLE);
            tvHostTimeMin.setText(timeLineVo.getMinute() + "'");
        }
        if (isShowHostPlayers1) {
            ivHostPlayers1Img.setVisibility(VISIBLE);
            displayImage(timeLineVo.getPlayer().getHeadImg(), ivHostPlayers1Img);
            tvHostPlayers1Name.setVisibility(VISIBLE);
            tvHostPlayers1Name.setText(timeLineVo.getPlayer().getName());
        }
        if (isShowHostDoubleArrow) {
            ivHostDoubleArrow.setVisibility(VISIBLE);
        }
        if (isShowHostPlayers2) {
            ivHostPlayers2Img.setVisibility(VISIBLE);
            tvHostPlayers2Name.setVisibility(VISIBLE);
            try {
                PlayerVO playerVO2 = timeLineVo.getSecondPlayer();
                if (playerVO2 != null) {
                    displayImage(playerVO2.getHeadImg(), ivHostPlayers2Img);
                    tvHostPlayers2Name.setText(playerVO2.getName());
                }
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        }

        if (isShowGuestTimeMin) {
            tvGuestTimeMin.setVisibility(VISIBLE);
            tvGuestTimeMin.setText(timeLineVo.getMinute() + "'");
        }
        if (isShowGuestPlayers1) {
            ivGuestPlayers1Img.setVisibility(VISIBLE);
            displayImage(timeLineVo.getPlayer().getHeadImg(), ivGuestPlayers1Img);
            tvGuestPlayers1Name.setVisibility(VISIBLE);
            tvGuestPlayers1Name.setText(timeLineVo.getPlayer().getName());
        }
        if (isShowGuestDoubleArrow) {
            ivGuestDoubleArrow.setVisibility(VISIBLE);
        }
        if (isShowGuestPlayers2) {
            ivGuestPlayers2Img.setVisibility(VISIBLE);
            tvGuestPlayers2Name.setVisibility(VISIBLE);
            try {
                PlayerVO playerVO2 = timeLineVo.getSecondPlayer();
                displayImage(playerVO2.getHeadImg(), ivGuestPlayers2Img);
                tvGuestPlayers2Name.setText(playerVO2.getName());
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    public void refreshTopAndBottomView(boolean isTop, boolean isBottom) {
        this.isTop = isTop;
        this.isBottom = isBottom;
        if (isTop) {
            lintTop.setVisibility(INVISIBLE);
        }
        if (isBottom) {
            lintBottom.setVisibility(INVISIBLE);
        }
    }

    private void displayImage(String uri, ImageView imageView) {
        if (uri == null) {
            return;
        }
        ImageLoader.getInstance().displayImage(uri, imageView, ImageLoaderUtil.getOptions());
    }

    public String getDeleteEventName() {
        return String.format(Locale.getDefault(), "%s在%d分钟", tvEventName.getText().toString(), timeLineVo.getMinute());
    }

}
