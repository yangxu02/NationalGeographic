package com.linkx.wallpaper.data.services;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.Pair;
import android.widget.ImageView;
import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.linkx.wallpaper.utils.IOUtil;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class NGImageDownloader {

    private int errorResource;
    private String url;
    private Context context;
    private NGImageDownloader(Context context) {
        this.context = context;
    }

    public static NGImageDownloader with(Context context) {
        return new NGImageDownloader(context);
    }

    public NGImageDownloader load(final String url) {
        this.url = url;
        return this;
    }

    public void into(final ImageView imageView) {
        this.get().subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread()).subscribe(
            bitmapInfo -> {
                if (null == bitmapInfo) {
                    imageView.setImageResource(errorResource);
                } else {
                    imageView.setImageBitmap(bitmapInfo.second);
                }
            }
        );
    }

    public Observable<Pair<String, Bitmap>> get() {
        String localPath = cacheFilePath(this.url);
        File localFile = new File(localPath);
        return Observable.just(url).flatMap(
            s -> {
                if (localFile.exists()) {
                    try {
                        return Observable.just(new Pair<>(localPath, Picasso.with(context).load(localFile).get()));
                    } catch (IOException e) {
                        Log.e("WP", "", e);
                    }
                } else {
                    try {
                        Bitmap bitmap = Picasso.with(context).load(url).get();
                        try {
                            if (!localFile.getParentFile().exists()) {
                                Files.createParentDirs(localFile);
                            }
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(localFile));
                            return Observable.just(new Pair<>(localPath, bitmap));
                        } catch (Exception e) {
                            return Observable.just(new Pair<>("", bitmap));
                        }
                    } catch (IOException e) {
                        Log.e("WP", "", e);
                    }
                }
                return Observable.just(null);
            }
        );
    }

    public NGImageDownloader error(final int resource) {
        this.errorResource = resource;
        return this;
    }

    private String cacheFilePath(String url) {
        return IOUtil.cacheFilePath(Hashing.md5().hashString(url, Charsets.UTF_8).toString()) + ".png";
    }

}
