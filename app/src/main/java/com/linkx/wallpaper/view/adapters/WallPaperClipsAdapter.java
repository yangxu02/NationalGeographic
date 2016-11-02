package com.linkx.wallpaper.view.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.linkx.wallpaper.R;
import com.linkx.wallpaper.data.models.WallPaper;
import com.squareup.picasso.Picasso;

/**
 * Created by ulyx.yang@yeahmobi.com on 2016/11/1.
 */
public class WallPaperClipsAdapter extends RecyclerView.Adapter<WallPaperClipsAdapter.ViewHolder> {

    private WallPaper wallPaper;

    public WallPaperClipsAdapter(WallPaper wallPaper) {
        this.wallPaper = wallPaper;
    }

    @Override
    public WallPaperClipsAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_clip, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WallPaperClipsAdapter.ViewHolder viewHolder, int i) {
        String clipUrl = wallPaper.clips().get(i);
        Picasso.with(viewHolder.itemView.getContext())
            .load(clipUrl)
            .into(viewHolder.clipView);
    }

    @Override
    public int getItemCount() {
        return wallPaper.clips().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.clip)
        ImageView clipView;
        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}

