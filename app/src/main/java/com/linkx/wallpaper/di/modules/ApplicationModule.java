package com.linkx.wallpaper.di.modules;

import android.app.Application;
import android.content.Context;
import com.linkx.wallpaper.AndroidApplication;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module
public class ApplicationModule {
    private final Application application;

    public ApplicationModule(AndroidApplication application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Context provideApplicationContext() {
        return this.application;
    }
}
