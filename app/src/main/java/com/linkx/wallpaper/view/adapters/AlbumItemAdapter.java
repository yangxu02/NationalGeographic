package com.linkx.wallpaper.view.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.linkx.wallpaper.R;
import com.linkx.wallpaper.data.models.AlbumItem;
import com.linkx.wallpaper.data.models.WallPaper;
import com.linkx.wallpaper.view.components.timeline.LineType;
import com.linkx.wallpaper.view.components.timeline.TimelineView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ulyx.yang@yeahmobi.com on 2016/11/1.
 */
public class AlbumItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<AlbumItem> itemList = new ArrayList<>();

    private final int VIEW_TYPE_LOADING = -1;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (VIEW_TYPE_LOADING == viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_album_loading, viewGroup, false);
            return new LoadingViewHolder(view);
        } else {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_album, viewGroup, false);
            return new AlbumItemViewHolder(view, viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof AlbumItemViewHolder) {
            AlbumItem item = itemList.get(position);
            Picasso.with(viewHolder.itemView.getContext())
                .load(item.thumb())
                .into(((AlbumItemViewHolder)viewHolder).thumbView);
            Log.w("WP", "total=" + getItemCount() + ",pos=" + position + ",viewType=" + getItemViewType(position));
        } else {
            ((LoadingViewHolder)viewHolder).progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemViewType(int position) {
        AlbumItem item = itemList.get(position);
        return null == item ? VIEW_TYPE_LOADING : TimelineView.getTimeLineViewType(position, getItemCount());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public AlbumItemAdapter add(AlbumItem item) {
        this.itemList.add(item);
        notifyItemInserted(itemList.size() - 1);
        return this;
    }

    public AlbumItemAdapter addAll(List<AlbumItem> items) {
        this.itemList.addAll(items);
        notifyDataSetChanged();
        return this;
    }

    public AlbumItemAdapter removeLast() {
        int index = itemList.size() - 1;
        this.itemList.remove(index);
        notifyItemRemoved(itemList.size());
        return this;
    }



    public class AlbumItemViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.time_marker)
        TimelineView timelineView;
        @Bind(R.id.album_item_thumb)
        ImageView thumbView;
        public AlbumItemViewHolder(View view, int viewType) {
            super(view);
            ButterKnife.bind(this, view);
            timelineView.initLine(viewType);
        }

    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.progress_bar)
        ProgressBar progressBar;
        public LoadingViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

    }
}

