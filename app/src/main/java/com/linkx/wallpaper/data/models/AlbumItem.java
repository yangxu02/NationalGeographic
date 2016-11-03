package com.linkx.wallpaper.data.models;

import android.text.TextUtils;
import android.util.Log;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.linkx.wallpaper.utils.TextUtil;
import java.util.List;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by ulyx.yang on 2016/9/15.
 */
@AutoValue
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AlbumItem extends Model {
    @JsonCreator
    public static AlbumItem create(@JsonProperty("id") String id,
                                   @JsonProperty("title") String title,
                                   @JsonProperty("description") String description,
                                   @JsonProperty("thumb") String thumb,
                                   @JsonProperty("url") String itemLink,
                                   @JsonProperty("inputtime") String inputTime
    ) {
        return new AutoValue_AlbumItem(id,
            TextUtil.strip(title, "每日一图："),
            description,
//            StringEscapeUtils.escapeJava(title),
//            StringEscapeUtils.escapeJava(description),
            thumb, itemLink, inputTime);
    }

    @JsonProperty("id")
    public abstract String id();

    @JsonProperty("title")
    public abstract String title();

    @JsonProperty("description")
    public abstract String description();

    @JsonProperty("thumb")
    public abstract String thumb();

    @JsonProperty("url")
    public abstract String itemLink();

    @JsonProperty("inputtime")
    public abstract String inputTime();

    @Override
    public String identity() throws MethodNotOverrideException {
        return id();
    }

}
