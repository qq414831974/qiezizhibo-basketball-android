package com.qiezitv.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.qiezitv.R;


public abstract class BaseScoreBoardView extends FrameLayout {

    protected Context context;
    protected TextView tvTitle;
    protected TextView tvTeamNameLeft;
    protected TextView tvTeamNameRight;
    protected TextView tvScoreLeft;
    protected TextView tvScoreRight;
    protected TextView tvTime;
    protected ImageView ivScoreBoard;
    protected ImageView ivHostColor;
    protected ImageView ivGuestColor;

    public BaseScoreBoardView(Context context, int resource) {
        super(context);
        this.context = context;
        init(context, resource);
    }

    private void init(Context context, int resource) {
        View view = LayoutInflater.from(context).inflate(resource, this, true);
        ivScoreBoard = view.findViewById(R.id.iv_scoreboard);
        tvTitle = view.findViewById(R.id.tv_title);
        tvTeamNameLeft = view.findViewById(R.id.tv_team_name_left);
        tvTeamNameRight = view.findViewById(R.id.tv_team_name_right);
        tvScoreLeft = view.findViewById(R.id.tv_score_left);
        tvScoreRight = view.findViewById(R.id.tv_score_right);
        tvTime = view.findViewById(R.id.tv_time);
        ivHostColor = view.findViewById(R.id.iv_host_color);
        ivGuestColor = view.findViewById(R.id.iv_guest_color);
    }

    public void setTitle(String title) {
        if (tvTitle == null) {
            return;
        }
        if (title != null && title.length() > 12) {
            float textsize = tvTeamNameLeft.getTextSize() - (title.length() - 12) * 2.5f;
            tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, 28);
        } else {
            tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, 33);
        }
        tvTitle.setText(title);
    }

    public void setTeamNameHost(String teamName) {
        if (tvTeamNameLeft == null) {
            return;
        }
        if (teamName != null && teamName.length() > 9) {
            float textsize = tvTeamNameLeft.getTextSize() - (teamName.length() - 9) * 2.5f;
            tvTeamNameLeft.setTextSize(TypedValue.COMPLEX_UNIT_PX, textsize);
        }
        tvTeamNameLeft.setText(teamName);
    }

    public void setTeamNameGuest(String teamName) {
        if (tvTeamNameRight == null) {
            return;
        }
        if (teamName != null && teamName.length() > 9) {
            float textsize = tvTeamNameRight.getTextSize() - (teamName.length() - 9) * 2.5f;
            tvTeamNameRight.setTextSize(TypedValue.COMPLEX_UNIT_PX, textsize);
        }
        tvTeamNameRight.setText(teamName);
    }

    public void setScoreLeft(String score) {
        if (tvScoreLeft == null) {
            return;
        }
        tvScoreLeft.setText(score);
    }

    public void setScoreRight(String score) {
        if (tvScoreRight == null) {
            return;
        }
        tvScoreRight.setText(score);
    }

    public void setTime(String time) {
        if (tvTime == null) {
            return;
        }
        tvTime.setText(time);
    }

    public void setHostColor(int color) {
        if (ivHostColor == null) {
            return;
        }
        ivHostColor.setColorFilter(color);
    }

    public void setGuestColor(int color) {
        if (ivGuestColor == null) {
            return;
        }
        ivGuestColor.setColorFilter(color);
    }

    public Bitmap getBitmap() {
        Bitmap bitmap = null;
        //开启view缓存bitmap
        setDrawingCacheEnabled(true);
        //设置view缓存Bitmap质量
        setDrawingCacheQuality(DRAWING_CACHE_QUALITY_HIGH);
        //获取缓存的bitmap
        Bitmap cache = getDrawingCache();
        if (cache != null && !cache.isRecycled()) {
            bitmap = Bitmap.createBitmap(cache);
        }
        //销毁view缓存bitmap
        destroyDrawingCache();
        //关闭view缓存bitmap
        setDrawingCacheEnabled(false);
        return bitmap;
    }

    public static Bitmap getBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.RGB_565);
        Canvas c = new Canvas(b);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        // Draw background
        Drawable bgDrawable = v.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(c);
        else
            c.drawColor(Color.WHITE);
        // Draw view to canvas
        v.draw(c);
        return b;
    }
}
