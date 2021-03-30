package com.qiezitv;

import android.util.Log;

import com.qiezitv.common.Constants;
import com.qiezitv.common.ImageLoaderUtil;
import com.qiezitv.common.SharedPreferencesUtil;
import com.qiezitv.exception.ExceptionHandler;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class Application extends android.app.Application {
    private static final String TAG = Application.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        SharedPreferencesUtil.setInstance(new SharedPreferencesUtil(this, Constants.SP_FILE_NAME));

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .memoryCacheExtraOptions(480, 800)
                // max width, max height，即保存的每个缓存文件的最大长宽
                // Can slow ImageLoader, use it carefully (Better don't use
                // it)/设置缓存的详细信息，最好不要设置这个
                .threadPoolSize(3)
                // 线程池内加载的数量
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024))
                // You can pass your own memory cache
                // implementation/你可以通过自己的内存缓存实现
                .memoryCacheSize(2 * 1024 * 1024)
                // 将保存的时候的URI名称用MD5 加密
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                // 缓存的文件数量
                // 自定义缓存路径
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .writeDebugLogs().build();// 开始构建
        ImageLoader.getInstance().init(config);
        ImageLoaderUtil.init();

//        initCrashHandler();
    }

    private void initCrashHandler() {
        //ExceptionHander的使用
        ExceptionHandler.install((thread, throwable) -> {
            if (throwable != null) {
                Log.e("060ExceptionHandler===", throwable.getMessage() + "");
            }
        });
    }
}
