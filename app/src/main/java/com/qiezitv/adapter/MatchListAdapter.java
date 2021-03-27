package com.qiezitv.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.qiezitv.R;
import com.qiezitv.common.ImageLoaderUtil;
import com.qiezitv.model.FootballEvent;
import com.qiezitv.model.MatchVO;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MatchListAdapter extends BaseAdapter {

    private List<MatchVO> mData;
    private LayoutInflater mInflater;

    public MatchListAdapter(Context context, List<MatchVO> data) {
        mData = data;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
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
            convertView = mInflater.inflate(R.layout.item_match, null);
            viewHolder = new ViewHolder(convertView);

            //通过setTag将convertView与viewHolder关联
            convertView.setTag(viewHolder);
        } else {//如果缓存池中有对应的view缓存，则直接通过getTag取出viewHolder
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // 取出bean对象
        MatchVO matchVo = mData.get(position);

        viewHolder.tvName.setText(matchVo.getName());
        if (matchVo.getHostTeam() == null) {
            viewHolder.tvHostTeamName.setText("无");
        } else {
            viewHolder.tvHostTeamName.setText(matchVo.getHostTeam().getName());
            ImageLoader.getInstance().displayImage(matchVo.getHostTeam().getHeadImg(), viewHolder.ivHostTeamImg, ImageLoaderUtil.getOptions());
        }
        viewHolder.tvScore.setText(matchVo.getScore());
        if (matchVo.getGuestTeam() == null) {
            viewHolder.tvGuestTeamName.setText("无");
        } else {
            ImageLoader.getInstance().displayImage(matchVo.getGuestTeam().getHeadImg(), viewHolder.ivGuestTeamImg, ImageLoaderUtil.getOptions());
            viewHolder.tvGuestTeamName.setText(matchVo.getGuestTeam().getName());
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        viewHolder.tvTime.setText(simpleDateFormat.format(matchVo.getStartTime()));
        viewHolder.tvLocation.setText(matchVo.getPlace());
        viewHolder.tvStatus.setText(FootballEvent.translateStatus(matchVo.getStatus()));

        return convertView;
    }

    public List<MatchVO> getData() {
        return mData;
    }

    public void setData(List<MatchVO> mData) {
        this.mData = mData;
    }

    static class ViewHolder {
        TextView tvName, tvHostTeamName, tvScore, tvGuestTeamName, tvTime, tvLocation, tvStatus;
        ImageView ivHostTeamImg, ivGuestTeamImg;

        public ViewHolder(View view) {
            tvName = view.findViewById(R.id.tv_name);
            tvHostTeamName = view.findViewById(R.id.tv_host_team_name);
            tvScore = view.findViewById(R.id.tv_score);
            tvGuestTeamName = view.findViewById(R.id.tv_guest_team_name);
            tvTime = view.findViewById(R.id.tv_time);
            tvLocation = view.findViewById(R.id.tv_location);
            tvStatus = view.findViewById(R.id.tv_status);
            ivHostTeamImg = view.findViewById(R.id.iv_host_team_img);
            tvGuestTeamName = view.findViewById(R.id.tv_guest_team_name);
            ivGuestTeamImg = view.findViewById(R.id.iv_guest_team_img);
        }
    }
}
