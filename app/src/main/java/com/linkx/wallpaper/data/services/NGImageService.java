package com.linkx.wallpaper.data.services;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.linkx.wallpaper.R;
import com.linkx.wallpaper.data.models.AlbumItem;
import com.linkx.wallpaper.data.models.Model;
import com.linkx.wallpaper.data.models.WallPaper;
import com.linkx.wallpaper.data.parser.NGImageLinkParser;
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

import com.squareup.picasso.Picasso;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.OnErrorThrowable;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class NGImageService implements ICachedDataService {

    public final static String ENDPOINT = "http://www.nationalgeographic.com.cn";
    public final static int PAGESIZE = 15;
    public interface NGImageApi {
        @GET("/index.php?m=content&c=index&a=loadmorebya&catid=39&modelid=3&parentid=11")
        Observable<List<AlbumItem>> getAlbumItemList(@Query("num") String pageNumber);
    }

    public interface NGImageClipApi {
        @GET("/{item_link}")
        Observable<ResponseBody> getAlbumItemClipLink(@Path("item_link") String itemLink);
    }

    private Context appCtx;

    public NGImageService(Context appCtx) {
        this.appCtx = appCtx;
    }

    @Override
    public String getCachedDataFileName(String tag) {
        return IOUtil.cachedDataFileName("./wallpaper/data", tag);
    }

    public static void getAlbumItemClip(final AlbumItem albumItem, final ImageView clipView) {
        NGImageClipApi ngImageClipApi = ServiceFactory.createServiceFrom(NGImageClipApi.class, ENDPOINT, null);
        Observable.just(albumItem.itemLink()).flatMap(ngImageClipApi::getAlbumItemClipLink)
                .flatMap(new Func1<ResponseBody, Observable<String>>() {
                    @Override
                    public Observable<String> call(ResponseBody responseBody) {
                        String clipLink = "";
                        try {
                            clipLink = NGImageLinkParser.getLinkFromHtml(responseBody.string());
                            Log.d("WP", "clip=" + clipLink + ",thumb=" + albumItem.thumb());
                        } catch (Exception e) {
                            Log.w("WP", e);
                        }
                        if (Strings.isNullOrEmpty(clipLink)) {
                            throw OnErrorThrowable.from(new Exception("empty clipLink"));
                        }
                        return Observable.just(clipLink);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        Log.w("WP", "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.w("WP", e);
                        clipView.setImageResource(R.mipmap.ic_gallery_empty2);
                    }

                    @Override
                    public void onNext(String clipLink) {
                        Picasso.with(clipView.getContext()).load(clipLink).error(R.mipmap.ic_gallery_empty2).into(clipView);
                    }
                });
    }

    public static void getAlbumItemList(final AlbumItemAdapter adapter,
                                        String pageNumber) {
        getAlbumItemList(adapter, pageNumber, PAGESIZE);
    }

    public static void getAlbumItemList(final AlbumItemAdapter adapter,
                                        String pageNumber,
                                        int limit) {
        NGImageApi ngImageApi =
            ServiceFactory.createServiceFrom(NGImageApi.class, ENDPOINT);

        Observable.just(pageNumber).flatMap(ngImageApi::getAlbumItemList)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Subscriber<List<AlbumItem>>() {
                @Override
                public void onCompleted() {
                    Log.w("WP", "onCompleted");
                }

                @Override
                public void onError(Throwable e) {
                    Log.w("WP", e);
                    adapter.removeLast();
                }

                @Override
                public void onNext(List<AlbumItem> items) {
                    if (limit >= items.size()) {
                        adapter.addAll(items);
                    } else {
                        for (int i = 0; i < limit; ++i) {
                            adapter.add(items.get(i));
                        }
                    }
                }
            });
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
