package com.qiezitv.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.qiezitv.R;
import com.qiezitv.common.ImageLoaderUtil;
import com.qiezitv.model.ScoreBoard;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class ScoreBoardAdapter extends BaseAdapter {

    private List<ScoreBoard> scoreBoardList;
    private LayoutInflater mInflater;

    public ScoreBoardAdapter(Context context, List<ScoreBoard> scoreBoardList) {
        mInflater = LayoutInflater.from(context);
        this.scoreBoardList = scoreBoardList;
    }

    @Override
    public int getCount() {
        return scoreBoardList.size();
    }

    @Override
    public Object getItem(int position) {
        return scoreBoardList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        //如果view未被实例化过，缓存池中没有对应的缓存
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_scoreboard, null);
            viewHolder = new ViewHolder(convertView);

            //通过setTag将convertView与viewHolder关联
            convertView.setTag(viewHolder);
        } else {//如果缓存池中有对应的view缓存，则直接通过getTag取出viewHolder
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // 取出bean对象
        ScoreBoard scoreBoard = scoreBoardList.get(position);
        ImageLoader.getInstance().displayImage(scoreBoard.getDetail().getScoreboardpic(), viewHolder.ivScoreboard, ImageLoaderUtil.getOptions());
        return convertView;
    }

    public void setPlayerVoList(List<ScoreBoard> scoreBoardList) {
        this.scoreBoardList = scoreBoardList;
    }

    static class ViewHolder {
        ImageView ivScoreboard;

        public ViewHolder(View view) {
            ivScoreboard = view.findViewById(R.id.iv_scoreboard_item);
        }
    }

}
