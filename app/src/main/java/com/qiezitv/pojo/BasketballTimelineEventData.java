package com.qiezitv.pojo;

import com.qiezitv.R;

import java.util.HashMap;
import java.util.Map;

import static com.qiezitv.pojo.BasketballEvent.START;
import static com.qiezitv.pojo.BasketballEvent.NEXT_SETION;
import static com.qiezitv.pojo.BasketballEvent.PRE_SETION;
import static com.qiezitv.pojo.BasketballEvent.SWITCH_AGAINST;
import static com.qiezitv.pojo.BasketballEvent.FINISH;
import static com.qiezitv.pojo.BasketballEvent.GOAL_ONE;
import static com.qiezitv.pojo.BasketballEvent.GOAL_TWO;
import static com.qiezitv.pojo.BasketballEvent.GOAL_THREE;
import static com.qiezitv.pojo.BasketballEvent.GOAL_ONE_REVERSE;
import static com.qiezitv.pojo.BasketballEvent.GOAL_TWO_REVERSE;
import static com.qiezitv.pojo.BasketballEvent.GOAL_THREE_REVERSE;

public class BasketballTimelineEventData {
    private int backgroundResourceId;
    private String text;

    public BasketballTimelineEventData(int backgroundResourceId, String text) {
        this.backgroundResourceId = backgroundResourceId;
        this.text = text;
    }

    public int getBackgroundResourceId() {
        return backgroundResourceId;
    }

    public void setBackgroundResourceId(int backgroundResourceId) {
        this.backgroundResourceId = backgroundResourceId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public static Map<Integer, BasketballTimelineEventData> statusEventDataMap = new HashMap<Integer, BasketballTimelineEventData>() {
        {
            put(START, new BasketballTimelineEventData(R.drawable.ic_result_ball, "比赛开始"));
            put(NEXT_SETION, new BasketballTimelineEventData(R.drawable.ic_result_calendar, "下一节"));
            put(PRE_SETION, new BasketballTimelineEventData(R.drawable.ic_result_calendar, "上一节"));
            put(SWITCH_AGAINST, new BasketballTimelineEventData(R.drawable.ic_result_vest, "切换对阵"));
            put(FINISH, new BasketballTimelineEventData(R.drawable.ic_result_flag, "比赛结束"));
        }
    };
    public static Map<Integer, BasketballTimelineEventData> timelineEventDataMap = new HashMap<Integer, BasketballTimelineEventData>() {
        {
            put(GOAL_ONE, new BasketballTimelineEventData(R.drawable.ic_result_start, "一分球"));
            put(GOAL_TWO, new BasketballTimelineEventData(R.drawable.ic_result_calendar, "二分球"));
            put(GOAL_THREE, new BasketballTimelineEventData(R.drawable.ic_result_calendar, "三分球"));
            put(GOAL_ONE_REVERSE, new BasketballTimelineEventData(R.drawable.ic_result_start, "一分球撤销"));
            put(GOAL_TWO_REVERSE, new BasketballTimelineEventData(R.drawable.ic_result_calendar, "二分球撤销"));
            put(GOAL_THREE_REVERSE, new BasketballTimelineEventData(R.drawable.ic_result_calendar, "三分球撤销"));
        }
    };
}