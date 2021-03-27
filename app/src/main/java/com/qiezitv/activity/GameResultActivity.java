package com.qiezitv.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.qiezitv.R;
import com.qiezitv.fragment.ResultFragment;
import com.qiezitv.model.MatchStatusDetail;
import com.qiezitv.model.MatchVO;

public class GameResultActivity extends BaseActivity {

    private ResultFragment resultFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_result);
        init();
    }

    private void init() {
        MatchVO matchVo = (MatchVO) getIntent().getSerializableExtra("MatchVo");
        MatchStatusDetail matchStatusDetail = (MatchStatusDetail) getIntent().getSerializableExtra("MatchStatusDetailVo");
        resultFragment = new ResultFragment();
        resultFragment.setMatchVo(matchVo);
        resultFragment.setMatchStatusDetail(matchStatusDetail);
        resultFragment.setBtnBackOnClick(v -> GameResultActivity.this.finish());
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, resultFragment);
        fragmentTransaction.commit();
    }

}
