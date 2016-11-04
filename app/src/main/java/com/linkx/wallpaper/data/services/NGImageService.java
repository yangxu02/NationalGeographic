package com.linkx.wallpaper.data.services;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.linkx.wallpaper.data.models.AlbumItem;
import com.linkx.wallpaper.data.models.IImage;
import com.linkx.wallpaper.data.models.Image;
import com.linkx.wallpaper.data.models.ImageType;
import com.linkx.wallpaper.data.models.Model;
import com.linkx.wallpaper.data.parser.NGImageLinkParser;
import com.linkx.wallpaper.utils.AssetsUtil;
import com.linkx.wallpaper.utils.IOUtil;
import com.linkx.wallpaper.utils.QueryContextUtils;
import com.linkx.wallpaper.view.adapters.AlbumItemAdapter;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.OnErrorThrowable;
import rx.functions.Action1;
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

    public interface NGImageCacheApi {
        void save(Image image);

        Observable<IImage> get(AlbumItem albumItem);
    }

    public static class NGImageCache implements NGImageCacheApi {
        @Override
        public void save(Image image) {
            String fileName = IOUtil.dataFilePath("clip_" + image.identity());
            try {
                IOUtil.writeLine(fileName, image.toJson());
            } catch (IOException e) {
                Log.w("WP", e);
            }
        }

        @Override
        public Observable<IImage> get(AlbumItem albumItem) {
            String fileName = IOUtil.dataFilePath("clip_" + albumItem.identity());
            try {
                File file = new File(fileName);
                if (file.exists()) {
                    return Observable.just(Model.fromJson(Files.readFirstLine(file, Charsets.UTF_8), Image.class));
                }
            } catch (IOException e) {
                Log.w("WP", e);
            }
            return Observable.just(null);
        }
    }


    private Context appCtx;

    public NGImageService(Context appCtx) {
        this.appCtx = appCtx;
    }

    @Override
    public String getCachedDataFileName(String tag) {
        return IOUtil.cachedDataFileName("./wallpaper/data", tag);
    }

    public static class ImageInfo {
        String id;
        String link;
        String mimeType;
        String localPath;
        Bitmap bitmap;

        public Bitmap getBitmap() {
            return bitmap;
        }
    }


    public static Observable<ImageInfo> getAlbumItemClip(final Context context, final AlbumItem albumItem) {
        return new NGImageCache().get(albumItem).flatMap(iImage -> {
            if (null != iImage) {
                Image image = (Image) iImage;
                ImageInfo imageInfo = new ImageInfo();
                imageInfo.id = image.source().id();
                imageInfo.link = image.url();

                imageInfo.localPath = image.localPath();
                imageInfo.bitmap = BitmapFactory.decodeFile(image.localPath());
                imageInfo.mimeType = image.mimeType();

                Log.d("WP", "load from disk");

                return Observable.just(imageInfo);
            } else {
                Log.d("WP", "load from 2 stage network");
                NGImageClipApi ngImageClipApi = ServiceFactory.createServiceFrom(NGImageClipApi.class, ENDPOINT, null);
                return Observable.just(albumItem.itemLink()).flatMap(ngImageClipApi::getAlbumItemClipLink)
                    .flatMap(responseBody -> {
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
                        ImageInfo imageInfo = new ImageInfo();
                        imageInfo.id = albumItem.id();
                        imageInfo.link = clipLink;
                        return Observable.just(imageInfo);
                    })
                    .flatMap(imageInfo -> {
                        NGImageDownloader.with(context).load(imageInfo.link).get()
                            .subscribe(imageBitMap -> {
                                if (null == imageBitMap) {
                                    throw OnErrorThrowable.from(new Exception("get bitmap of " + imageInfo.link + " failed"));
                                }
                                imageInfo.localPath = imageBitMap.first;
                                imageInfo.bitmap = imageBitMap.second;
                                imageInfo.mimeType = "image/png";
                            });
                        return Observable.just(imageInfo);
                    })
                    .flatMap(imageInfo -> {
                        // save image information to disk
                        Image image = Image.create(albumItem.id(), ImageType.clip, imageInfo.link,
                            imageInfo.mimeType, imageInfo.localPath, albumItem);
                        new NGImageCache().save(image);
                        return Observable.just(imageInfo);
                    });
            }

        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
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

    public static void getImageObservable(final AlbumItem albumItem, Action1<IImage> onNext) {
        NGImageCache ngImageCache = new NGImageCache();
        Observable.just(albumItem).flatMap(ngImageCache::get)
            .subscribeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe(onNext);
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
