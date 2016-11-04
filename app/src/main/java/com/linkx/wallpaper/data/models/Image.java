package com.linkx.wallpaper.data.models;

import android.net.Uri;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import java.io.File;

/**
 * Created by ulyx.yang@yeahmobi.com on 2016/11/4.
 */
@AutoValue
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class Image extends Model implements IImage {
    @JsonProperty("id")
    public abstract String id();

    @JsonProperty("type") // clip or thumb
    public abstract ImageType type();

    @JsonProperty("url")
    public abstract String url();

    @JsonProperty("mimeType")
    public abstract String mimeType();

    @JsonProperty("localPath")
    public abstract String localPath();

    @JsonProperty("source")
    public abstract AlbumItem source();

    @JsonCreator
    public static Image create(@JsonProperty("id") String id,
                               @JsonProperty("type") ImageType type,
                               @JsonProperty("url") String url,
                               @JsonProperty("mimeType") String mimeType,
                               @JsonProperty("localPath") String localPath,
                               @JsonProperty("source") AlbumItem source) {
        return new AutoValue_Image(id, type, url, mimeType, localPath, source);
    }

    @Override
    public Uri fullSizeImageUri() {
        return Uri.fromFile(new File(localPath()));
    }

    @Override
    public String identity() {
        return id();
    }

    @Override
    public String imageMimeType() {
        return mimeType();
    }
}
