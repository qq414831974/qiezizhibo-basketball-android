package com.qiezitv.view;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import com.qiezitv.R;
import com.qiezitv.common.ImageLoaderUtil;
import com.qiezitv.model.ScoreBoard;
import com.qiezitv.model.ScoreboardDetail;
import com.nostra13.universalimageloader.core.ImageLoader;


public class ScoreBoardView extends BaseScoreBoardView {
    private static final int SCORE_BOARD_WIDTH = 900;
    private static final int SCORE_BOARD_HEIGHT = 180;

    public ScoreBoardView(Context context) {
        super(context, R.layout.view_score_board);
    }

    public ScoreBoardView initPositionWithScoreBoard(ScoreBoard scoreBoard) {
        ScoreboardDetail detail = scoreBoard.getDetail();
        setViewXY(tvTitle, Math.round(detail.getTitle().getX() * SCORE_BOARD_WIDTH), Math.round(detail.getTitle().getY() * SCORE_BOARD_HEIGHT));
        setViewXY(tvScoreLeft, Math.round(detail.getHostscore().getX() * SCORE_BOARD_WIDTH), Math.round(detail.getHostscore().getY() * SCORE_BOARD_HEIGHT));
        setViewXY(tvScoreRight, Math.round(detail.getGuestscore().getX() * SCORE_BOARD_WIDTH), Math.round(detail.getGuestscore().getY() * SCORE_BOARD_HEIGHT));
        setViewXY(tvTeamNameLeft, Math.round(detail.getHostname().getX() * SCORE_BOARD_WIDTH), Math.round(detail.getHostname().getY() * SCORE_BOARD_HEIGHT));
        setViewXY(tvTeamNameRight, Math.round(detail.getGuestname().getX() * SCORE_BOARD_WIDTH), Math.round(detail.getGuestname().getY() * SCORE_BOARD_HEIGHT));
        setViewXY(tvTime, Math.round(detail.getTime().getX() * SCORE_BOARD_WIDTH), Math.round(detail.getTime().getY() * SCORE_BOARD_HEIGHT));
        setViewXY(ivHostColor, Math.round(detail.getHostshirt().getX() * SCORE_BOARD_WIDTH), Math.round(detail.getHostshirt().getY() * SCORE_BOARD_HEIGHT));
        setViewXY(ivGuestColor, Math.round(detail.getGuestshirt().getX() * SCORE_BOARD_WIDTH), Math.round(detail.getGuestshirt().getY() * SCORE_BOARD_HEIGHT));
        ImageLoader.getInstance().displayImage(detail.getHostshirtpic(), ivHostColor, ImageLoaderUtil.getOptions());
        ImageLoader.getInstance().displayImage(detail.getGuestshirtpic(), ivGuestColor, ImageLoaderUtil.getOptions());
        ImageLoader.getInstance().displayImage(detail.getScoreboardpic(), ivScoreBoard, ImageLoaderUtil.getOptions());
        return this;
    }

    public void setViewXY(View view, int x, int y) {
        FrameLayout.LayoutParams lp = (LayoutParams) view.getLayoutParams();
        lp.leftMargin = x;
        lp.topMargin = y;
        view.setLayoutParams(lp);
    }
}
