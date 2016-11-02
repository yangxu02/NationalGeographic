package com.linkx.wallpaper.di.components;

import android.content.Context;
import com.linkx.wallpaper.di.modules.ApplicationModule;
import dagger.Component;
import javax.inject.Singleton;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {
    Context context();
}
