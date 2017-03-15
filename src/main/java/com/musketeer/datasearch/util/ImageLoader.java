package com.musketeer.datasearch.util;

import android.widget.ImageView;

import com.musketeer.datasearch.service.MainImageLoadService;

/**
 * Created by zhongxuqi on 16-5-7.
 */
public class ImageLoader {
    public static void loadImage(ImageView iv, String url) {
        MainImageLoadService.loadImage(iv, url);
    }
}
