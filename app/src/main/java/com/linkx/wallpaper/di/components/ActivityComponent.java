package com.linkx.wallpaper.di.components;

import android.app.Activity;
import android.app.Application;
import com.linkx.wallpaper.di.ActivityScope;
import com.linkx.wallpaper.di.modules.ActivityModule;
import dagger.Component;

@ActivityScope
@Component(dependencies = Application.class, modules = ActivityModule.class)
public interface ActivityComponent {
    Activity activity();
}
