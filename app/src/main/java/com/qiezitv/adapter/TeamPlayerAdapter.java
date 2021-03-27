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
import com.qiezitv.model.PlayerVO;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;
import java.util.Locale;

public class TeamPlayerAdapter extends BaseAdapter {

    private List<PlayerVO> playerVOList;
    private LayoutInflater mInflater;

    public TeamPlayerAdapter(Context context, List<PlayerVO> playerVOList) {
        mInflater = LayoutInflater.from(context);
        this.playerVOList = playerVOList;
    }

    @Override
    public int getCount() {
        return playerVOList.size();
    }

    @Override
    public Object getItem(int position) {
        return playerVOList.get(position);
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
            convertView = mInflater.inflate(R.layout.item_player, null);
            viewHolder = new ViewHolder(convertView);

            //通过setTag将convertView与viewHolder关联
            convertView.setTag(viewHolder);
        } else {//如果缓存池中有对应的view缓存，则直接通过getTag取出viewHolder
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // 取出bean对象
        PlayerVO playerVo = playerVOList.get(position);
        ImageLoader.getInstance().displayImage(playerVo.getHeadImg(), viewHolder.ivUserImg, ImageLoaderUtil.getOptions());
        String nameAndNumStr = String.format(Locale.getDefault(), "%d号\n%s", playerVo.getShirtNum(), playerVo.getName());
        viewHolder.tvNameAndNum.setText(nameAndNumStr);
        return convertView;
    }

    public void setPlayerVOList(List<PlayerVO> playerVOList) {
        this.playerVOList = playerVOList;
    }

    static class ViewHolder {
        TextView tvNameAndNum;
        ImageView ivUserImg;

        public ViewHolder(View view) {
            tvNameAndNum = view.findViewById(R.id.tv_name_and_num);
            ivUserImg = view.findViewById(R.id.iv_user_img);
        }
    }

}
