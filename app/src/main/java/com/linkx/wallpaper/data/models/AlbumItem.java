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
public abstract class AlbumItem extends Model {
    @JsonCreator
    public static AlbumItem create(@JsonProperty("id") String id,
                                   @JsonProperty("title") String title,
                                   @JsonProperty("description") String description,
                                   @JsonProperty("thumb") String thumb,
                                   @JsonProperty("url") String itemLink,
                                   @JsonProperty("inputtime") String inputTime
    ) {
        return new AutoValue_AlbumItem(id, title, description, thumb, itemLink, inputTime);
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
