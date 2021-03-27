package com.qiezitv.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.qiezitv.R;
import com.qiezitv.adapter.ScoreBoardAdapter;
import com.qiezitv.common.Constants;
import com.qiezitv.common.FinishActivityManager;
import com.qiezitv.common.http.AutoRefreshTokenCallback;
import com.qiezitv.common.http.RetrofitManager;
import com.qiezitv.common.http.entity.ResponseEntity;
import com.qiezitv.fragment.ResultFragment;
import com.qiezitv.http.request.FootballRequest;
import com.qiezitv.http.request.LiveRequest;
import com.qiezitv.http.request.ScoreBoardRequest;
import com.qiezitv.model.ActivityVO;
import com.qiezitv.model.FootballEvent;
import com.qiezitv.model.MatchStatusDetail;
import com.qiezitv.model.MatchVO;
import com.qiezitv.model.ScoreBoard;
import com.qiezitv.view.BaseScoreBoardView;
import com.qiezitv.view.ColorPickerDialog;
import com.qiezitv.view.HorizontalListView;
import com.qiezitv.view.ScoreBoardBasketball;
import com.qiezitv.view.ScoreBoardView;
import com.qiezitv.view.WaterMarkView;
import com.laifeng.sopcastsdk.camera.CameraListener;
import com.laifeng.sopcastsdk.configuration.AudioConfiguration;
import com.laifeng.sopcastsdk.configuration.CameraConfiguration;
import com.laifeng.sopcastsdk.configuration.VideoConfiguration;
import com.laifeng.sopcastsdk.entity.Watermark;
import com.laifeng.sopcastsdk.entity.WatermarkPosition;
import com.laifeng.sopcastsdk.stream.packer.rtmp.RtmpPacker;
import com.laifeng.sopcastsdk.stream.sender.DebugInfo;
import com.laifeng.sopcastsdk.stream.sender.rtmp.RtmpSender;
import com.laifeng.sopcastsdk.ui.CameraLivingView;
import com.laifeng.sopcastsdk.utils.SopCastLog;
import com.laifeng.sopcastsdk.video.effect.Effect;
import com.laifeng.sopcastsdk.video.effect.HSLEffect;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Response;

import static com.qiezitv.model.FootballEvent.FINISH;
import static com.qiezitv.model.FootballEvent.HALF_TIME;
import static com.qiezitv.model.FootballEvent.SECOND_HALF;
import static com.qiezitv.model.FootballEvent.START;
import static com.qiezitv.model.FootballEvent.UNOPEN;

@SuppressLint("HandlerLeak")
public class GameVideoActivity extends BaseActivity {
    private static final String TAG = GameVideoActivity.class.getSimpleName();

    private static final String HINT_PUSH = "是否开始直播？";
    private static final String HINT_LEAVE = "是否离开？";
    private static final String HINT_STOP = "是否停止直播？";

    private CameraLivingView mLFLiveView;
    private RtmpSender mRtmpSender;
    private GestureDetector mGestureDetector;
    private VideoConfiguration mVideoConfiguration;
    private int mCurrentBps;
    private int mVideoQuality = Constants.VideoQuality.MID;

    private ImageView ivStart, ivFinish, ivVideoSetting, ivResult, ivAdd, ivReduce, ivStatus,
            ivMute, ivNetStatus;
    private TextView tvHint, tvHintBandwidth;
    private SeekBar seekbarZoom;
    private LinearLayout llBottom, llRight, llHintNetwork, llHintPhone;
    private FrameLayout flResultContainer;
    private WaterMarkView waterMarkContainer;

    private ResultFragment resultFragment;

    // 确认对话框
    private DialogPlus confirmDialog;
    private TextView confirmDialogTvHint;

    // 设置分辨率对话框
    private DialogPlus videoSettingDialog;

    private boolean isMute = false;
    private boolean isPublish = false;
    private boolean isScoreBoardShow = true;
    private boolean isLogoShow = true;
    private float brightness = 1.0f;
    private boolean isAutoFocus = false;

    private Integer timeSecond = 0;
    private boolean isTimeCountRun = false;

    // 设置比分牌对话框
    private DialogPlus scoreSettingDialog;
    private ImageView ivHostColor, ivGuestColor;
    private EditText etTime;
    private int hostColor = Color.rgb(255, 0, 0);
    private int guestColor = Color.rgb(0, 0, 255);
    private String gameTitle, hostTeamName, guestTeamName;

    private MatchVO matchVo;
    private MatchStatusDetail matchStatusDetail;
    private ActivityVO activityVo;

    private List<ScoreBoard> scoreBoardList;
    private ScoreBoard currentScoreBoard;
    private Integer liveQuality;
    private int pushRetryTimes = 0;
    private static final int MAX_RETRY_TIMES = 3;
    private long firstRetryTime = -1L;

    //定时刷新
    private Timer mTimer = new Timer();
    private TimerTask mTimerTask;
    private int SECOND = 30;
    //定时获取推流波动
    private Timer mTimerLiveQuality = new Timer();
    private TimerTask mTimerTaskLiveQuality;
    private int SECOND_LIVE_QUALITY = 70;

    //篮球
    private boolean isBasketball = false;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (isTimeCountRun && waterMarkContainer.getScoreBoard() != null) {
                timeSecond++;
                int min = timeSecond / 60;
                int second = timeSecond % 60;
                if(!isBasketball){
                    waterMarkContainer.getScoreBoard().setTime(String.format(Locale.getDefault(), "%02d:%02d", min, second));
                }
                reloadWatermark();
            }
            sendEmptyMessageDelayed(1, 1000);
        }
    };

    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = () -> {
        if (mLFLiveView != null && waterMarkContainer.getScoreBoard() != null) {
            reloadWatermark();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_game_video);

        matchVo = (MatchVO) getIntent().getSerializableExtra("MatchVo");

        isBasketball = (boolean) getIntent().getSerializableExtra("isBasketball");
        if (matchVo.getLeague().getShortName() != null) {
            gameTitle = matchVo.getLeague().getShortName();
        } else {
            gameTitle = matchVo.getLeague().getName();
        }
        if (matchVo.getHostTeam() != null) {
            hostTeamName = matchVo.getHostTeam().getName();
        } else {
            hostTeamName = "无";
        }
        if (matchVo.getGuestTeam() != null) {
            guestTeamName = matchVo.getGuestTeam().getName();
        } else {
            guestTeamName = "无";
        }

        init();

        queryActivityVo();

        queryScoreBoard();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLFLiveView.stop();
        setPublish(false);
        tvHint.setText("未直播");
        tvHintBandwidth.setText("未直播");
        ivStart.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLFLiveView.stop();
        mLFLiveView.release();
        mTimer.cancel();
        mTimerLiveQuality.cancel();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG, "onConfigurationChanged");
    }

    @Override
    public void setRequestedOrientation(int requestedOrientation) {
        // 注释后防止屏幕翻转
//        super.setRequestedOrientation(requestedOrientation);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                mLFLiveView.zoom(false, 1);
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                mLFLiveView.zoom(true, 1);
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if (resultFragment != null && resultFragment.isVisible()) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(resultFragment);
            fragmentTransaction.commit();
            llBottom.setVisibility(View.VISIBLE);
            llRight.setVisibility(View.VISIBLE);
            tvHint.setVisibility(View.VISIBLE);
            tvHintBandwidth.setVisibility(View.VISIBLE);
            flResultContainer.setVisibility(View.GONE);
        } else if (isPublish()) {
            showConfirmDialog(HINT_LEAVE);
        } else {
            finish();
        }
    }

    //init
    private void init() {
        hideBottomUIMenu();
        //初始化view
        initView();
        //初始化直播
        initLiveView();
        // 比分牌
        initWaterMark();
        //初始化计时器
        initTimer();
    }

    /**
     * 隐藏虚拟按键，并且全屏
     */
    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        if (Build.VERSION.SDK_INT >= 28) {
            WindowManager.LayoutParams lp = this.getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(lp);
        }
        decorView.setOnSystemUiVisibilityChangeListener(i -> {
            int uiOptions1 = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions1);
        });
    }

    private void initView() {
        mLFLiveView = findViewById(R.id.gl_surface_view_camera);
        ivStart = findViewById(R.id.iv_start);
        ivFinish = findViewById(R.id.iv_finish);
        ivVideoSetting = findViewById(R.id.iv_video_setting);
        ivMute = findViewById(R.id.iv_mute);
        ivResult = findViewById(R.id.iv_result);
        ivStatus = findViewById(R.id.iv_status);
        llBottom = findViewById(R.id.ll_bottom);
        flResultContainer = findViewById(R.id.fl_result_container);
        tvHint = findViewById(R.id.tv_hint);
        tvHintBandwidth = findViewById(R.id.tv_hint_bandwidth);
        llRight = findViewById(R.id.ll_right);
        ivAdd = findViewById(R.id.iv_add);
        ivReduce = findViewById(R.id.iv_reduce);
        seekbarZoom = findViewById(R.id.seekbar_zoom);
        ivNetStatus = findViewById(R.id.iv_net_status);
        llHintNetwork = findViewById(R.id.ll_hint_network);
        llHintPhone = findViewById(R.id.ll_hint_phone);
        seekbarZoom.setClickable(false);
        llHintPhone.setOnClickListener(v->{
            callPhone();
        });
        if (isBasketball) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.BOTTOM | Gravity.END;
            params.setMargins(0, 0, 30, 30);
            llBottom.setLayoutParams(params);
        }
        ivStart.setOnClickListener(v -> {
            if (!isPublish()) {
                if (activityVo != null) {
                    showConfirmDialog(HINT_PUSH);
                } else {
                    showToast("获取直播地址失败,请尝试重新进入该页面");
                }
            } else {
                showConfirmDialog(HINT_STOP);
            }
        });
        ivFinish.setOnClickListener(v -> {
            if (isPublish()) {
                showConfirmDialog(HINT_LEAVE);
            } else {
                finish();
            }
        });
        ivMute.setOnClickListener(v -> {
            if (isMute()) {
                setMute(false);
            } else {
                setMute(true);
            }
        });
        ivVideoSetting.setOnClickListener(v -> showSettingMenu(ivVideoSetting));
        if (isBasketball) {
            ivStatus.setOnClickListener(v -> {
                showChangeScoreDialog();
            });
        } else {
            ivStatus.setVisibility(View.GONE);
        }
        ivResult.setOnClickListener(v -> {
            if (isBasketball) {
                showBasketballEventDialog();
                return;
            }
            if (resultFragment == null) {
                resultFragment = new ResultFragment();
                resultFragment.setMatchVo(matchVo);
                resultFragment.setMatchStatusDetail(matchStatusDetail);
                resultFragment.setBtnBackOnClick(v1 -> {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.remove(resultFragment);
                    fragmentTransaction.commit();
                    llBottom.setVisibility(View.VISIBLE);
                    llRight.setVisibility(View.VISIBLE);
                    tvHint.setVisibility(View.VISIBLE);
                    tvHintBandwidth.setVisibility(View.VISIBLE);
                    flResultContainer.setVisibility(View.GONE);
                });
                resultFragment.setOnAddEventListener(event -> {
                    //点击开始比赛，开始计时，从0开始
                    if (event.getEventType() == FootballEvent.START) {
                        startTime(0);
                    }
                    //点击下半场开始，开始计时，从比赛的一半时间开始
                    if (event.getEventType() == FootballEvent.SECOND_HALF) {
                        startTime(matchVo.getDuration() / 2);
                    }
                    //点击中场事件，停止计时，事件为比赛的一半
                    if (event.getEventType() == FootballEvent.HALF_TIME) {
                        stopTime(matchVo.getDuration() / 2);
                    }
                    //点击结束，停止计时
                    if (event.getEventType() == FootballEvent.FINISH) {
                        stopTime();
                    }
                });
                resultFragment.setOnScoreAndStatusChangeListener((score, eventType) -> {
                    if (waterMarkContainer.getScoreBoard() != null) {
                        String[] temp = score.split("-");
                        waterMarkContainer.getScoreBoard().setScoreLeft(temp[0]);
                        waterMarkContainer.getScoreBoard().setScoreRight(temp[1]);
                        if (isBasketball) {
                            return;
                        }
                        // 未开始比赛
                        if (eventType == FootballEvent.UNOPEN) {
                            stopTime(0);
                        }
                        // 点击开始比赛，开始计时，从0开始
                        if (eventType == FootballEvent.START) {
                            startTime(0);
                        }
                        // 点击下半场开始，开始计时，从比赛的一半时间开始
                        if (eventType == FootballEvent.SECOND_HALF) {
                            startTime(matchVo.getDuration() / 2);
                        }
                        // 点击中场事件，停止计时，事件为比赛的一半
                        if (eventType == FootballEvent.HALF_TIME) {
                            stopTime(matchVo.getDuration() / 2);
                        }
                        // 点击结束，停止计时
                        if (eventType == FootballEvent.FINISH) {
                            stopTime();
                        }
                    }
                });
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.fl_result_container, resultFragment);
            fragmentTransaction.commit();
            llBottom.setVisibility(View.GONE);
            llRight.setVisibility(View.GONE);
            tvHint.setVisibility(View.GONE);
            tvHintBandwidth.setVisibility(View.GONE);
            flResultContainer.setVisibility(View.VISIBLE);
        });
        ivAdd.setOnClickListener(v -> {
            mLFLiveView.zoom(true, 1);
        });
        ivReduce.setOnClickListener(v -> {
            mLFLiveView.zoom(false, 1);
        });
    }

    private void initWaterMark() {
        waterMarkContainer = findViewById(R.id.top_container);
        waterMarkContainer.setPadding(20, 20, 20, 20);

        //init logo
        ImageView logoWaterMarkView = new ImageView(this);
        logoWaterMarkView.setImageResource(R.drawable.ic_logo_horizontal);
        FrameLayout.LayoutParams logoLp = new FrameLayout.LayoutParams(mVideoConfiguration.width / 6, (int) (mVideoConfiguration.height / 6 / 2.818));
        logoLp.leftMargin = (int) (mVideoConfiguration.width * 0.0214);
        logoLp.topMargin = (int) (mVideoConfiguration.height * 0.023);
        logoWaterMarkView.setLayoutParams(logoLp);

        //水印设置成跟视频大小一样
        FrameLayout.LayoutParams waterMarkContainerLp = new FrameLayout.LayoutParams(mVideoConfiguration.width, mVideoConfiguration.height);
        waterMarkContainer.setLayoutParams(waterMarkContainerLp);

        waterMarkContainer.setLogo(logoWaterMarkView);
    }

    private void initTimer() {
        handler.sendEmptyMessage(0);
        //定时刷新
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                queryMatchStatusDetail();
            }
        };
        mTimer.schedule(mTimerTask, 0, SECOND * 1000);
    }

    private void initLiveView() {
        SopCastLog.isOpen(true);
        mLFLiveView.init();
        CameraConfiguration.Builder cameraBuilder = new CameraConfiguration.Builder();
        cameraBuilder.setOrientation(CameraConfiguration.Orientation.LANDSCAPE)
                .setFacing(CameraConfiguration.Facing.BACK)
                .setFocusMode(CameraConfiguration.FocusMode.TOUCH);
        CameraConfiguration cameraConfiguration = cameraBuilder.build();
        mLFLiveView.setCameraConfiguration(cameraConfiguration);

        mVideoConfiguration = new VideoConfiguration.Builder().build(VideoConfiguration.VideoQuality.VideoQuality_Mid);
        mVideoQuality = Constants.VideoQuality.MID;
        mLFLiveView.setVideoConfiguration(mVideoConfiguration);

        //设置预览监听
        mLFLiveView.setCameraOpenListener(new CameraListener() {
            @Override
            public void onOpenSuccess() {
                seekbarZoom.setMax(mLFLiveView.getMaxZoom());
            }

            @Override
            public void onOpenFail(int error) {
                showToast("相机开启失败");
            }

            @Override
            public void onCameraChange() {
                showToast("相机切换");
            }
        });

        //设置手势识别
        mGestureDetector = new GestureDetector(this, new GestureListener());
        mLFLiveView.setOnTouchListener((v, event) -> {
            mGestureDetector.onTouchEvent(event);
            return false;
        });

        //初始化flv打包器
        RtmpPacker packer = new RtmpPacker();
        packer.initAudioParams(AudioConfiguration.DEFAULT_FREQUENCY, 16, false);
        mLFLiveView.setPacker(packer);
        //设置发送器
        mRtmpSender = new RtmpSender();
        mRtmpSender.setVideoParams(mVideoConfiguration.width, mVideoConfiguration.height, mVideoConfiguration.maxBps, mVideoConfiguration.fps);
        mRtmpSender.setAudioParams(AudioConfiguration.DEFAULT_FREQUENCY, 16, false);
        mRtmpSender.setSenderListener(mSenderListener);
        mLFLiveView.setSender(mRtmpSender);
        mLFLiveView.setLivingStartListener(new CameraLivingView.LivingStartListener() {
            @Override
            public void startError(int error) {
                //直播失败
                showToast("开始直播失败");
                mLFLiveView.stop();
            }

            @Override
            public void startSuccess() {
                //直播成功
                setVideoBitRate(mVideoConfiguration.maxBps);
            }
        });
        mLFLiveView.setOnZoomProgressListener(progress -> seekbarZoom.setProgress((int) Math.floor(progress * 100)));
    }

    private RtmpSender.OnSenderListener mSenderListener = new RtmpSender.OnSenderListener() {
        @Override
        public void onConnecting() {

        }

        @Override
        public void onConnected() {
            tvHint.setText("已连接");
            tvHintBandwidth.setText("已连接");
            mLFLiveView.start();
            llHintPhone.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onDisConnected() {
            showToast("直播断开");
            mLFLiveView.stop();
            setPublish(false);
            tvHint.setText("已断开");
            tvHintBandwidth.setText("已断开");
        }

        @Override
        public void onPublishFail() {
            showToast("推流失败");
            setPublish(false);
            llHintPhone.setVisibility(View.VISIBLE);
        }

        @Override
        public void onNetGood() {
            if (mCurrentBps + 50 <= mVideoConfiguration.maxBps) {
                SopCastLog.d(TAG, "BPS_CHANGE good up 50");
                int bps = mCurrentBps + 50;
                setVideoBitRate(bps);
            } else {
                SopCastLog.d(TAG, "BPS_CHANGE good good good");
            }
            SopCastLog.d(TAG, "Current Bps: " + mCurrentBps);
            tvHint.setText("网络正常，码率：" + mCurrentBps);
            ivNetStatus.setImageResource(R.drawable.shape_network_status_good);
        }

        @Override
        public void onNetBad() {
            if (mCurrentBps - 100 >= mVideoConfiguration.minBps) {
                SopCastLog.d(TAG, "BPS_CHANGE bad down 100");
                int bps = mCurrentBps - 100;
                setVideoBitRate(bps);
            } else {
                SopCastLog.d(TAG, "BPS_CHANGE bad down 100");
            }
            SopCastLog.d(TAG, "Current Bps: " + mCurrentBps);
            tvHint.setText("网络差，码率：" + mCurrentBps);
            ivNetStatus.setImageResource(R.drawable.shape_network_status_bad);
        }

        @Override
        public void onDebug(DebugInfo debugInfo) {
            tvHintBandwidth.setText(debugInfo.getCurrentBandwidth() / 1000 + "kb/s");
        }
    };

    public class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e1.getX() - e2.getX() > 100
                    && Math.abs(velocityX) > 200) {
                // Fling left
            } else if (e2.getX() - e1.getX() > 100
                    && Math.abs(velocityX) > 200) {
                // Fling right
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }

    private void queryActivityVo() {
        LiveRequest request = RetrofitManager.getInstance().getRetrofit().create(LiveRequest.class);
        Call<ResponseEntity<ActivityVO>> response = request.getActivityVo(matchVo.getActivityId());
        response.enqueue(new AutoRefreshTokenCallback<ResponseEntity<ActivityVO>>() {
            @Override
            public void onRefreshTokenFail() {
                gotoLoginActivity();
            }

            @Override
            public void onSuccess(ResponseEntity<ActivityVO> result) {
                activityVo = result.getData();
            }

            @Override
            public void onFail(@Nullable Response<ResponseEntity<ActivityVO>> response, @Nullable Throwable t) {
                if (response != null) {
                    showToast("获取直播地址失败:" + (response.body() != null ? response.body().getMessage() : ""));
                }
                if (t != null) {
                    showToast("获取直播地址失败:" + t.getMessage());
                }
            }
        });
    }

    private void queryScoreBoard() {
        if (isBasketball) {
            changeScoreBoard();
            return;
        }
        ScoreBoardRequest request = RetrofitManager.getInstance().getRetrofit().create(ScoreBoardRequest.class);
        Call<ResponseEntity<List<ScoreBoard>>> response = request.getScoreboard();
        response.enqueue(new AutoRefreshTokenCallback<ResponseEntity<List<ScoreBoard>>>() {
            @Override
            public void onRefreshTokenFail() {
                gotoLoginActivity();
            }

            @Override
            public void onSuccess(ResponseEntity<List<ScoreBoard>> result) {
                scoreBoardList = result.getData();
                currentScoreBoard = scoreBoardList.get(0);
                changeScoreBoard(currentScoreBoard);
            }

            @Override
            public void onFail(@Nullable Response<ResponseEntity<List<ScoreBoard>>> response, @Nullable Throwable t) {
                if (response != null) {
                    showToast("请求比分牌失败:" + (response.body() != null ? response.body().getMessage() : ""));
                }
                if (t != null) {
                    showToast("请求比分牌失败:" + t.getMessage());
                }
            }
        });
    }

    private void queryLiveQuality() {
        LiveRequest request = RetrofitManager.getInstance().getRetrofit().create(LiveRequest.class);
        Call<ResponseEntity<Integer>> response = request.quality(matchVo.getActivityId());
        response.enqueue(new AutoRefreshTokenCallback<ResponseEntity<Integer>>() {
            @Override
            public void onRefreshTokenFail() {
                gotoLoginActivity();
            }

            @Override
            public void onSuccess(ResponseEntity<Integer> result) {
                liveQuality = result.getData();
                switch (liveQuality) {
                    case Constants.ActivityQuality.BAD:
                        long now = System.currentTimeMillis();
                        if (pushRetryTimes < MAX_RETRY_TIMES) {
                            mLFLiveView.restart();
                            if (now - firstRetryTime <= 5 * 60 * 1000) {
                                pushRetryTimes = pushRetryTimes + 1;
                            } else {
                                firstRetryTime = now;
                                pushRetryTimes = 1;
                            }
                            showToast("网络不佳，推流重试:" + pushRetryTimes + "次");
                        } else {
                            llHintNetwork.setVisibility(View.VISIBLE);
                        }
                        break;
                    case Constants.ActivityQuality.UNKNOW:
                        break;
                    case Constants.ActivityQuality.NORMAL:
                    case Constants.ActivityQuality.NOTBAD:
                        if (pushRetryTimes >= MAX_RETRY_TIMES) {
                            llHintNetwork.setVisibility(View.INVISIBLE);
                            pushRetryTimes = 0;
                        }
                        break;
                }
            }

            @Override
            public void onFail(@Nullable Response<ResponseEntity<Integer>> response, @Nullable Throwable t) {
                if (response != null) {
                    showToast("请求直播质量失败:" + (response.body() != null ? response.body().getMessage() : ""));
                }
                if (t != null) {
                    showToast("请求直播质量失败:" + t.getMessage());
                }
            }
        });
    }

    private void startQueryLiveQuality() {
        mTimerLiveQuality.cancel();
        mTimerLiveQuality = new Timer();
        pushRetryTimes = 0;
        mTimerTaskLiveQuality = new TimerTask() {
            @Override
            public void run() {
                queryLiveQuality();
            }
        };
        mTimerLiveQuality.schedule(mTimerTaskLiveQuality, SECOND_LIVE_QUALITY * 1000, SECOND_LIVE_QUALITY * 1000);
    }

    private void showConfirmDialog(String hint) {
        if (confirmDialog == null) {
            confirmDialog = DialogPlus.newDialog(this)
                    .setContentHolder(new ViewHolder(R.layout.dialog_confirm))
                    .setGravity(Gravity.BOTTOM)
                    .setCancelable(true)
                    .setOnClickListener((dialog, view) -> {
                        switch (view.getId()) {
                            case R.id.btn_confirm:
                                String btnText = confirmDialogTvHint.getText().toString();
                                if (btnText.equals(HINT_PUSH)) {
                                    Log.d(TAG, "PushStreamUrl->" + activityVo.getPushStreamUrl());
                                    mRtmpSender.setAddress(activityVo.getPushStreamUrl());
                                    mRtmpSender.connect();
                                    setPublish(true);
                                    startTime();
                                    tvHint.setText("直播中");
                                    tvHintBandwidth.setText("直播中");
                                    llHintNetwork.setVisibility(View.INVISIBLE);
                                    switch (matchStatusDetail.getStatus()) {
                                        case FootballEvent.UNOPEN:
                                            stopTime(0);
                                            break;
                                        case FootballEvent.HALF_TIME:
                                            stopTime(matchVo.getDuration() / 2);
                                            break;
                                        case FootballEvent.FINISH:
                                            stopTime(matchVo.getDuration());
                                            break;
                                        default:
                                            startTime(matchStatusDetail.getMinute());
                                            break;
                                    }
                                    startQueryLiveQuality();
                                } else if (btnText.equals(HINT_LEAVE)) {
                                    finish();
                                } else if (btnText.equals(HINT_STOP)) {
                                    setPublish(false);
                                    mLFLiveView.stop();
                                }
                                dialog.dismiss();
                                break;
                            case R.id.btn_cancel:
                                dialog.dismiss();
                                break;
                        }
                    })
                    .setExpanded(false)
                    .create();
            confirmDialogTvHint = (TextView) confirmDialog.findViewById(R.id.tv_hint);
        }
        confirmDialogTvHint.setText(hint);
        confirmDialog.show();
    }

    private void showVideoResolutionSettingDialog() {
        if (videoSettingDialog == null) {
            videoSettingDialog = DialogPlus.newDialog(this)
                    .setContentHolder(new ViewHolder(R.layout.dialog_video_setting))
                    .setContentBackgroundResource(R.drawable.shape_circle_popup)
                    .setGravity(Gravity.CENTER)
                    .setCancelable(true)
                    .setExpanded(false)
                    .create();
            SeekBar seekbarQuality = (SeekBar) videoSettingDialog.findViewById(R.id.seekbar_quality);
            seekbarQuality.setProgress(mVideoQuality);
            seekbarQuality.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    switch (i) {
                        case Constants.VideoQuality.LOW:
                            changeVideoQuality(Constants.VideoQuality.LOW);
                            break;
                        case Constants.VideoQuality.MID:
                            changeVideoQuality(Constants.VideoQuality.MID);
                            break;
                        case Constants.VideoQuality.HIGH:
                            changeVideoQuality(Constants.VideoQuality.HIGH);
                            break;
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            SeekBar seekbarBrightness = (SeekBar) videoSettingDialog.findViewById(R.id.seekbar_brightness);
            seekbarBrightness.setProgress((int) (brightness - 1.0f));
            seekbarBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    brightness = 1.0f + i / 100f;
                    setBrightness(brightness);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            Switch switchAutoFocus = (Switch) videoSettingDialog.findViewById(R.id.switch_autoFocus);
            switchAutoFocus.setChecked(isAutoFocus);
            switchAutoFocus.setOnCheckedChangeListener((compoundButton, b) -> setAutoFocus(b));
        }

        videoSettingDialog.show();
    }

    private void showSettingMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_setting, popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.scoreboard_setting:
                    if (isBasketball) {
                        showToast("暂不支持更换比分牌");
                    } else {
                        showChooseScoreBoardDialog();
                    }
                    break;
                case R.id.live_setting:
                    showVideoResolutionSettingDialog();
                    break;
            }
            return true;
        });

    }

    private void queryMatchStatusDetail() {
        FootballRequest request = RetrofitManager.getInstance().getRetrofit().create(FootballRequest.class);
        Call<ResponseEntity<MatchStatusDetail>> response = request.getMatchStatusDetailById(matchVo.getId());
        response.enqueue(new AutoRefreshTokenCallback<ResponseEntity<MatchStatusDetail>>() {
            @Override
            public void onRefreshTokenFail() {
                gotoLoginActivity();
            }

            @Override
            public void onSuccess(ResponseEntity<MatchStatusDetail> result) {
                matchStatusDetail = result.getData();
                if (waterMarkContainer.getScoreBoard() != null) {
                    String[] scores = matchStatusDetail.getScore().split("-");
                    waterMarkContainer.getScoreBoard().setScoreLeft(scores[0]);
                    waterMarkContainer.getScoreBoard().setScoreRight(scores[1]);
                }
            }

            @Override
            public void onFail(@Nullable Response<ResponseEntity<MatchStatusDetail>> response, @Nullable Throwable t) {
                if (response != null) {
                    showToast("请求失败:" + (response.body() != null ? response.body().getMessage() : ""));
                }
                if (t != null) {
                    showToast("网络请求失败:" + t.getMessage());
                }
            }
        });
    }

    private void showChooseScoreBoardDialog() {
        if (scoreSettingDialog == null) {
            scoreSettingDialog = DialogPlus.newDialog(this)
                    .setContentHolder(new ViewHolder(R.layout.dialog_choose_score_board))
                    .setGravity(Gravity.BOTTOM)
                    .setCancelable(true)
                    .setContentBackgroundResource(R.drawable.shape_circle_bottom_popup)
                    .setOnClickListener((dialog, view) -> {
                        ColorPickerDialog colorPickerDialog;
                        switch (view.getId()) {
                            case R.id.iv_host_color:
                                colorPickerDialog = new ColorPickerDialog(GameVideoActivity.this, hostColor, "主队颜色", color -> {
                                    hostColor = color;
                                    ivHostColor.setBackgroundColor(hostColor);
                                    if (waterMarkContainer.getScoreBoard() != null) {
                                        waterMarkContainer.getScoreBoard().setHostColor(hostColor);
                                        reloadWatermark();
                                    }
                                });
                                colorPickerDialog.show();
                                break;
                            case R.id.iv_guest_color:
                                colorPickerDialog = new ColorPickerDialog(GameVideoActivity.this, guestColor, "客队颜色", color -> {
                                    guestColor = color;
                                    ivGuestColor.setBackgroundColor(guestColor);
                                    if (waterMarkContainer.getScoreBoard() != null) {
                                        waterMarkContainer.getScoreBoard().setGuestColor(guestColor);
                                        reloadWatermark();
                                    }
                                });
                                colorPickerDialog.show();
                                break;
                        }
                    })
                    .setExpanded(false)
                    .create();
            HorizontalListView listViewScoreBoard = (HorizontalListView) scoreSettingDialog.findViewById(R.id.lv_score_list);
            ScoreBoardAdapter adapter = new ScoreBoardAdapter(this, scoreBoardList);
            listViewScoreBoard.setAdapter(adapter);
            listViewScoreBoard.setOnItemClickListener((parent, view, position, id) -> {
                currentScoreBoard = scoreBoardList.get(position);
                changeScoreBoard(currentScoreBoard);
            });
            adapter.notifyDataSetChanged();

            Switch switchLogo = (Switch) scoreSettingDialog.findViewById(R.id.switch_logo);
            Switch switchScoreboard = (Switch) scoreSettingDialog.findViewById(R.id.switch_scoreboard);
            switchLogo.setChecked(isLogoShow);
            switchScoreboard.setChecked(isScoreBoardShow);
            switchLogo.setOnCheckedChangeListener((compoundButton, b) -> {
                if (b) {
                    showLogo();
                } else {
                    hideLogo();
                }
            });
            switchScoreboard.setOnCheckedChangeListener((compoundButton, b) -> {
                if (b) {
                    showScoreboard();
                } else {
                    hideScoreboard();
                }
            });
            ivHostColor = (ImageView) scoreSettingDialog.findViewById(R.id.iv_host_color);
            ivGuestColor = (ImageView) scoreSettingDialog.findViewById(R.id.iv_guest_color);
            EditText etGameTitle = (EditText) scoreSettingDialog.findViewById(R.id.et_title);
            etGameTitle.setText(gameTitle);
            etGameTitle.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    gameTitle = s.toString();
                    if (waterMarkContainer.getScoreBoard() != null) {
                        waterMarkContainer.getScoreBoard().setTitle(gameTitle);
                        reloadWatermark();
                    }
                }
            });
            EditText etHostTeamName = (EditText) scoreSettingDialog.findViewById(R.id.et_host_name);
            etHostTeamName.setText(hostTeamName);
            etHostTeamName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    hostTeamName = s.toString();
                    if (waterMarkContainer.getScoreBoard() != null) {
                        waterMarkContainer.getScoreBoard().setTeamNameHost(hostTeamName);
                        reloadWatermark();
                    }
                }
            });
            EditText etGuestTeamName = (EditText) scoreSettingDialog.findViewById(R.id.et_guest_name);
            etGuestTeamName.setText(guestTeamName);
            etGuestTeamName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    guestTeamName = s.toString();
                    if (waterMarkContainer.getScoreBoard() != null) {
                        waterMarkContainer.getScoreBoard().setTeamNameGuest(guestTeamName);
                        reloadWatermark();
                    }
                }
            });
            etTime = (EditText) scoreSettingDialog.findViewById(R.id.et_time);
            etTime.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    int time;
                    try {
                        if (TextUtils.isEmpty(s)) {
                            time = 0;
                        } else {
                            time = Integer.parseInt(s.toString().trim());
                        }
                        Integer status = matchStatusDetail.getStatus();
                        if (isPublish() && status != FootballEvent.FINISH
                                && status != FootballEvent.HALF_TIME
                                && status != FootballEvent.UNOPEN
                                && status != FootballEvent.PAUSE) {
                            startTime(time);
                        } else {
                            stopTime(time);
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        if (timeSecond == 0) {
            etTime.setText("0");
        } else {
            etTime.setText(String.valueOf(timeSecond / 60));
        }
        scoreSettingDialog.show();
    }

    //换比分牌
    private <T extends BaseScoreBoardView> void changeScoreBoard(ScoreBoard scoreBoard) {
        //隐藏比分牌
        if (scoreBoard == null) {
            waterMarkContainer.hideScoreBoard();
            return;
        }
        BaseScoreBoardView scoreBoardWaterMarkView = new ScoreBoardView(this).initPositionWithScoreBoard(scoreBoard);
        //删除比分牌
        if (waterMarkContainer.getScoreBoard() != null) {
            waterMarkContainer.removeView(waterMarkContainer.getScoreBoard());
        }
        //添加比分牌
        scoreBoardWaterMarkView.setTeamNameHost(hostTeamName);
        scoreBoardWaterMarkView.setTeamNameGuest(guestTeamName);
        scoreBoardWaterMarkView.setTitle(gameTitle);
        // 设置队服颜色
        scoreBoardWaterMarkView.setHostColor(hostColor);
        scoreBoardWaterMarkView.setGuestColor(guestColor);
        scoreBoardWaterMarkView.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
        waterMarkContainer.setScoreBoard(scoreBoardWaterMarkView);
    }

    private <T extends BaseScoreBoardView> void changeScoreBoard() {
        BaseScoreBoardView scoreBoardWaterMarkView = new ScoreBoardBasketball(this);
        //删除比分牌
        if (waterMarkContainer.getScoreBoard() != null) {
            waterMarkContainer.removeView(waterMarkContainer.getScoreBoard());
        }
        //添加比分牌
        scoreBoardWaterMarkView.setTeamNameHost(hostTeamName);
        scoreBoardWaterMarkView.setTeamNameGuest(guestTeamName);
        scoreBoardWaterMarkView.setScoreLeft("0");
        scoreBoardWaterMarkView.setScoreRight("0");
        scoreBoardWaterMarkView.setTime("1");
        scoreBoardWaterMarkView.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
        waterMarkContainer.setScoreBoard(scoreBoardWaterMarkView);
    }

    //隐藏比分牌
    private void hideScoreboard() {
        isScoreBoardShow = false;
        waterMarkContainer.hideScoreBoard();
        reloadWatermark();
    }

    //显示比分牌
    private void showScoreboard() {
        isScoreBoardShow = true;
        waterMarkContainer.showScoreBoard();
        reloadWatermark();
    }

    //隐藏logo
    private void hideLogo() {
        isLogoShow = false;
        waterMarkContainer.hideLogo();
        reloadWatermark();
    }

    //显示logo
    private void showLogo() {
        isLogoShow = true;
        waterMarkContainer.showLogo();
        reloadWatermark();
    }

    private void reloadWatermark() {
        setWatermark(waterMarkContainer.getBitmap());
    }

    private void setWatermark(Bitmap watermarkImg) {
        if (watermarkImg == null) {
            mLFLiveView.setWatermark(null);
            return;
        }
        //全屏水印
        Watermark watermark = new Watermark(watermarkImg, WatermarkPosition.WATERMARK_ORIENTATION_TOP_RIGHT, true);
        mLFLiveView.setWatermark(watermark);
    }

    private boolean isPublish() {
        return isPublish;
    }

    private void setPublish(boolean publish) {
        isPublish = publish;
        if (isPublish) {
            ivStart.setImageResource(R.drawable.ic_stop);
            tvHint.setText("直播中");
            tvHintBandwidth.setText("直播中");
        } else {
            ivStart.setImageResource(R.drawable.ic_play);
            tvHint.setText("未直播");
            tvHintBandwidth.setText("未直播");
        }
    }

    private boolean isMute() {
        return isMute;
    }

    private void setMute(boolean mute) {
        isMute = mute;
        mLFLiveView.mute(isMute);
        if (isMute) {
            ivMute.setImageResource(R.drawable.ic_mute_on);
        } else {
            ivMute.setImageResource(R.drawable.ic_mute_off);
        }
    }

    private void startTime() {
        if (isBasketball) {
            return;
        }
        startTime(null);
    }

    private void startTime(Integer minute) {
        if (isBasketball) {
            return;
        }
        if (minute != null) {
            timeSecond = minute * 60;
            waterMarkContainer.getScoreBoard().setTime(String.format(Locale.getDefault(), "%02d:00", minute));
            reloadWatermark();
        }
        isTimeCountRun = true;
    }

    private void stopTime() {
        if (isBasketball) {
            return;
        }
        stopTime(null);
    }

    private void stopTime(Integer minute) {
        if (isBasketball) {
            return;
        }
        if (minute != null) {
            timeSecond = minute * 60;
            waterMarkContainer.getScoreBoard().setTime(String.format(Locale.getDefault(), "%02d:00", minute));
            reloadWatermark();
        }
        isTimeCountRun = false;
    }

    private void changeVideoQuality(int quality) {
        switch (quality) {
            case Constants.VideoQuality.LOW:
                mVideoConfiguration = new VideoConfiguration.Builder().build(VideoConfiguration.VideoQuality.VideoQuality_Low);
                mVideoQuality = Constants.VideoQuality.LOW;
                mLFLiveView.setVideoConfiguration(mVideoConfiguration);
                setVideoBitRate(mVideoConfiguration.maxBps);
                break;
            case Constants.VideoQuality.MID:
                mVideoConfiguration = new VideoConfiguration.Builder().build(VideoConfiguration.VideoQuality.VideoQuality_Mid);
                mVideoQuality = Constants.VideoQuality.MID;
                mLFLiveView.setVideoConfiguration(mVideoConfiguration);
                setVideoBitRate(mVideoConfiguration.maxBps);
                break;
            case Constants.VideoQuality.HIGH:
                mVideoConfiguration = new VideoConfiguration.Builder().build(VideoConfiguration.VideoQuality.VideoQuality_High);
                mVideoQuality = Constants.VideoQuality.HIGH;
                mLFLiveView.setVideoConfiguration(mVideoConfiguration);
                setVideoBitRate(mVideoConfiguration.maxBps);
                break;
        }
    }

    private void setVideoBitRate(int bps) {
        if (mLFLiveView != null) {
            boolean result = mLFLiveView.setVideoBps(bps);
            if (result) {
                mCurrentBps = bps;
            }
        }
    }

    private void setBrightness(float brightness) {
        Effect effect = new HSLEffect(this);
        effect.setL(brightness);
        setEffet(effect);
    }

    private void setEffet(Effect effet) {
        mLFLiveView.setEffect(effet);
    }

    private void setAutoFocus(boolean autoFocus) {
        isAutoFocus = !autoFocus;
        mLFLiveView.switchFocusMode();
    }

    private void gotoLoginActivity() {
        runOnUiThread(() -> {
            showToast("授权过期，请重新登录");
            readyGo(LoginActivity.class);
            finish();
            FinishActivityManager.getManager().finishActivity(MatchActivity.class);
            FinishActivityManager.getManager().finishActivity(MainActivity.class);
        });
    }

    private DialogPlus changeScoreDialog;
    private EditText etScore;
    private int eventType;
    private RadioGroup rgStatus;
    private RadioButton rbNotStart, rbStart, rbHalfTime, rbSecondHalf, rbFinish;
    private boolean isQueryOrChangeMatchVo = false;

    private void showChangeScoreDialog() {
        if (changeScoreDialog == null) {
            changeScoreDialog = DialogPlus.newDialog(this)
                    .setContentHolder(new ViewHolder(R.layout.dialog_change_scroe))
                    .setContentBackgroundResource(R.drawable.shape_circle_bottom_popup)
                    .setGravity(Gravity.BOTTOM)
                    .setCancelable(true)
                    .setOnClickListener((dialog, view) -> {
                        if (view.getId() == R.id.btn_confirm) {
                            if (TextUtils.isEmpty(etScore.getText().toString().trim())) {
                                showToast("输入的比分必须满足'X-X'格式");
                                return;
                            }
                            String[] temp = etScore.getText().toString().trim().split("-");
                            if (temp.length != 2) {
                                showToast("输入的比分必须满足'X-X'格式");
                                return;
                            }
                            try {
                                Integer.parseInt(temp[0]);
                                Integer.parseInt(temp[1]);
                            } catch (NumberFormatException e) {
                                showToast("输入的比分必须满足'X-X'格式");
                                return;
                            }

                            int selectedId = rgStatus.getCheckedRadioButtonId();
                            if (selectedId == -1) {
                                showToast("请选择比赛状态");
                                return;
                            }
                            switch (selectedId) {
                                case R.id.rb_not_start:
                                    eventType = UNOPEN;
                                    break;
                                case R.id.rb_start:
                                    eventType = START;
                                    break;
                                case R.id.rb_half_time:
                                    eventType = HALF_TIME;
                                    break;
                                case R.id.rb_second_half:
                                    eventType = SECOND_HALF;
                                    break;
                                case R.id.rb_finish:
                                    eventType = FINISH;
                                    break;
                            }
                            updateMatchScoreStatus();
                            dialog.dismiss();
                        }
                    })
                    .setExpanded(false)
                    .create();
            rgStatus = (RadioGroup) changeScoreDialog.findViewById(R.id.rg_status);
            etScore = (EditText) changeScoreDialog.findViewById(R.id.et_score);
            rbNotStart = (RadioButton) changeScoreDialog.findViewById(R.id.rb_not_start);
            rbStart = (RadioButton) changeScoreDialog.findViewById(R.id.rb_start);
            rbHalfTime = (RadioButton) changeScoreDialog.findViewById(R.id.rb_half_time);
            rbSecondHalf = (RadioButton) changeScoreDialog.findViewById(R.id.rb_second_half);
            rbFinish = (RadioButton) changeScoreDialog.findViewById(R.id.rb_finish);
        }
        rgStatus.clearCheck();
        etScore.setText(matchVo.getScore());
        switch (matchVo.getStatus()) {
            case UNOPEN:
                rbNotStart.setChecked(true);
                break;
            case START:
                rbStart.setChecked(true);
                break;
            case HALF_TIME:
                rbHalfTime.setChecked(true);
                break;
            case SECOND_HALF:
                rbSecondHalf.setChecked(true);
                break;
            case FINISH:
                rbFinish.setChecked(true);
                break;
        }
        FootballRequest request = RetrofitManager.getInstance().getRetrofit().create(FootballRequest.class);
        Call<ResponseEntity<MatchStatusDetail>> response = request.getMatchStatusDetailById(matchVo.getId());
        response.enqueue(new AutoRefreshTokenCallback<ResponseEntity<MatchStatusDetail>>() {
            @Override
            public void onRefreshTokenFail() {
                gotoLoginActivity();
            }

            @Override
            public void onSuccess(ResponseEntity<MatchStatusDetail> result) {
                matchStatusDetail = result.getData();
                etScore.setText(matchStatusDetail.getScore());
                changeScoreDialog.show();
            }

            @Override
            public void onFail(@Nullable Response<ResponseEntity<MatchStatusDetail>> response, @Nullable Throwable t) {
                if (response != null) {
                    showToast("请求失败:" + (response.body() != null ? response.body().getMessage() : ""));
                }
                if (t != null) {
                    showToast("网络请求失败:" + t.getMessage());
                }
            }
        });
    }

    private synchronized void updateMatchScoreStatus() {
        if (isQueryOrChangeMatchVo) {
            return;
        }
        isQueryOrChangeMatchVo = true;
        MatchVO paramMatchVO = new MatchVO();
        paramMatchVO.setId(matchVo.getId());
        paramMatchVO.setPenaltyScore(matchVo.getPenaltyScore());
        paramMatchVO.setStatus(eventType);
        paramMatchVO.setScore(etScore.getText().toString());
        FootballRequest request = RetrofitManager.getInstance().getRetrofit().create(FootballRequest.class);
        Call<ResponseEntity<Boolean>> response = request.updateMatchScoreStatus(paramMatchVO);
        response.enqueue(new AutoRefreshTokenCallback<ResponseEntity<Boolean>>() {
            @Override
            public void onRefreshTokenFail() {
                isQueryOrChangeMatchVo = false;
                gotoLoginActivity();
            }

            @Override
            public void onSuccess(ResponseEntity<Boolean> result) {
                isQueryOrChangeMatchVo = false;
                if (result.getData()) {
                    showToast("修改成功");
                    queryMatchStatusDetail();
                } else {
                    showToast("修改比赛状态请求失败:" + result.getMessage());
                }
            }

            @Override
            public void onFail(@Nullable Response<ResponseEntity<Boolean>> response, @Nullable Throwable t) {
                isQueryOrChangeMatchVo = false;
                if (response != null) {
                    showToast("修改比赛状态请求失败:" + (response.body() != null ? response.body().getMessage() : ""));
                }
                if (t != null) {
                    showToast("修改比赛状态请求失败:" + t.getMessage());
                }
            }
        });
    }

    private DialogPlus basketballEventDialog;

    private TextView tvHostName;
    private TextView tvGuestName;

    private Button btnHostReduce1;
    private Button btnHostReduce2;
    private Button btnHostReduce3;
    private Button btnGuestReduce1;
    private Button btnGuestReduce2;
    private Button btnGuestReduce3;

    private Button btnHostPlus1;
    private Button btnHostPlus2;
    private Button btnHostPlus3;
    private Button btnGuestPlus1;
    private Button btnGuestPlus2;
    private Button btnGuestPlus3;

    private Button btnPreSection;
    private Button btnNextSection;

    private boolean isUpdating = false;

    private Integer currentSection = 1;

    private void showBasketballEventDialog() {
        if (basketballEventDialog == null) {
            basketballEventDialog = DialogPlus.newDialog(this)
                    .setContentHolder(new ViewHolder(R.layout.dialog_event_basketball))
                    .setContentBackgroundResource(R.drawable.shape_circle_popup)
                    .setGravity(Gravity.CENTER)
                    .setCancelable(true)
                    .setContentWidth(1200)
                    .setOnClickListener((dialog, view) -> {
                        switch (view.getId()) {
                            case R.id.btn_host_plus1:
                                updateMatchScore(matchVo.getHostTeamId(), 1);
                                break;
                            case R.id.btn_host_plus2:
                                updateMatchScore(matchVo.getHostTeamId(), 2);
                                break;
                            case R.id.btn_host_plus3:
                                updateMatchScore(matchVo.getHostTeamId(), 3);
                                break;
                            case R.id.btn_host_reduce1:
                                updateMatchScore(matchVo.getHostTeamId(), -1);
                                break;
                            case R.id.btn_host_reduce2:
                                updateMatchScore(matchVo.getHostTeamId(), -2);
                                break;
                            case R.id.btn_host_reduce3:
                                updateMatchScore(matchVo.getHostTeamId(), -3);
                                break;
                            case R.id.btn_guest_plus1:
                                updateMatchScore(matchVo.getGuestTeamId(), 1);
                                break;
                            case R.id.btn_guest_plus2:
                                updateMatchScore(matchVo.getGuestTeamId(), 2);
                                break;
                            case R.id.btn_guest_plus3:
                                updateMatchScore(matchVo.getGuestTeamId(), 3);
                                break;
                            case R.id.btn_guest_reduce1:
                                updateMatchScore(matchVo.getGuestTeamId(), -1);
                                break;
                            case R.id.btn_guest_reduce2:
                                updateMatchScore(matchVo.getGuestTeamId(), -2);
                                break;
                            case R.id.btn_guest_reduce3:
                                updateMatchScore(matchVo.getGuestTeamId(), -3);
                                break;
                            case R.id.btn_pre_section:
                                updateMatchSection(-1);
                                break;
                            case R.id.btn_next_section:
                                updateMatchSection(1);
                                break;
                        }
                    })
                    .setExpanded(false)
                    .create();
            tvHostName = (TextView) basketballEventDialog.findViewById(R.id.tv_host_name);
            tvGuestName = (TextView) basketballEventDialog.findViewById(R.id.tv_guest_name);

            btnHostReduce1 = (Button) basketballEventDialog.findViewById(R.id.btn_host_reduce1);
            btnHostReduce2 = (Button) basketballEventDialog.findViewById(R.id.btn_host_reduce2);
            btnHostReduce3 = (Button) basketballEventDialog.findViewById(R.id.btn_host_reduce3);

            btnGuestReduce1 = (Button) basketballEventDialog.findViewById(R.id.btn_guest_reduce1);
            btnGuestReduce2 = (Button) basketballEventDialog.findViewById(R.id.btn_guest_reduce2);
            btnGuestReduce2 = (Button) basketballEventDialog.findViewById(R.id.btn_guest_reduce3);

            btnHostPlus1 = (Button) basketballEventDialog.findViewById(R.id.btn_host_plus1);
            btnHostPlus2 = (Button) basketballEventDialog.findViewById(R.id.btn_host_plus2);
            btnHostPlus3 = (Button) basketballEventDialog.findViewById(R.id.btn_host_plus3);

            btnGuestPlus1 = (Button) basketballEventDialog.findViewById(R.id.btn_guest_plus1);
            btnGuestPlus2 = (Button) basketballEventDialog.findViewById(R.id.btn_guest_plus2);
            btnGuestPlus2 = (Button) basketballEventDialog.findViewById(R.id.btn_guest_plus3);

            btnPreSection = (Button) basketballEventDialog.findViewById(R.id.btn_pre_section);
            btnNextSection = (Button) basketballEventDialog.findViewById(R.id.btn_next_section);
        }
        if (matchVo != null) {
            tvHostName.setText(matchVo.getHostTeam().getName());
            tvGuestName.setText(matchVo.getGuestTeam().getName());
        }
        basketballEventDialog.show();
    }

    private synchronized void updateMatchScore(Long teamId, Integer scoreChange) {
        if (isUpdating) {
            showToast("正在操作中...");
            return;
        }
        isUpdating = true;
        try {
            FootballRequest request = RetrofitManager.getInstance().getRetrofit().create(FootballRequest.class);
            Call<ResponseEntity<MatchStatusDetail>> response = request.getMatchStatusDetailById(matchVo.getId());
            response.enqueue(new AutoRefreshTokenCallback<ResponseEntity<MatchStatusDetail>>() {
                @Override
                public void onRefreshTokenFail() {
                    isUpdating = false;
                    gotoLoginActivity();
                }

                @Override
                public void onSuccess(ResponseEntity<MatchStatusDetail> result) {
                    isUpdating = false;
                    if (result.getData() != null) {
                        String score = result.getData().getScore();
                        String[] scores = score.split("-");
                        Integer hostScore = Integer.parseInt(scores[0]);
                        Integer guestScore = Integer.parseInt(scores[1]);
                        if (teamId.equals(matchVo.getHostTeamId()) && (hostScore + scoreChange > 0)) {
                            hostScore = hostScore + scoreChange;
                        } else if (teamId.equals(matchVo.getGuestTeamId()) && (guestScore + scoreChange > 0)) {
                            guestScore = guestScore + scoreChange;
                        } else {
                            showToast("请选择正确的比分");
                            isUpdating = false;
                            return;
                        }
                        MatchVO paramMatchVO = new MatchVO();
                        paramMatchVO.setId(matchVo.getId());
                        paramMatchVO.setPenaltyScore(matchVo.getPenaltyScore());
                        paramMatchVO.setStatus(0);
                        paramMatchVO.setScore(hostScore + "-" + guestScore);
                        FootballRequest request = RetrofitManager.getInstance().getRetrofit().create(FootballRequest.class);
                        Call<ResponseEntity<Boolean>> response = request.updateMatchScoreStatus(paramMatchVO);
                        response.enqueue(new AutoRefreshTokenCallback<ResponseEntity<Boolean>>() {
                            @Override
                            public void onRefreshTokenFail() {
                                isUpdating = false;
                                gotoLoginActivity();
                            }

                            @Override
                            public void onSuccess(ResponseEntity<Boolean> result) {
                                isUpdating = false;
                                if (result.getData()) {
                                    showToast("修改成功");
                                    queryMatchStatusDetail();
                                } else {
                                    showToast("修改比赛状态请求失败:" + result.getMessage());
                                }
                            }

                            @Override
                            public void onFail(@Nullable Response<ResponseEntity<Boolean>> response, @Nullable Throwable t) {
                                isUpdating = false;
                                if (response != null) {
                                    showToast("修改比赛状态请求失败:" + (response.body() != null ? response.body().getMessage() : ""));
                                }
                                if (t != null) {
                                    showToast("修改比赛状态请求失败:" + t.getMessage());
                                }
                            }
                        });
                    } else {
                        showToast("获取比赛状态请求失败:" + result.getMessage());
                    }
                }

                @Override
                public void onFail(@Nullable Response<ResponseEntity<MatchStatusDetail>> response, @Nullable Throwable t) {
                    isUpdating = false;
                    if (response != null) {
                        showToast("获取比赛状态请求失败:" + (response.body() != null ? response.body().getMessage() : ""));
                    }
                    if (t != null) {
                        showToast("获取比赛状态请求失败:" + t.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            isUpdating = false;
            showToast("修改失败");
        }
    }

    private void updateMatchSection(Integer sectionChange) {
        if (waterMarkContainer.getScoreBoard() != null) {
            if (currentSection + sectionChange <= 0) {
                currentSection = 1;
            } else {
                currentSection = currentSection + sectionChange;
            }
            waterMarkContainer.getScoreBoard().setTime(String.valueOf(currentSection));
        }
    }
    private void callPhone(){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:17750235615");
        intent.setData(data);
        startActivity(intent);
    }
}
