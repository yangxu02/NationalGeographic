package com.linkx.wallpaper.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.linkx.wallpaper.R;
import com.linkx.wallpaper.data.models.AlbumItem;
import com.linkx.wallpaper.data.models.Model;
import com.linkx.wallpaper.data.models.WallPaper;
import com.linkx.wallpaper.view.Transition;
import com.linkx.wallpaper.view.adapters.WallPaperClipsAdapter;
import com.squareup.picasso.Picasso;

public class AlbumItemActivity extends BaseActivity {

//    @Bind(R.id.drawer_layout)
//    DrawerLayout drawerLayout;
//    @Bind(R.id.navigation_view)
//    NavigationView navigationView;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.album_item_clip)
    ImageView albumItemClip;
    @Bind(R.id.album_item_title)
    TextView albumItemTitle;
    @Bind(R.id.album_item_desc)
    TextView albumItemDesc;

    private AlbumItem albumItem;
    private final static String SER_EXTRA_ALBUM_ITEM = "_album_item";

    public static void launch(Activity activity, AlbumItem albumItem, Transition transition) {
        Intent intent = new Intent(activity, AlbumItemActivity.class);
        intent.putExtra(SER_EXTRA_ALBUM_ITEM, albumItem.toJson());
        Transition.putTransition(intent, transition);
        activity.startActivity(intent);
        transition.overrideOpenTransition(activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        albumItem = Model.fromJson(getIntent().getStringExtra(SER_EXTRA_ALBUM_ITEM), AlbumItem.class);
        setContentView(R.layout.activity_album_item);
        ButterKnife.bind(this);
        setupActionBar();
        setupViews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setupActionBar() {
        toolbar.inflateMenu(R.menu.menu_toolbar_album_item);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int menuItemId = item.getItemId();
                if (menuItemId == R.id.action_share) {
                    Toast.makeText(AlbumItemActivity.this , "share:item=" + albumItem.toJson() , Toast.LENGTH_SHORT).show();
                } else if (menuItemId == R.id.action_set_as_wallpaper) {
                    Toast.makeText(AlbumItemActivity.this , "set as wallpaper:item=" + albumItem.toJson(), Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        /*
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close);
        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerToggle.syncState();
        */

    }

    private void setupViews() {
           Picasso.with(this)
                .load(albumItem.thumb())
                .into(this.albumItemClip);
            this.albumItemTitle.setText(albumItem.title());
            this.albumItemDesc.setText(albumItem.description());
    }
}
