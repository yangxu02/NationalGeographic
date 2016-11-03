package com.linkx.wallpaper.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.linkx.wallpaper.R;
import com.linkx.wallpaper.data.services.NGImageService;
import com.linkx.wallpaper.view.adapters.AlbumItemAdapter;
import com.linkx.wallpaper.view.adapters.WallPaperListAdapter;
import com.linkx.wallpaper.view.listeners.EndlessRecyclerOnScrollListener;

public class MainActivity extends BaseActivity {

//    @Bind(R.id.drawer_layout)
//    DrawerLayout drawerLayout;
//    @Bind(R.id.navigation_view)
//    NavigationView navigationView;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
//    @Bind(R.id.toolbar_title)
//    TextView toolbarTitle;

    @Bind(R.id.history_album_container)
    RecyclerView historyAlbumContainer;

    @Bind(R.id.album_container)
    RecyclerView albumContainer;


    private WallPaperListAdapter wallPaperListAdapter;

    private AlbumItemAdapter historyAlbumItemAdapter;
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
        setSupportActionBar(toolbar);
//        getSupportActionBar().setLogo(this.getApplicationInfo().icon);
//        getSupportActionBar().setTitle(this.getApplicationInfo().name);
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

        historyAlbumItemAdapter = new AlbumItemAdapter(AlbumItemAdapter.AdapterType.ALBUM_HISTORY);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        historyAlbumContainer.setLayoutManager(linearLayoutManager);
        historyAlbumContainer.setAdapter(historyAlbumItemAdapter);
        historyAlbumItemAdapter.addLoadingView();
        int pageNumber = 60 / 15;
        NGImageService.getAlbumItemList(historyAlbumItemAdapter, "" + pageNumber, 3);

        albumItemAdapter = new AlbumItemAdapter();
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        albumContainer.setLayoutManager(linearLayoutManager);
        albumContainer.addOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                Log.w("WP", "Load page:" + currentPage);
//                albumItemAdapter.addLoadingView();
                int nextPageNumber = currentPage - 1;
                NGImageService.getAlbumItemList(albumItemAdapter, "" + nextPageNumber);
            }
        });

        albumContainer.setAdapter(albumItemAdapter);
        albumItemAdapter.addLoadingView();
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
