package com.linkx.wallpaper.data.services;

import android.content.Context;
import android.util.Log;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.linkx.wallpaper.data.models.Model;
import com.linkx.wallpaper.utils.AssetsUtil;
import com.linkx.wallpaper.utils.IOUtil;
import com.linkx.wallpaper.utils.QueryContextUtils;
import com.linkx.wallpaper.view.adapters.WallPaperListAdapter;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.OnErrorThrowable;
import rx.schedulers.Schedulers;

public class WallPaperListQueryService implements ICachedDataService {

    private Context appCtx;

    public WallPaperListQueryService(Context appCtx) {
        this.appCtx = appCtx;
    }

    @Override
    public String getCachedDataFileName(String tag) {
        return IOUtil.cachedDataFileName("./wallpaper/data", tag);
    }

    public static void getWallPaperList(final WallPaperListAdapter adapter, String tag) {
        WallPaperListService wallPaperListService =
            ServiceFactory.createServiceFrom(WallPaperListService.class, WallPaperListService.ENDPOINT);

        Observable.from(new String[] {tag}).flatMap(wallPaperListService::getWallPaperList)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(adapter::addAll);
    }


    /**
     * should update from network when file not exist or file content is one day before
     *
     * @param tag: file tag
     * @return true if should update
     */
    private boolean shouldLoadFromNetwork(String tag) {
        String fileName = getCachedDataFileName(tag);
        File file = new File(fileName);
        if (!file.exists()) return true;

        long lastModified = file.lastModified();
        long now = System.currentTimeMillis();

        return (TimeUnit.MILLISECONDS.toDays(now - lastModified) >= 1);
    }

    private <T extends Model> List<T> asFileSource(String tag, Class<T> clazz) throws IOException, Model.MethodNotOverrideException {
        String fileName = getCachedDataFileName(tag);
        String content = "";
        if (shouldLoadFromNetwork(tag)) {
            content = IOUtil.readFromNetworkAndCache(QueryContextUtils.url(tag), fileName);
            if (Strings.isNullOrEmpty(content)) {
                content = AssetsUtil.getContent(appCtx, tag);
            }
        } else {
            content = Files.toString(new File(fileName), Charsets.UTF_8);
        }
        if (Strings.isNullOrEmpty(content)) return Collections.emptyList();
        return Model.listFromJson(content, clazz);
    }

    public <T extends Model> Observable<List<T>> baseDetailObservable(String tag, Class<T> clazz) {
        return Observable.defer(() -> {
            try {
                List<T> dataList = asFileSource(tag, clazz);
                return Observable.just(dataList);
            } catch (IOException | Model.MethodNotOverrideException e) {
                throw OnErrorThrowable.from(e);
            }
        });
    }

    public <T extends Model> void queryFromUISilently(String tag, Class<T> clazz) {
        Subscriber<List<T>> subscriber = new Subscriber<List<T>>() {
            String tag = clazz.getSimpleName();

            @Override
            public void onCompleted() {
                Log.d(tag, "onCompleted()");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(tag, "onError()", e);
            }

            @Override
            public void onNext(List<T> ts) {
                Log.d(tag, "onNext():data=" + ((Model) ts).toJson());
            }
        };

        queryFromUI(tag, clazz, subscriber);
    }

    public <T extends Model> void queryFromUI(String tag, Class<T> clazz, Subscriber<List<T>> subscriber) {
        queryFromUI(tag, clazz).subscribe(subscriber);
    }

    private <T extends Model> Observable<List<T>> queryFromUI(String tag, Class<T> clazz) {
        return baseDetailObservable(tag, clazz)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

}
