package com.linkx.wallpaper.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;
import com.linkx.wallpaper.R;
import com.linkx.wallpaper.data.models.IImage;

/**
 * Created by ulyx.yang@yeahmobi.com on 2016/11/4.
 */
public class IntentUtil {
    public static Intent createSetAsIntent(IImage image) {
        Uri u = image.fullSizeImageUri();
        Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
        intent.setDataAndType(u, image.imageMimeType());
        intent.putExtra("mimeType", image.imageMimeType());
        return intent;
    }

    public static Intent createShareMediaIntent(IImage image) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType(image.imageMimeType());
        intent.putExtra(Intent.EXTRA_STREAM, image.fullSizeImageUri());
        return intent;
    }

    public static void startShareMediaActivity(Activity activity, IImage image) {
        try {
            activity.startActivity(Intent.createChooser(createShareMediaIntent(image),
                activity.getText(R.string.sendImage)));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(activity, R.string.no_way_to_share_image, Toast.LENGTH_SHORT).show();
        }
    }

    public static void startSetAsActivity(Activity activity, IImage image) {
        try {
            activity.startActivity(Intent.createChooser(createSetAsIntent(image),
                activity.getText(R.string.setImage)));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(activity, R.string.no_way_to_set_image_as,
                Toast.LENGTH_SHORT).show();
        }
    }

}
