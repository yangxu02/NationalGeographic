package com.linkx.wallpaper.data.models;

import android.net.Uri;

/**
 * Created by ulyx.yang@yeahmobi.com on 2016/11/4.
 */
public interface IImage {
    Uri fullSizeImageUri();

    String imageMimeType();
}
