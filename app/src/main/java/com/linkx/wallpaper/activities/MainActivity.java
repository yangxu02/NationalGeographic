package com.linkx.wallpaper.activities;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.etsy.android.grid.StaggeredGridView;
import com.google.common.collect.Lists;
import com.linkx.wallpaper.R;
import com.linkx.wallpaper.data.models.WallPaper;
import com.linkx.wallpaper.data.services.NGImageService;
import com.linkx.wallpaper.data.services.WallPaperListQueryService;
import com.linkx.wallpaper.view.adapters.AlbumItemAdapter;
import com.linkx.wallpaper.view.adapters.WallPaperListAdapter;
import com.linkx.wallpaper.view.listeners.EndlessRecyclerOnScrollListener;
import java.util.ArrayList;
import java.util.List;
import rx.Subscriber;

public class MainActivity extends BaseActivity {

    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;
//    @Bind(R.id.navigation_view)
//    NavigationView navigationView;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.toolbar_title)
    TextView toolbarTitle;
    @Bind(R.id.wallpaper_list)
    StaggeredGridView wallPaperListView;
    @Bind(R.id.album_container)
    RecyclerView albumContainer;

    private WallPaperListAdapter wallPaperListAdapter;

    private AlbumItemAdapter albumItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setupActionBar();
        setupViews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setupActionBar() {
//        toolbarTitle.setText("大家都在玩");
        toolbarTitle.setText("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

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

        albumItemAdapter = new AlbumItemAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        albumContainer.setLayoutManager(linearLayoutManager);
        albumContainer.addOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                Log.w("WP", "Load page:" + currentPage);
                albumItemAdapter.add(null);

                albumItemAdapter.removeLast();

                int nextPageNumber = currentPage - 1;
                NGImageService.getAlbumItemList(albumItemAdapter, "" + nextPageNumber);
            }
        });

        albumContainer.setAdapter(albumItemAdapter);
        NGImageService.getAlbumItemList(albumItemAdapter, "0");

        /*
        wallPaperListAdapter = new WallPaperListAdapter(this, new ArrayList<>());
        wallPaperListView.setAdapter(wallPaperListAdapter);

        WallPaperListQueryService service = new WallPaperListQueryService(getApplication());

        service.queryFromUI("hubble", WallPaper.class,  new Subscriber<List<WallPaper>>() {
            @Override
            public void onCompleted() {
                Log.d("Trends", "onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Log.w("Trends", e);
            }

            @Override
            public void onNext(List<WallPaper> wallPaperList) {
                wallPaperListAdapter.addAll(wallPaperList);
                Log.d("Trends", "onNext:data=" + wallPaperList);
            }
        });
        */
    }

}
