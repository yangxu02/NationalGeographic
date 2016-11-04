package com.linkx.wallpaper;

import android.app.Application;
import com.facebook.stetho.Stetho;
import com.linkx.wallpaper.di.components.ApplicationComponent;
import com.linkx.wallpaper.di.components.DaggerApplicationComponent;
import com.linkx.wallpaper.di.modules.ApplicationModule;
import com.linkx.wallpaper.utils.IOUtil;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

public class AndroidApplication extends Application {

    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
        initializeInjector();
        initializeStetho();
        initializePicasso();
    }

    public void initializeInjector() {
        this.applicationComponent = DaggerApplicationComponent.builder()
            .applicationModule(new ApplicationModule(this))
            .build();
    }

    private void initializePicasso() {
        int maxCacheSize = 25 * 1024 * 1024;

        Picasso picasso =  new Picasso.Builder(this)
            .downloader(new OkHttpDownloader(IOUtil.picassoCacheDir(), maxCacheSize))
            .loggingEnabled(true)
            .build();
        picasso.setIndicatorsEnabled(true);
        Picasso.setSingletonInstance(picasso);
    }

    private void initializeStetho() {
        Stetho.initialize(
            Stetho.newInitializerBuilder(this)
                .enableDumpapp(
                    Stetho.defaultDumperPluginsProvider(this))
                .enableWebKitInspector(
                    Stetho.defaultInspectorModulesProvider(this))
                .build());
    }

    public ApplicationComponent getApplicationComponent() {
        return this.applicationComponent;
    }
}
