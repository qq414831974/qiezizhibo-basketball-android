package com.qiezitv.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.qiezitv.R;
import com.qiezitv.activity.GameResultActivity;
import com.qiezitv.activity.GameVideoActivity;
import com.qiezitv.activity.LoginActivity;
import com.qiezitv.activity.MainActivity;
import com.qiezitv.adapter.MatchListAdapter;
import com.qiezitv.common.FinishActivityManager;
import com.qiezitv.common.http.AutoRefreshTokenCallback;
import com.qiezitv.common.http.RetrofitManager;
import com.qiezitv.common.http.entity.ResponseEntity;
import com.qiezitv.http.request.FootballRequest;
import com.qiezitv.model.MatchStatusDetail;
import com.qiezitv.model.MatchVO;
import com.qiezitv.view.WaitingDialog;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Response;

public class MatchListFragment extends BaseFragment {
    private static final String TAG = MatchListFragment.class.getSimpleName();

    private TextView tvNotDataHint;
    private ListView lvMatchList;
    private MatchListAdapter adapter;
    private WaitingDialog waitingDialog;

    private List<MatchVO> matchVOList;
    private DialogPlus dialog;
    private TextView dialogTvName;
    private TextView dialogTvTime;
    private TextView dialogBtnLive;
    private TextView dialogBtnLiveBasketball;

    private MatchVO matchVo;

    private Class<?> chooseClazz;
    private boolean isBasketball = false;

    public String round;


    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_match_list;
    }

    @Override
    protected void onBindFragment(View view) {
        tvNotDataHint = view.findViewById(R.id.tv_not_data_hint);
        lvMatchList = view.findViewById(R.id.lv_match_list);
    }

    @Override
    protected void lazyLoad() {
    }

    public void updateView() {
        getActivity().runOnUiThread(() -> {
            if (matchVOList == null || matchVOList.isEmpty()) {
                lvMatchList.setVisibility(View.GONE);
                tvNotDataHint.setVisibility(View.VISIBLE);
            } else {
                lvMatchList.setVisibility(View.VISIBLE);
                tvNotDataHint.setVisibility(View.GONE);

                if (adapter == null) {
                    adapter = new MatchListAdapter(getContext(), matchVOList);
                    lvMatchList.setAdapter(adapter);
                    lvMatchList.setOnItemClickListener((parent, view, position, id) -> {
                        matchVo = matchVOList.get(position);
                        Log.d(TAG, "matchVo.id:" + matchVo.getId());
                        showDialog();
                    });
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void setMatchVOList(List<MatchVO> matchVOList) {
        this.matchVOList = matchVOList;
    }

    private void showDialog() {
        chooseClazz = null;
        if (dialog == null) {
            dialog = DialogPlus.newDialog(getContext())
                    .setContentHolder(new ViewHolder(R.layout.dialog_match))
                    .setGravity(Gravity.BOTTOM)
                    .setCancelable(true)
                    .setOnDismissListener(dialog -> {
                        if (chooseClazz != null) {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("MatchVo", matchVo);
                            bundle.putSerializable("isBasketball", isBasketball);
                            if (chooseClazz == GameVideoActivity.class) {
                                readyGo(GameVideoActivity.class, bundle);
                            } else {
                                queryMatchStatusDetailByIdAndJump(GameResultActivity.class);
                            }
                        }
                    })
                    .setOnClickListener((dialog, view) -> {
                        dialog.dismiss();
                        if (view instanceof Button) {
                            switch (view.getId()) {
                                case R.id.btn_live:
                                    chooseClazz = GameVideoActivity.class;
                                    isBasketball = false;
                                    break;
                                case R.id.btn_live_basketball:
                                    chooseClazz = GameVideoActivity.class;
                                    isBasketball = true;
                                    break;
                                case R.id.btn_count:
                                    chooseClazz = GameResultActivity.class;
                                    break;
                            }
                        }
                    })
                    .setExpanded(false)
                    .create();
            dialogTvName = (TextView) dialog.findViewById(R.id.tv_name);
            dialogTvTime = (TextView) dialog.findViewById(R.id.tv_time);
            dialogBtnLive = (Button) dialog.findViewById(R.id.btn_live);
            dialogBtnLiveBasketball = (Button) dialog.findViewById(R.id.btn_live_basketball);
        }
        if (!matchVo.isAllowStreaming()) {
            dialogBtnLive.setVisibility(View.INVISIBLE);
            dialogBtnLiveBasketball.setVisibility(View.INVISIBLE);
        } else {
            dialogBtnLive.setVisibility(View.VISIBLE);
            dialogBtnLiveBasketball.setVisibility(View.VISIBLE);
        }
        dialogTvName.setText(matchVo.getName());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        dialogTvTime.setText(simpleDateFormat.format(matchVo.getStartTime()));
        dialog.show();
    }

    private void showWaitingDialog() {
        if (waitingDialog == null) {
            waitingDialog = new WaitingDialog(getActivity(), "");
            waitingDialog.setCanceledOnTouchOutside(false);
        }
        getActivity().runOnUiThread(() -> waitingDialog.show());
    }

    private void dismissWaitingDialog() {
        getActivity().runOnUiThread(() -> {
            if (waitingDialog != null) {
                waitingDialog.dismiss();
            }
        });
    }

    private void queryMatchStatusDetailByIdAndJump(Class<?> clazz) {
        showWaitingDialog();
        FootballRequest request = RetrofitManager.getInstance().getRetrofit().create(FootballRequest.class);
        Call<ResponseEntity<MatchStatusDetail>> response = request.getMatchStatusDetailById(matchVo.getId());
        response.enqueue(new AutoRefreshTokenCallback<ResponseEntity<MatchStatusDetail>>() {
            @Override
            public void onRefreshTokenFail() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        dismissWaitingDialog();
                        showToast("授权失效，重新登录");
                        readyGo(LoginActivity.class);
                        FinishActivityManager.getManager().finishActivity(MainActivity.class);
                        getActivity().finish();
                    });
                }
            }

            @Override
            public void onSuccess(ResponseEntity<MatchStatusDetail> result) {
                dismissWaitingDialog();
                Bundle bundle = new Bundle();
                bundle.putSerializable("MatchVo", matchVo);
                bundle.putSerializable("MatchStatusDetailVo", result.getData());
                readyGo(clazz, bundle);
            }

            @Override
            public void onFail(@Nullable Response<ResponseEntity<MatchStatusDetail>> response, @Nullable Throwable t) {
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

}
