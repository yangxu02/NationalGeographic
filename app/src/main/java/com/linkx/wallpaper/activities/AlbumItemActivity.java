package com.linkx.wallpaper.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.linkx.wallpaper.R;
import com.linkx.wallpaper.data.models.AlbumItem;
import com.linkx.wallpaper.data.models.Model;
import com.linkx.wallpaper.data.services.NGImageService;
import com.linkx.wallpaper.utils.IntentUtil;
import com.linkx.wallpaper.view.Transition;
import rx.Subscriber;
import uk.co.senab.photoview.PhotoView;

public class AlbumItemActivity extends BaseActivity {

//    @Bind(R.id.drawer_layout)
//    DrawerLayout drawerLayout;
//    @Bind(R.id.navigation_view)
//    NavigationView navigationView;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.album_item_clip)
    PhotoView albumItemClip;
//    ImageViewTouchBase albumItemClip;
    @Bind(R.id.album_item_title)
    TextView albumItemTitle;
    @Bind(R.id.album_item_time)
    TextView albumItemTime;
    @Bind(R.id.album_item_desc)
    TextView albumItemDesc;
    @Bind(R.id.progress_bar)
    ProgressBar progressBar;
    @Bind(R.id.root_view)
    View rootView;

//    private PhotoViewAttacher  photoViewAttacher;
    private AlbumItem albumItem;
    private final static String SER_EXTRA_ALBUM_ITEM = "_album_item";

    Handler handler = new Handler(Looper.getMainLooper());

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
//        toolbar.setLogo(R.mipmap.ic_launcher);
        final Activity activity = this;
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int menuItemId = item.getItemId();
                if (menuItemId == R.id.action_share) {
//                    Toast.makeText(AlbumItemActivity.this , "share:item=" + albumItem.toJson() , Toast.LENGTH_SHORT).show();

                    NGImageService.getImageObservable(albumItem,
                        image -> {
                            if (null == image) {
                                Toast.makeText(AlbumItemActivity.this , "share failed:item=" + albumItem.id(), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            IntentUtil.startShareMediaActivity(activity, image);
                        }
                    );
                } else if (menuItemId == R.id.action_set_as_wallpaper) {
//                    Toast.makeText(AlbumItemActivity.this , "set as wallpaper:item=" + albumItem.toJson(), Toast.LENGTH_SHORT).show();

                    NGImageService.getImageObservable(albumItem,
                        image -> {
                            if (null == image) {
                                Toast.makeText(AlbumItemActivity.this , "set as wallpaper failed:item=" + albumItem.id(), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            IntentUtil.startSetAsActivity(activity, image);
                        }
                    );
                }
                return true;
            }
        });

//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);

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
        this.albumItemClip.setAdjustViewBounds(true);
        this.progressBar.setIndeterminate(true);
        this.albumItemTitle.setText(albumItem.title());
        this.albumItemTime.setText(getText(R.string.subscribeTime) + albumItem.inputTime());
        this.albumItemDesc.setText(albumItem.description());

        this.scheduleMenuAndTextHiding();

        NGImageService.getAlbumItemClip(this, albumItem)
            .subscribe(new Subscriber<NGImageService.ImageInfo>() {
                @Override
                public void onCompleted() {
                    Log.w("WP", "onCompleted");
                }
                @Override
                public void onError(Throwable e) {
                    Log.w("WP", e);
                    progressBar.setVisibility(View.GONE);
                    albumItemClip.setImageResource(R.mipmap.ic_gallery_empty2);
                }

                @Override
                public void onNext(NGImageService.ImageInfo clipInfo) {
                    progressBar.setVisibility(View.GONE);
                    albumItemClip.setImageBitmap(clipInfo.getBitmap());

                }
            });

        albumItemClip.setOnViewTapListener((view, v, v1) -> {
            Log.w("WP", "click on image");
            showMenuAndText();
            scheduleMenuAndTextHiding();
        });

    }

    void scheduleMenuAndTextHiding() {
         handler.postDelayed(() -> hideMenuAndText(), 3000);
    }

    void hideMenuAndText() {
        this.toolbar.setVisibility(View.GONE);
        this.albumItemTitle.setVisibility(View.GONE);
        this.albumItemTime.setVisibility(View.GONE);
        this.albumItemDesc.setVisibility(View.GONE);
    }

    void showMenuAndText() {
        this.albumItemTitle.setVisibility(View.VISIBLE);
        this.albumItemTime.setVisibility(View.VISIBLE);
        this.albumItemDesc.setVisibility(View.VISIBLE);
        this.toolbar.setVisibility(View.VISIBLE);
    }
}
