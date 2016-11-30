package com.helloweather.app.util;

        import android.content.Context;

        import com.squareup.leakcanary.LeakCanary;
        import com.squareup.leakcanary.RefWatcher;

/**
 * Created by Administrator on 2016-11-30.
 */
public class ExampleApplication extends MyApplication {

    public static RefWatcher getRefWatcher(Context context) {
        ExampleApplication application = (ExampleApplication) context.getApplicationContext();
        return application.refWatcher;
    }

    private RefWatcher refWatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        refWatcher = LeakCanary.install(this);
    }
}
