package com.linkx.wallpaper.data.services;

import com.linkx.wallpaper.data.models.WallPaper;
import java.util.List;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by ulyx.yang@yeahmobi.com on 2016/11/1.
 */
public interface WallPaperListService {
    String ENDPOINT = "http://admin.yeahmagic.com.s3.amazonaws.com";

    @GET("/{tag}")
    Observable<List<WallPaper>> getWallPaperList(@Path("tag") String tag);
}
