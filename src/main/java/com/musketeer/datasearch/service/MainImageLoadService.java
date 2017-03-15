package com.musketeer.datasearch.service;

import com.musketeer.baselibrary.service.ImageLoaderService;
import com.musketeer.baselibrary.util.LogUtils;

/**
 * Created by zhongxuqi on 16-5-7.
 */
public class MainImageLoadService extends ImageLoaderService {
    public static final String TAG = "MainImageLoadService";

    private static MainImageLoadService instance;

    public static MainImageLoadService getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.d(TAG, "[MainImageLoadService] On Create");
        instance = this;
    }
}
