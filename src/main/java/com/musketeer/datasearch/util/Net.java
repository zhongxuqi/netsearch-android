package com.musketeer.datasearch.util;

import com.musketeer.datasearch.request.RequestURLs;

/**
 * Created by zhongxuqi on 16-5-7.
 */
public class Net {
    public static String getRootUrl() {
        if (RequestURLs.Path.endsWith("/")) {
            return RequestURLs.Path;
        } else {
            return RequestURLs.Path.concat("/");
        }
    }

    public static String getAbsoluteUrl(String url) {
        String realUrl = getRootUrl();
        if (url.startsWith("/")) {
            realUrl = realUrl.concat(url.substring(1));
        } else {
            realUrl = realUrl.concat(url);
        }
        if (!url.endsWith("?")) {
            realUrl = realUrl.concat("?");
        }
        return realUrl;
    }

    public static String getAbsoluteImageUrl(String url) {
        return getAbsoluteUrl(url);
    }
}
