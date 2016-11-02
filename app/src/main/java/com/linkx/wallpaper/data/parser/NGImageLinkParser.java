package com.linkx.wallpaper.data.parser;

import android.util.Log;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.util.NodeList;

/**
 * Created by ulyx.yang@yeahmobi.com on 2016/11/2.
 */
public class NGImageLinkParser {
    private final static TagNode[] path = new TagNode[]{
        new TagNode("div").withAttr("class", "main photo-channel"),
        new TagNode("div").withAttr("id", "Article"),
        new TagNode("div").withAttr("class", "list-pic"),
        new TagNode("div").withAttr("class", "cont"),
        new TagNode("ul").withAttr("class", "cont picbig"),
        new TagNode("li"),
        new TagNode("div").withAttr("class", "img-wrap"),
        new TagNode("a"),
        new TagNode("img"),
    };

    public static String getLink(String pageUrl) {
        try {

            Parser htmlParser = new Parser(pageUrl);
            htmlParser.setEncoding("UTF-8");

            NodeList lastNodeList = htmlParser.extractAllNodesThatMatch(path[0].asFilter());
            if (null != lastNodeList && lastNodeList.size() > 0) {
                for (int i = 1; i < path.length; ++i) {
//                    System.out.println(i + " => filters=" + JSON.toJSON(path[i].asFilter()));
                    NodeList curNodeList = lastNodeList.elementAt(0).getChildren().extractAllNodesThatMatch(path[i].asFilter());
                    if (null == curNodeList || curNodeList.size() == 0) break;
                    lastNodeList = curNodeList;
                }
            }

            if (lastNodeList != null) {
                Node node = lastNodeList.elementAt(0);
                if (node instanceof ImageTag) {
                    ImageTag imageTag = (ImageTag) node;
                    return imageTag.getImageURL();
                }
            }
        } catch (Exception e) {
            Log.w("WP", e);
        }

        return "";
    }
}
