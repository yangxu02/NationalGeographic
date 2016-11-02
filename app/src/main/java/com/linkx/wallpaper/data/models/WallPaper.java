package com.linkx.wallpaper.data.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import java.util.List;

/**
 * Created by ulyx.yang on 2016/9/15.
 */
@AutoValue
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class WallPaper extends Model {
    @JsonCreator
    public static WallPaper create(@JsonProperty("id") String id,
                                    @JsonProperty("title") String title,
                                    @JsonProperty("thumb_link") String thumb,
                                    @JsonProperty("clip_real_links") List<String> clips
    ) {
        return new AutoValue_WallPaper(id, title, thumb, clips);
    }

    @JsonProperty("id")
    public abstract String id();

    @JsonProperty("title")
    public abstract String title();

    @JsonProperty("thumb_link")
    public abstract String thumb();

    @JsonProperty("clip_real_links")
    public abstract List<String> clips();

    @Override
    public String identity() throws MethodNotOverrideException {
        return id();
    }

}
