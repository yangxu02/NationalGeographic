package com.linkx.wallpaper.data.parser;

import android.util.Pair;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import org.htmlparser.NodeFilter;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;

/**
 * Created by ulyx.yang@yeahmobi.com on 2016/11/2.
 */

class TagNode {
    String tag;
    List<Pair<String, String>> attrs = new ArrayList<>();

    public TagNode(String tag) {
        this.tag = tag;
    }

    public TagNode withAttr(String key, String val) {
        if (!Strings.isNullOrEmpty(key)) {
            attrs.add(new Pair<String, String>(key, val));
        }
        return this;
    }

    public AndFilter asFilter() {
        List<NodeFilter> filters = Lists.newArrayList((NodeFilter) new TagNameFilter(tag));
        filters.addAll(Lists.transform(attrs, new Function<Pair<String,String>, NodeFilter>() {
            @Nullable
            @Override
            public NodeFilter apply(@Nullable Pair<String, String> attr) {
                if (null == attr) return null;
                return (NodeFilter) new HasAttributeFilter(attr.first, attr.second);
            }
        }));
        return new AndFilter(Iterables.toArray(filters, NodeFilter.class));
    }
}
