package com.qiezitv.model;

import com.qiezitv.R;

public class FootballEvent {

    public static final int UNOPEN = -1;//0:比赛未开始
    public static final int START = 0;//0:比赛开始
    public static final int GOAL = 1;//1:进球
    // remark位 助攻球员
    public static final int SHOOT = 2;//2:射门
    // remark位 为空则普通射门
    // SHOOT_BLOCK = 1 射门被拦下
    // SHOOT_DOOR = 2 射到门框
    // SHOOT_OUT = 3 射偏
    public static final int OFFSIDE = 3;//3:越位
    public static final int TACKLE = 4;//4:抢断
    // remark位 为空则普通抢断
    // TACKLE_FAILED = 0
    // TACKLE_SUCCESS = 1
    public static final int FREE_KICK = 5;//5:任意球
    public static final int FOUL = 6;//6:犯规
    public static final int YELLOW = 7;//7:黄牌
    public static final int RED = 8;//8:红牌
    public static final int SAVE = 9;//9:扑救
    public static final int SUBSTITUTE = 10;//10:换人
    // remark位 换上的人
    public static final int EXTRA = 11;//11:加时
    public static final int PENALTY = 12;//12:点球大战开始
    public static final int INJURY = 13;//13:伤停
    // remark位伤停时间
    public static final int HALF_TIME = 14;//14:中场
    public static final int SECOND_HALF = 15;//15:下半场
    public static final int PAUSE = 16;//16:暂停
    public static final int CORNER = 17;//17:角球
    public static final int CROSS = 18;//18:传中
    // remark位 为空则普通传中
    // CROSS_FAILED = 0
    // CROSS_SUCCESS = 1
    public static final int LONG_PASS = 19;//19:长传
    public static final int CLEARANCE = 20;//20:解围
    public static final int FINISH = 21;//21:比赛结束
    public static final int OWN_GOAL = 22;//22:乌龙球
    public static final int PASS_POSSESSION = 23;//23:传球控球率
    public static final int PENALTY_KICK = 24;//24:点球
    // remark位 为空则普通点球
    // PENALTY_KICK_FAILED = 0
    // PENALTY_KICK_SUCCESS = 1
    public static final int PENALTY_GOAL = 25;//24:点球大战进球

    public static final int TEXT = 1000;//1000:文字描述

    public static String translateStatus(Integer status) {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case -1:
                return "未开始";
            case START:
                return "比赛开始";
            case EXTRA:
                return "加时";
            case PENALTY:
                return "点球大赛";
            case INJURY:
                return "伤停";
            case HALF_TIME:
                return "中场";
            case SECOND_HALF:
                return "下半场";
            case PAUSE:
                return "暂停";
            case FINISH:
                return "比赛结束";
            default:
                return "未知";
        }
    }

    public static int getEventImageDrawable(int eventType) {
        switch (eventType) {
            case START:
                return R.drawable.ic_result_start;
            case GOAL:
                return R.drawable.ic_result_goal;
            case SHOOT:
                return R.drawable.ic_result_shoot;
            case OFFSIDE:
                return R.drawable.ic_result_offside;
            case TACKLE:
                return R.drawable.ic_result_tackle;
            case FREE_KICK:
                return R.drawable.ic_result_free_kick;
            case FOUL:
                return R.drawable.ic_result_foul;
            case YELLOW:
                return R.drawable.ic_result_yellow_card;
            case RED:
                return R.drawable.ic_result_red_card;
            case SAVE:
                return R.drawable.ic_result_save;
            case SUBSTITUTE:
                return R.drawable.ic_result_substitution;
            case EXTRA:
                return R.drawable.ic_result_extra;
            case PENALTY:
                return R.drawable.ic_result_penalty;
            case INJURY:
                return R.drawable.ic_result_injury;
            case HALF_TIME:
                return R.drawable.ic_result_half_time;
            case SECOND_HALF:
                return R.drawable.ic_result_start;
            case PAUSE:
                return R.drawable.ic_result_pause;
            case CORNER:
                return R.drawable.ic_result_corner;
            case CROSS:
                return R.drawable.ic_result_cross;
            case LONG_PASS:
                return R.drawable.ic_result_long_pass;
            case CLEARANCE:
                return R.drawable.ic_result_clearance;
            case FINISH:
                return R.drawable.ic_result_finish;
            case OWN_GOAL:
                return R.drawable.ic_result_own_goal;
            case PASS_POSSESSION:
                return R.drawable.ic_result_tackle;
            case PENALTY_KICK:
                return R.drawable.ic_result_penalty;
            case PENALTY_GOAL:
                return R.drawable.ic_result_penalty;
        }
        return -1;
    }
}