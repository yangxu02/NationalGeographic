package com.linkx.wallpaper.view.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.linkx.wallpaper.R;
import com.linkx.wallpaper.activities.WallPaperActivity;
import com.linkx.wallpaper.data.models.WallPaper;
import com.linkx.wallpaper.view.Transition;
import com.squareup.picasso.Picasso;
import java.util.List;

/**
 * Created by ulyx.yang@yeahmobi.com on 2016/11/1.
 */
public class WallPaperListAdapter extends BindableAdapter<WallPaper> {
    public WallPaperListAdapter(Context context, List<WallPaper> wallPaperList) {
        super(context, wallPaperList);
    }

    @Override
    public View newView(LayoutInflater inflater, int position, ViewGroup container) {
        View view = inflater.inflate(R.layout.list_item_wallpaper, null, false);
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(WallPaper wallPaper, int position, View view) {
        final ViewHolder holder = (ViewHolder) view.getTag();

        Picasso.with(view.getContext())
            .load(wallPaper.thumb())
            .into(holder.wallPaperThumb);

        holder.wallPaperTitle.setText(wallPaper.title());

        view.setOnClickListener(v -> {
            Activity activity = (Activity) v.getContext();
            WallPaperActivity.launch(activity, wallPaper, Transition.PUSH_UP);
        });
    }

    static class ViewHolder {
        @Bind(R.id.wallpaper_thumb)
        ImageView wallPaperThumb;
        @Bind(R.id.wallpaper_title)
        TextView wallPaperTitle;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
