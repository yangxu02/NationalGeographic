package com.linkx.wallpaper.data.services;

import android.content.Context;
import android.util.Log;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.linkx.wallpaper.data.models.AlbumItem;
import com.linkx.wallpaper.data.models.Model;
import com.linkx.wallpaper.data.models.WallPaper;
import com.linkx.wallpaper.utils.AssetsUtil;
import com.linkx.wallpaper.utils.IOUtil;
import com.linkx.wallpaper.utils.QueryContextUtils;
import com.linkx.wallpaper.view.adapters.AlbumItemAdapter;
import com.linkx.wallpaper.view.adapters.WallPaperListAdapter;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.OnErrorThrowable;
import rx.schedulers.Schedulers;

public class NGImageService implements ICachedDataService {

    public final static String ENDPOINT = "http://www.nationalgeographic.com.cn";
    public interface NGImageApi {
        @GET("/index.php?m=content&c=index&a=loadmorebya&catid=39&modelid=3&parentid=11")
        Observable<List<AlbumItem>> getAlbumItemList(@Query("num") String pageNumber);
    }

    private Context appCtx;

    public NGImageService(Context appCtx) {
        this.appCtx = appCtx;
    }

    @Override
    public String getCachedDataFileName(String tag) {
        return IOUtil.cachedDataFileName("./wallpaper/data", tag);
    }

    public static void getAlbumItemList(final AlbumItemAdapter adapter, String pageNumber) {
        NGImageApi ngImageApi =
            ServiceFactory.createServiceFrom(NGImageApi.class, ENDPOINT);

        Observable.from(new String[] {pageNumber}).flatMap(ngImageApi::getAlbumItemList)
            .subscribeOn(Schedulers.io())
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
