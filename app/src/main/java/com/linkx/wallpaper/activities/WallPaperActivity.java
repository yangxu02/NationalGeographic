package com.linkx.wallpaper.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.linkx.wallpaper.R;
import com.linkx.wallpaper.data.models.Model;
import com.linkx.wallpaper.data.models.WallPaper;
import com.linkx.wallpaper.view.Transition;
import com.linkx.wallpaper.view.adapters.WallPaperClipsAdapter;

public class WallPaperActivity extends BaseActivity {

    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;
//    @Bind(R.id.navigation_view)
//    NavigationView navigationView;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.toolbar_title)
    TextView toolbarTitle;
    @Bind(R.id.horizontal_recycler_view)
    RecyclerView recyclerView;
    @Bind(R.id.button_set_wallpaper)
    View buttonSetWallPaper;

    private WallPaper wallPaper;
    private final static String SER_EXTRA_WP = "_wp";

    public static void launch(Activity activity, WallPaper wallPaper, Transition transition) {
        Intent intent = new Intent(activity, WallPaperActivity.class);
        intent.putExtra(SER_EXTRA_WP, wallPaper.toJson());
        Transition.putTransition(intent, transition);
        activity.startActivity(intent);
        transition.overrideOpenTransition(activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wallPaper = Model.fromJson(getIntent().getStringExtra(SER_EXTRA_WP), WallPaper.class);
        setContentView(R.layout.activity_wallpaper_detail);
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
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        WallPaperClipsAdapter wallPaperClipsAdapter = new WallPaperClipsAdapter(wallPaper);
        recyclerView.setAdapter(wallPaperClipsAdapter);

        Log.w("WP", "items=" + wallPaperClipsAdapter.getItemCount() + ",wp=" + wallPaper.toJson());

        buttonSetWallPaper.setOnClickListener(l -> {
            Toast.makeText(getApplicationContext(), "Set As Wallpaper", Toast.LENGTH_LONG);
        });
    }

}
